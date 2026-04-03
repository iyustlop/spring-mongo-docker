# --- FASE 1: Construcción (Build) ---
# Usamos una imagen de Maven con JDK 25 para compilar el proyecto
FROM maven:3.9.12 AS build

# Definimos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo pom.xml para descargar las dependencias primero (aprovecha el caché de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente y generamos el JAR (saltando los tests para ir más rápido)
COPY src ./src
RUN mvn clean package -DskipTests

# --- FASE 2: Ejecución (Runtime) ---
# Usamos una imagen ligera de JRE o JDK 25 para correr la app
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copiamos solo el archivo JAR generado en la fase anterior
# "build" es el alias que le pusimos a la primera etapa
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto típico de Spring Boot
EXPOSE 8080

# Comando para ejecutar el microservicio
ENTRYPOINT ["java", "-jar", "app.jar"]