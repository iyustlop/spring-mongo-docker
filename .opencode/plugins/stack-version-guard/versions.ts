import { readFile } from "node:fs/promises"
import { join } from "node:path"

const SPRING_METADATA_URL =
  "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-parent/maven-metadata.xml"
const ADOPTIUM_URL = "https://api.adoptium.net/v3/info/available_releases"
const STABLE_SEMVER = /^\d+\.\d+\.\d+$/

export type UpdateType = "patch" | "minor" | "major" | "lts" | "feature"

export type StackUpdate = {
  component: "spring-boot" | "java"
  current: string
  latest: string
  type: UpdateType
  automatic: boolean
}

export type StackReport = {
  checkedAt: string
  directory: string
  pom: string | null
  spring: {
    current: string | null
    latest: string | null
    latestForLine: string | null
  }
  java: {
    current: string | null
    latestLts: string | null
    latestFeature: string | null
  }
  updates: StackUpdate[]
  notes: string[]
  ok: boolean
}

export type PomVersions = {
  spring: string | null
  java: string | null
  compilerSource: string | null
  compilerTarget: string | null
}

const label = (update: StackUpdate) => (update.component === "spring-boot" ? "Spring Boot" : "Java")

const majorMinor = (version: string) => version.split(".").slice(0, 2).join(".")

const major = (version: string) => version.split(".")[0]

function compareVersions(a: string, b: string): number {
  const left = a.split(".").map((part) => Number.parseInt(part, 10) || 0)
  const right = b.split(".").map((part) => Number.parseInt(part, 10) || 0)
  const length = Math.max(left.length, right.length)

  for (let index = 0; index < length; index += 1) {
    const difference = (left[index] ?? 0) - (right[index] ?? 0)
    if (difference !== 0) return difference
  }

  return 0
}

export function parsePomVersions(xml: string): PomVersions {
  const parent = xml.match(/<parent>[\s\S]*?<\/parent>/)?.[0] ?? ""
  const isSpringParent = /<artifactId>\s*spring-boot-starter-parent\s*<\/artifactId>/.test(parent)

  return {
    spring: isSpringParent ? (parent.match(/<version>\s*([^<\s]+)\s*<\/version>/)?.[1] ?? null) : null,
    java: xml.match(/<java\.version>\s*([^<\s]+)\s*<\/java\.version>/)?.[1] ?? null,
    compilerSource: xml.match(/<maven\.compiler\.source>\s*([^<\s]+)\s*<\/maven\.compiler\.source>/)?.[1] ?? null,
    compilerTarget: xml.match(/<maven\.compiler\.target>\s*([^<\s]+)\s*<\/maven\.compiler\.target>/)?.[1] ?? null,
  }
}

async function fetchText(url: string): Promise<string | null> {
  try {
    const response = await fetch(url, { headers: { "user-agent": "opencode-stack-version-guard" } })
    if (!response.ok) return null
    return await response.text()
  } catch {
    return null
  }
}

export async function fetchSpringVersions(): Promise<string[]> {
  const xml = await fetchText(SPRING_METADATA_URL)
  if (!xml) return []

  return [...xml.matchAll(/<version>\s*([^<\s]+)\s*<\/version>/g)]
    .map((match) => match[1])
    .filter((version) => STABLE_SEMVER.test(version))
}

type AdoptiumResponse = {
  most_recent_lts?: number
  most_recent_feature_release?: number
}

export async function fetchJavaReleases(): Promise<{ lts: number | null; feature: number | null }> {
  try {
    const response = await fetch(ADOPTIUM_URL)
    if (!response.ok) return { lts: null, feature: null }

    const releases = (await response.json()) as AdoptiumResponse
    return {
      lts: typeof releases.most_recent_lts === "number" ? releases.most_recent_lts : null,
      feature:
        typeof releases.most_recent_feature_release === "number" ? releases.most_recent_feature_release : null,
    }
  } catch {
    return { lts: null, feature: null }
  }
}

export async function checkVersions(directory: string): Promise<StackReport> {
  let xml: string | null = null
  try {
    xml = await readFile(join(directory, "pom.xml"), "utf8")
  } catch {
    xml = null
  }

  const parsed = xml
    ? parsePomVersions(xml)
    : { spring: null, java: null, compilerSource: null, compilerTarget: null }

  const [springVersions, javaReleases] = await Promise.all([fetchSpringVersions(), fetchJavaReleases()])
  const sortedSpring = [...springVersions].sort(compareVersions)

  const springLatest = sortedSpring.at(-1) ?? null
  const springCurrent = parsed.spring
  const springLatestForLine = springCurrent
    ? (sortedSpring.filter((version) => majorMinor(version) === majorMinor(springCurrent)).at(-1) ?? null)
    : null

  const updates: StackUpdate[] = []
  const notes: string[] = []

  if (!xml) {
    notes.push(`No se encontró \`pom.xml\` en \`${directory}\`.`)
  }

  if (springCurrent && springLatest) {
    if (compareVersions(springCurrent, springLatest) < 0) {
      const type: UpdateType =
        majorMinor(springCurrent) === majorMinor(springLatest)
          ? "patch"
          : major(springCurrent) === major(springLatest)
            ? "minor"
            : "major"

      updates.push({
        component: "spring-boot",
        current: springCurrent,
        latest: springLatest,
        type,
        automatic: type === "patch",
      })

      if (type !== "patch" && springLatestForLine) {
        notes.push(
          `Última revisión de la línea ${majorMinor(springCurrent)}: ${springLatestForLine} (parche seguro dentro de la línea actual).`,
        )
      }
    }
  } else if (springCurrent && !springLatest) {
    notes.push("No se pudo consultar Maven Central para Spring Boot (revisa la conexión).")
  } else if (!springCurrent) {
    notes.push("No se detectó `spring-boot-starter-parent` en el `pom.xml`.")
  }

  if (springCurrent && !STABLE_SEMVER.test(springCurrent)) {
    notes.push(
      `La versión actual de Spring Boot (${springCurrent}) no es una release estable; confirma que sea intencional antes de actualizar.`,
    )
  }

  const javaCurrent = parsed.java ? Number.parseInt(parsed.java, 10) : null

  if (javaCurrent !== null && javaReleases.lts !== null) {
    if (javaCurrent < javaReleases.lts) {
      updates.push({
        component: "java",
        current: String(javaCurrent),
        latest: String(javaReleases.lts),
        type: "lts",
        automatic: false,
      })
    } else if (javaCurrent > javaReleases.lts) {
      notes.push(
        `Java ${javaCurrent} es una feature release (no LTS); la última LTS es ${javaReleases.lts}. Revisa que sea intencional para producción.`,
      )
    }

    if (
      javaReleases.feature !== null &&
      javaCurrent < javaReleases.feature &&
      javaReleases.feature > javaReleases.lts
    ) {
      notes.push(
        `Java ${javaReleases.feature} ya está disponible como feature release, pero no se recomienda para producción: mantente en LTS ${javaReleases.lts}.`,
      )
    }
  } else if (parsed.java && javaReleases.lts === null) {
    notes.push("No se pudo consultar Adoptium para las versiones de Java (revisa la conexión).")
  }

  if (parsed.compilerSource && javaCurrent !== null && parsed.compilerSource !== parsed.java) {
    notes.push(
      `\`maven.compiler.source\` (${parsed.compilerSource}) no coincide con \`<java.version>\` (${parsed.java}).`,
    )
  }
  if (parsed.compilerTarget && javaCurrent !== null && parsed.compilerTarget !== parsed.java) {
    notes.push(
      `\`maven.compiler.target\` (${parsed.compilerTarget}) no coincide con \`<java.version>\` (${parsed.java}).`,
    )
  }

  return {
    checkedAt: new Date().toISOString(),
    directory,
    pom: xml ? join(directory, "pom.xml") : null,
    spring: {
      current: springCurrent,
      latest: springLatest,
      latestForLine: springLatestForLine,
    },
    java: {
      current: parsed.java,
      latestLts: javaReleases.lts === null ? null : String(javaReleases.lts),
      latestFeature: javaReleases.feature === null ? null : String(javaReleases.feature),
    },
    updates,
    notes,
    ok: updates.length === 0,
  }
}

export function formatReport(report: StackReport): string {
  const lines: string[] = [
    `Revisión de versiones · ${report.checkedAt}`,
    `Spring Boot: ${report.spring.current ?? "desconocida"} (última estable: ${report.spring.latest ?? "desconocida"})`,
    `Java: ${report.java.current ?? "desconocida"} (última LTS: ${report.java.latestLts ?? "desconocida"}${
      report.java.latestFeature ? `, última feature: ${report.java.latestFeature}` : ""
    })`,
    "",
  ]

  if (report.updates.length === 0) {
    lines.push("Estado: al día. No hay actualizaciones recomendadas.")
  } else {
    lines.push("Actualizaciones recomendadas:")
    for (const update of report.updates) {
      lines.push(
        `- ${label(update)}: ${update.current} → ${update.latest} (${update.type}, ${
          update.automatic ? "automática" : "requiere confirmación"
        })`,
      )
    }
  }

  if (report.notes.length > 0) {
    lines.push("", "Notas:")
    for (const note of report.notes) lines.push(`- ${note}`)
  }

  lines.push("", "Política del proyecto:")
  lines.push("- Parches de Spring Boot (misma línea): se aplican automáticamente y se verifican con mvn.")
  lines.push("- Minor/major de Spring Boot y cambios de Java: requieren aprobación del usuario.")

  return lines.join("\n")
}

export function buildNudgeText(report: StackReport): string {
  const automatic = report.updates.filter((update) => update.automatic)
  const manual = report.updates.filter((update) => !update.automatic)
  const lines: string[] = [
    "🧭 stack-version-guard · revisión automática de versiones al iniciar OpenCode",
    "",
    "Detecciones:",
    ...report.updates.map(
      (update) =>
        `- ${label(update)}: ${update.current} → ${update.latest} (${update.type}, ${
          update.automatic ? "automática" : "requiere confirmación"
        })`,
    ),
  ]

  if (automatic.length > 0) {
    lines.push(
      "",
      "Hazlo ahora, sin esperar a que te lo pida el usuario:",
      "1. Actualiza en `pom.xml` solo la versión del parent `spring-boot-starter-parent`.",
      "2. Verifica con `mvn -q clean compile`; si falla, revierte el cambio y reporta el error.",
      "3. Ejecuta `mvn -q test` para descartar regresiones.",
      "4. Muestra `git diff pom.xml` y resume el resultado. No hagas commit ni push.",
    )
  }

  if (manual.length > 0) {
    lines.push(
      "",
      `Pide permiso antes de aplicar: ${manual.map((update) => `${label(update)} ${update.latest}`).join(", ")}.`,
    )
  }

  return lines.join("\n")
}
