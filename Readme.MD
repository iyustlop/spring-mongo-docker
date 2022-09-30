<h1>Spring Mongo Demo Project</h1>
<h2>Variables de entorno necesarias</h2>
MONGODB_USER
MONGODB_PASSWORD
MONGODB_DATABASE

<h2>Ejemplos de llamadas a la API de Manufacturer</h2>

curl http://localhost:8080/manufacturer

curl -X POST http://localhost:8090/manufacturer \
-H 'Content-Type: application/json' \
-d '{"id": 1, "name":"Ferrari","code":"F01"}'

<h2>Docker Compose testeado en varias maquinas.</h2>

<h2>Kubernetes</h2>

To load the env variables use the following command

kubectl create configmap env-variables --from-env-file=.env

Previously you shall create a file with the env variables.



