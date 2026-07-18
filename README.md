# DataSentry

## Requirements
- Java 21
- Docker (for PostgreSQL / Testcontainers)

## Local Development
1. Start DB: docker run --name ds-pg -p 5432:5432 -e POSTGRES_PASSWORD=postgres -d postgres:16
2. Run app: ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
3. Run tests: ./mvnw test
"@

    ".gitignore" = @"
target/
!.mvn/wrapper/maven-wrapper.jar
log/
*.log
**/*.log
.env
.idea/
*.iml
.vscode/
