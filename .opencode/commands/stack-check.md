---
description: Revisa Java y Spring Boot frente a la última versión estable y aplica los parches seguros
agent: backend-engineer
---

Revisa el stack y actúa según la política de versiones del proyecto.

1. Llama al tool `stack_versions` con `{ "refresh": true }`.
2. Aplica las actualizaciones marcadas como `automatic: true` en `pom.xml` (solo el número de versión del parent) y verifica con `mvn -q clean compile` seguido de `mvn -q test`.
3. Para las actualizaciones que requieren confirmación (minor/major o Java), explica el impacto y espera mi decisión antes de tocar nada.
4. Termina con un resumen: versiones antes/después, comandos ejecutados con su resultado y `git diff` de los archivos tocados.

Alcance adicional solicitado: $ARGUMENTS
