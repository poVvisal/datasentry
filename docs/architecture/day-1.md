# DataSentry — Day 1 Architecture

## Request Flow

Client -> DatasetController -> DatasetService -> DatasetRepository -> PostgreSQL

## Validation and Errors
- DTO validation happens at the web boundary with `@Valid` on request DTOs.
- Spring raises `MethodArgumentNotValidException` for invalid payloads.
- `GlobalExceptionHandler` converts validation, 404, type mismatch, and unknown
  failures into one consistent JSON `ApiError` response.

## Health Semantics
- Liveness means the process is running.
- Readiness means the app can reach PostgreSQL.
- These map cleanly to Kubernetes probes later.

## Notes
- DTOs are used at the web boundary; entities stay internal.
- Flyway owns schema creation.
- Hibernate validates the schema but never creates or updates it.