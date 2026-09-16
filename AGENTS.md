# AGENTS.md

- **Stack:** Java 25, Spring Boot 3.5.10, Spring Data MongoDB, Lombok, MapStruct (1.6.3), Maven.
- **Build / Verification Commands:**
  - Compile & verify: `./mvnw clean compile`
  - Run tests (includes `*Test.java` and `*IT.java`): `./mvnw test`
  - Package boot jar: `./mvnw clean package`
- **Architecture & Source Paths:**
  - Main Application: `com.olivar.springbootmongodbdocker.SpringbootMongodbDockerApplication`
  - Domain/Infrastructure/Controllers under `src/main/java/com/olivar/springbootmongodbdocker/`
  - Additional test source folders (`src/test/it`, `src/test/at`) are registered via `build-helper-maven-plugin`.
- **Environment & Gotchas:**
  - Required environment variables for full run/deployment: `MONGODB_USER`, `MONGODB_PASSWORD`, `MONGODB_DATABASE`.
  - Docker Compose & Testcontainers are configured for MongoDB & PostgreSQL dependencies.
