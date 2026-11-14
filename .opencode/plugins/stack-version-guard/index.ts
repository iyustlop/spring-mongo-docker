import { Plugin } from "@opencode/plugin"
import { buildNudgeText, checkVersions, formatReport, type StackReport } from "./versions"

/**
 * Revisa Spring Boot y Java al arrancar OpenCode.
 *
 * - Registra el tool `stack_versions` para que el agente pueda consultar versiones cuando quiera.
 * - En la primera sesión de nivel superior tras arrancar el servidor, comprueba versiones y, si hay
 *   actualizaciones, inyecta un mensaje para que el agente aplique las seguras y verifique.
 *
 * Desactiva el aviso automático con `OPENCODE_STACK_AUTOUPDATE=0` (el tool sigue disponible).
 */

const CACHE_TTL_MS = 10 * 60 * 1000

const normalize = (path: string) => path.replace(/\/+$/, "")

export default Plugin.define({
  id: "stack-version-guard",

  async setup(ctx) {
    const directory = ctx.location.directory
    const autoNudge = process.env.OPENCODE_STACK_AUTOUPDATE !== "0"

    let cache: { at: number; report: StackReport } | null = null

    const getReport = async (refresh: boolean): Promise<StackReport> => {
      const now = Date.now()
      if (!refresh && cache && now - cache.at < CACHE_TTL_MS) return cache.report

      const report = await checkVersions(directory)
      cache = { at: now, report }
      return report
    }

    await ctx.tool.transform((editor) => {
      editor.add({
        name: "stack_versions",
        description:
          "Compara la versión de Spring Boot y Java del pom.xml con la última versión estable (Maven Central y Adoptium) y devuelve las actualizaciones recomendadas y su política de aplicación.",
        input: {
          type: "object",
          properties: {
            refresh: {
              type: "boolean",
              description: "Ignora la caché de 10 minutos y vuelve a consultar Maven Central y Adoptium.",
            },
          },
          additionalProperties: false,
        },
        execute: async (input) => {
          const refresh = (input as { refresh?: boolean } | undefined)?.refresh === true
          const report = await getReport(refresh)
          return { content: formatReport(report) }
        },
      })
    })

    if (!autoNudge) return

    const controller = new AbortController()
    let checkedThisRun = false

    void (async () => {
      try {
        for await (const event of ctx.event.subscribe({
          types: ["session.created"],
          signal: controller.signal,
        })) {
          if (checkedThisRun) continue

          const data = event.data as {
            sessionID: string
            parentID?: string
            location?: { directory?: string }
          }

          if (data.parentID) continue
          if (data.location?.directory && normalize(data.location.directory) !== normalize(directory)) continue

          try {
            const report = await getReport(true)
            checkedThisRun = true
            if (report.updates.length === 0) continue
            await ctx.session.prompt({ sessionID: data.sessionID, text: buildNudgeText(report) })
          } catch (error) {
            console.error("[stack-version-guard] no se pudo avisar de la actualización:", error)
          }
        }
      } catch (error) {
        if (!controller.signal.aborted) {
          console.error("[stack-version-guard] suscripción de eventos interrumpida:", error)
        }
      }
    })()

    return () => controller.abort()
  },
})
