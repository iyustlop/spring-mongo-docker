# Spring Mongo Docker Demo

Aplicación REST de ejemplo construida con **Spring Boot** y **MongoDB**. El proyecto sirve como demo de integración de una API de fabricantes con persistencia en MongoDB, empaquetado con Docker, monitorización con Prometheus/Grafana y despliegue en Kubernetes.

## Stack

| Tecnología | Versión / uso |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.x (`spring-boot-starter-parent`) |
| Spring Data MongoDB | Persistencia de documentos |
| Spring Boot Actuator | Health checks y métricas |
| Micrometer + Prometheus | Exportación de métricas |
| MapStruct | 1.6.3, mapeo entre entidades y DTOs |
| Lombok | Reducción de código boilerplate |
| Maven | 3.9+ (no se incluye wrapper `mvnw`) |
| JUnit 5 / Mockito / AssertJ | Tests unitarios y de integración |
| Testcontainers | 2.x, MongoDB real para los tests de integración |
| Docker / Docker Compose | Imagen de la aplicación y stack local |
| Kubernetes (minikube) | Despliegue de ejemplo |

## Estructura del proyecto

```text
.
├── src/main/java/com/olivar/springbootmongodbdocker
│   ├── SpringbootMongodbDockerApplication.java   # Punto de entrada
│   ├── dao/ManufacturerRepo.java                 # Repositorio Spring Data MongoDB
│   ├── domain/model/Manufacturer.java            # Documento MongoDB
│   ├── service/ManufacturerService.java          # Lógica de negocio
│   └── infrastructure/inboud/rest/core
│       ├── ManufacturerController.java           # API REST /manufacturer
│       ├── dto/                                  # ManufacturerRequest / ManufacturerResponse
│       └── mapper/ManufacturerResponseMapper.java
├── src/main/resources
│   ├── application.yml                           # Configuración de la aplicación
│   └── prometheus.yml                            # Configuración de scrape de Prometheus
├── src/test/java                                 # Tests unitarios (*Test.java)
├── src/test/it                                   # Tests de integración (*IT.java)
├── Dockerfile                                    # Imagen multi-stage de producción
├── DockerfileDev                                 # Imagen del devcontainer
├── docker-compose.yml                            # Stack completo (app + MongoDB + Prometheus + Grafana)
├── compose.yml                                   # Compose usado por el devcontainer
├── deployment.yml / ingress.yml                  # Despliegue y exposición en Kubernetes
└── Jenkinsfile                                   # Pipeline de CI
```

## Requisitos

- **JDK 25** (`java -version` debe mostrar 25).
- **Maven 3.9+** instalado en el sistema.
- **Docker** y **Docker Compose** para los tests de integración, la imagen y el stack completo.
- Opcional: **minikube** y **kubectl** para el despliegue en Kubernetes.

> El proyecto no incluye Maven Wrapper, por lo que los comandos usan `mvn` del sistema.

## Configuración

La configuración principal está en `src/main/resources/application.yml`:

| Propiedad | Valor por defecto |
|---|---|
| `spring.data.mongodb.uri` | `mongodb://localhost:27017` |
| `spring.data.mongodb.database` | `elolivar` |
| `server.port` | `8080` |
| `management.endpoints.web.exposure.include` | `*` (todos los endpoints de Actuator) |

### Variables de entorno

`docker-compose.yml` y el despliegue de Kubernetes usan un archivo `.env` con las credenciales de inicialización de MongoDB:

```dotenv
MONGODB_USER=admin
MONGODB_PASSWORD=admin
MONGODB_DATABASE=admin
```

La conexión que usa la aplicación se define en `application.yml`. Para sobreescribirla sin modificar código, utiliza la variable estándar de Spring Boot:

```bash
export SPRING_DATA_MONGODB_URI="mongodb://admin:admin@localhost:27017/elolivar?authSource=admin"
```

## Puesta en marcha

### Opción 1: ejecución local

1. Arranca MongoDB (la configuración por defecto espera MongoDB en `localhost:27017` sin autenticación):

   ```bash
   docker compose -f compose.yml up -d mongodb
   ```

2. Arranca la aplicación:

   ```bash
   mvn spring-boot:run
   ```

3. La API queda disponible en `http://localhost:8080`.

> La dependencia `spring-boot-docker-compose` permite que Spring Boot levante automáticamente el servicio `mongodb` del archivo `compose.yml` si Docker está disponible.

### Opción 2: stack completo con Docker Compose

Crea el archivo `.env` con las variables indicadas arriba y ejecuta:

```bash
docker compose -f docker-compose.yml up -d --build
```

Servicios y puertos:

| Servicio | URL / puerto |
|---|---|
| API Spring Boot | http://localhost:8090 |
| MongoDB | `localhost:27017` |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |

> **Importante:** en el directorio existen `compose.yml` (devcontainer) y `docker-compose.yml` (stack completo). Docker Compose elige `compose.yml` por defecto, así que indica siempre el archivo con `-f docker-compose.yml`.
>
> El servicio `spring-mongo-demo` define `MONGODB_USERNAME`, `MONGODB_PASSWORD` y `MONGODB_DATABASE`, pero la aplicación lee la conexión de `application.yml`. Si ejecutas la app dentro de Compose, añade `SPRING_DATA_MONGODB_URI=mongodb://<usuario>:<password>@mongodb:27017/<base-de-datos>?authSource=admin` para que apunte al contenedor `mongodb`.

### Opción 3: JAR empaquetado

```bash
mvn clean package
java -jar target/springboot-mongodb-docker.jar
```

`mvn clean package` ejecuta también los tests de integración y requiere Docker. Para empaquetar sin tests:

```bash
mvn clean package -DskipTests
```

### Opción 4: devcontainer

Existe una configuración local de devcontainer en `.devcontainer/devcontainer.json` (no versionada en Git) que usa `compose.yml` y `DockerfileDev`. Desde VS Code se puede abrir con **Dev Containers: Reopen in Container**; al crearse ejecuta `mvn clean install -DskipTests`.

> `DockerfileDev` está basado en JDK 17, mientras que el proyecto compila con Java 25. Para usar el devcontainer sin errores de compilación hay que actualizar la imagen base a una con JDK 25.

## API REST

La URL base es `http://localhost:8080` en local y `http://localhost:8090` con `docker-compose.yml`.

| Método | Ruta | Descripción | Cuerpo |
|---|---|---|---|
| `POST` | `/manufacturer` | Crea un fabricante | `{"name": "...", "code": "..."}` |
| `GET` | `/manufacturer` | Devuelve todos los fabricantes | — |

### Crear un fabricante

```bash
curl -X POST http://localhost:8080/manufacturer \
  -H 'Content-Type: application/json' \
  -d '{"name":"Ferrari","code":"F01"}'
```

Respuesta:

```json
{
  "id": "65f1c2a9e4b0a1b2c3d4e5f6",
  "name": "Ferrari",
  "code": "F01"
}
```

### Listar fabricantes

```bash
curl http://localhost:8080/manufacturer
```

Respuesta:

```json
[
  {
    "id": "65f1c2a9e4b0a1b2c3d4e5f6",
    "name": "Ferrari",
    "code": "F01"
  }
]
```

### Actuator

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/prometheus
```

> Actualmente solo están expuestos `GET /manufacturer` y `POST /manufacturer`. El servicio contiene operaciones de consulta por código y actualización, pero no tienen endpoint REST asociado.

## Tests

```bash
# Todos los tests (unitarios + integración). Requiere Docker para Testcontainers.
mvn test

# Solo el test unitario del controlador
mvn test -Dtest=ManufacturerControllerTest

# Solo un test de integración
mvn test -Dtest=ManufacturerIT
```

- `ManufacturerControllerTest`: test unitario del controlador con MockMvc y Mockito.
- `ManufacturerIT`: test de integración de la API REST contra un MongoDB real levantado con Testcontainers (`mongo:7.0`).
- `SpringMongodbDockerApplicationIT`: test de integración del contexto y la persistencia.

Los tests de integración pueden tardar más la primera vez porque descargan la imagen de MongoDB.

## Observabilidad

- **Actuator**: `http://localhost:8080/actuator` expone todos los endpoints, incluido `/actuator/prometheus`.
- **Prometheus**: se configura con `src/main/resources/prometheus.yml` y scrapea `/actuator/prometheus`.
- **Grafana**: disponible en `http://localhost:3000` cuando se levanta `docker-compose.yml`.

> En `prometheus.yml` el target está fijado a `192.168.1.105:8080`. Ajústalo a `localhost:8080` o al nombre del servicio (`spring-mongo-demo:8080`) según dónde se ejecute. El directorio `grafana/provisioning/datasources` referenciado en `docker-compose.yml` no está incluido en el repositorio, por lo que Grafana arranca sin datasource preconfigurado.

## Imagen Docker

```bash
docker build -t iyustlop/spring-mongo-demo:0.0.2 .
docker run --rm -p 8090:8080 \
  -e SPRING_DATA_MONGODB_URI="mongodb://host.docker.internal:27017/elolivar" \
  iyustlop/spring-mongo-demo:0.0.2
```

El `Dockerfile` es multi-stage: compila con Maven y ejecuta el JAR sobre `eclipse-temurin:25-jre-alpine`.

## Kubernetes con minikube

1. Arranca minikube:

   ```bash
   minikube start
   ```

2. Crea el namespace y el ConfigMap con las variables de entorno:

   ```bash
   kubectl create namespace 100-avisos
   kubectl create configmap env-variables --from-env-file=.env -n 100-avisos
   ```

3. Despliega la aplicación:

   ```bash
   kubectl apply -f deployment.yml
   kubectl get pods -n 100-avisos
   ```

4. Expón el servicio:

   ```bash
   minikube service spring-demo-service -n 100-avisos
   ```

   O mediante port-forward:

   ```bash
   kubectl port-forward svc/spring-demo-service 8080:8080 -n 100-avisos
   ```

5. Opcionalmente, activa el Ingress:

   ```bash
   minikube addons enable ingress
   kubectl apply -f ingress.yml
   ```

> `deployment.yml` usa la imagen `iyustlop/spring-mongo-demo:0.0.1`, mientras que `docker-compose.yml` usa `0.0.2`. Construye y publica la etiqueta correspondiente o actualiza el YAML antes de desplegar.

## CI/CD con Jenkins

El `Jenkinsfile` define tres etapas:

1. **Build Maven**: `mvn clean install -DskipTests`.
2. **Build Docker image**: construye `iyustlop/spring-mongo-demo:0.0.1`.
3. **Push image to Docker Hub**: publica la imagen usando la credencial `dockerhub-pwd`.

El pipeline requiere la herramienta Maven configurada en Jenkins con el nombre `maven_3_8_6`.

## Notas

- No hay Maven Wrapper; usa Maven 3.9+ del sistema.
- La compilación requiere JDK 25; otras versiones pueden fallar con Lombok/MapStruct.
- Solo se implementan los endpoints `GET /manufacturer` y `POST /manufacturer`.
- Los datos de MongoDB del stack Compose se persisten en `./mongodb`, y los de Prometheus en `./prometheus_data` (ambos ignorados por Git).
