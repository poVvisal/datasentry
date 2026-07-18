# Day 1 Architecture

## Request flow

For the Day 1 dataset metadata slice, the request path is:

`Client -> DatasetController -> DatasetService -> DatasetRepository -> PostgreSQL`

## Flow details

1. The client sends a request to `/api/v1/datasets` or `/api/v1/datasets/{id}`.
2. `DatasetController` owns the REST contract under `/api/v1`.
3. `CreateDatasetRequest` is validated at the web boundary using Bean Validation (`@Valid`).
4. `DatasetService` contains use-case logic and DTO/entity mapping orchestration.
5. `DatasetRepository` uses Spring Data JPA to persist and fetch `Dataset` entities.
6. PostgreSQL stores the data in the `datasets` table created by Flyway.

## Validation

Validation happens in two places:
- **Request DTO validation:** `CreateDatasetRequest` enforces required fields, name length, `text/csv`, and positive size.
- **Database constraints:** Flyway adds non-null, length, enum/status, and positive-size constraints.

This gives fast API feedback plus schema-level protection.

## Error handling

Global error handling is centralized in `shared/error/GlobalExceptionHandler`.

It converts validation failures, not-found errors, invalid path parameters, and unexpected exceptions into one JSON structure with:
- `timestamp`
- `status`
- `error`
- `message`
- `path`
- `correlationId`
- `fieldErrors` (when validation fails)

## Health semantics

Actuator exposes:
- `/actuator/health`
- `/actuator/health/liveness`
- `/actuator/health/readiness`
- `/actuator/info`
- `/actuator/metrics`

Liveness represents process health.

Readiness includes database reachability and is designed to map directly to future Kubernetes readiness probes.
