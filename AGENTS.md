# AGENTS.md

- **Stack:** Java 25, Spring Boot 4.1.0, Spring Data MongoDB, Lombok, MapStruct (1.6.3), Maven.
- **Build / Verification Commands:** (no Maven wrapper present — use system `mvn`)
  - Compile & verify: `mvn clean compile`
  - Run tests (includes `*Test.java` and `*IT.java`): `mvn test`
  - Package boot jar: `mvn clean package`
- **Architecture & Source Paths:**
  - Main Application: `com.olivar.springbootmongodbdocker.SpringbootMongodbDockerApplication`
  - Domain/Infrastructure/Controllers under `src/main/java/com/olivar/springbootmongodbdocker/`
  - Additional test source folders (`src/test/it`, `src/test/at`) are registered via `build-helper-maven-plugin`.
- **Environment & Gotchas:**
  - Required environment variables for full run/deployment: `MONGODB_USER`, `MONGODB_PASSWORD`, `MONGODB_DATABASE`.
  - Docker Compose & Testcontainers are configured for MongoDB & PostgreSQL dependencies.
  - Spring Boot 4.1 manages Testcontainers 2.x, which renamed artifact IDs: use `testcontainers-junit-jupiter`, `testcontainers-mongodb`, `testcontainers-postgresql` (old names no longer resolve).
  - Integration tests (`src/test/it`) use `TestRestTemplate`, which in Boot 4 lives in `org.springframework.boot.resttestclient` and requires test deps `spring-boot-resttestclient` + `spring-boot-restclient` + `@AutoConfigureTestRestTemplate`.
