# Spring Mongo Demo Project<
## Variables de entorno necesarias
MONGODB_USER
MONGODB_PASSWORD
MONGODB_DATABASE

## Ejemplos de llamadas a la API de Manufacturer

curl http://localhost:8080/manufacturer

curl -X POST http://localhost:8090/manufacturer \
-H 'Content-Type: application/json' \
-d '{"id": 1, "name":"Ferrari","code":"F01"}'

<h2>Docker Compose testeado en varias maquinas.</h2>

## Kubernetes with minikube

Start minikube 

> minikube start

To load the env variables use the following command

> kubectl create configmap env-variables --from-env-file=.env

Previously you shall create a file with the env variables.

To create the deployment run the following command

> kubectl create -f deployment.yml

To controll the deployment:

> minikube dashboard

