# DataSentry

DataSentry is a data-quality operations platform in progress. Day 1 delivers a runnable Spring Boot 3 + Java 21 backend foundation with PostgreSQL, Flyway, Actuator health checks, structured errors, and one vertical slice for dataset metadata.

## Repository layout

```text
datasentry/
├── backend/
│   ├── pom.xml
│   └── src/
├── docs/
├── README.md
└── .gitignore
```

## Day 1 scope

Implemented today:
- Modular monolith backend foundation
- PostgreSQL-only persistence (no H2)
- Flyway baseline migration
- Dataset metadata API
- Consistent JSON error handling with correlation IDs
- Actuator health/info/metrics exposure
- Integration tests with Testcontainers PostgreSQL

Intentionally not included today:
- Authentication/JWT
- CSV file upload
- MinIO/S3
- Redis / queues / async worker
- Frontend
- Docker / Kubernetes / Jenkins / Terraform / SonarQube / Trivy / Prometheus / Grafana
- Validation-rule engine

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 16+ running locally
- Docker (recommended for Testcontainers-based integration tests)

## Local PostgreSQL setup

Create a local database and user (example using `psql`):

```bash
psql -U postgres
```

Then run:

```sql
CREATE ROLE datasentry WITH LOGIN PASSWORD 'change_me_locally';
CREATE DATABASE datasentry OWNER datasentry;
```

## Environment variables

Copy `.env.example` to `.env` for your shell tooling if you want, but do not commit `.env`.

Required environment variables (or rely on defaults):

```bash
export DB_URL=jdbc:postgresql://localhost:5432/datasentry
export DB_USER=datasentry
export DB_PASS=change_me_locally
export SPRING_PROFILES_ACTIVE=local
```

## Run the API

From the repository root:

```bash
cd backend
mvn spring-boot:run
```

Or build then run:

```bash
cd backend
mvn clean package
java -jar target/datasentry-backend-0.1.0-SNAPSHOT.jar
```

## Execute tests

### Preferred: Testcontainers PostgreSQL

Requires Docker running locally.

```bash
cd backend
mvn test
```

### Fallback: local PostgreSQL only

If Docker/Testcontainers is unavailable, keep the test profile design as-is and run the application manually against local PostgreSQL for functional verification. For true automated integration tests, Docker is strongly recommended because the current test suite expects Testcontainers.

## Spotless formatting/linting

Check formatting during verify:

```bash
cd backend
mvn verify
```

Apply formatting automatically:

```bash
cd backend
mvn spotless:apply
```

## API endpoints

### POST `/api/v1/datasets`

Create dataset metadata.

Example request:

```bash
curl -i -X POST http://localhost:8080/api/v1/datasets \
  -H 'Content-Type: application/json' \
  -H 'X-Correlation-Id: demo-request-1' \
  -d '{
    "name": "Customers July 2026",
    "originalFilename": "customers.csv",
    "contentType": "text/csv",
    "sizeBytes": 2048,
    "storageKey": "uploads/customers-july-2026.csv"
  }'
```

Expected: `201 Created`

### GET `/api/v1/datasets`

List datasets in newest-first order.

```bash
curl -i http://localhost:8080/api/v1/datasets
```

Expected: `200 OK`

### GET `/api/v1/datasets/{id}`

Fetch one dataset by UUID.

```bash
curl -i http://localhost:8080/api/v1/datasets/REPLACE_WITH_UUID
```

Expected: `200 OK` or standardized `404 Not Found`

## Error response shape

Example validation error:

```json
{
  "timestamp": "2026-07-18T12:00:00Z",
  "status": 400,
  "error": "VALIDATION_FAILED",
  "message": "name name must be between 3 and 120 characters",
  "path": "/api/v1/datasets",
  "correlationId": "demo-request-1",
  "fieldErrors": [
    {
      "field": "name",
      "message": "name must be between 3 and 120 characters"
    }
  ]
}
```

## Verify health endpoints

```bash
curl -i http://localhost:8080/actuator/health
curl -i http://localhost:8080/actuator/health/liveness
curl -i http://localhost:8080/actuator/health/readiness
curl -i http://localhost:8080/actuator/info
curl -i http://localhost:8080/actuator/metrics
```

Readiness will report database reachability; this is designed to map later to Kubernetes readiness probes.

## Verify Flyway migration status

On application startup, Flyway logs applied migrations. You can also inspect the schema history table directly:

```bash
psql -U datasentry -d datasentry -c "SELECT installed_rank, version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

Inspect the application table:

```bash
psql -U datasentry -d datasentry -c "\d+ datasets"
```

## Day 1 architecture decisions

- Modular monolith, not microservices
- PostgreSQL only, no H2
- Flyway owns schema changes
- UUID primary keys
- UTC timestamps
- DTOs at the web boundary
- Feature-first packaging
- Distinct liveness/readiness semantics

See:
- `docs/adr/ADR-001-modular-monolith.md`
- `docs/adr/ADR-002-postgresql-flyway.md`
- `docs/architecture/day-1.md`

## Suggested commit plan

1. `chore: bootstrap Spring Boot project`
2. `feat: add PostgreSQL and Flyway baseline`
3. `feat: add dataset metadata API`
4. `test: cover dataset API integration paths`
5. `docs: add ADRs and Day 1 runbook`
