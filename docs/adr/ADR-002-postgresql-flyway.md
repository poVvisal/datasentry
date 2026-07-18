# ADR-002: PostgreSQL + Flyway for Schema Management

**Date:** 2026-07-18  
**Status:** Accepted

## Decision
Use PostgreSQL as the sole database and Flyway as the exclusive schema change
mechanism. Hibernate `ddl-auto` is set to `validate` in all non-test profiles.

## Why Not H2
1. Dialect divergence: PostgreSQL-specific features such as `TIMESTAMPTZ`,
   `gen_random_uuid()`, and strict constraints do not exist in H2.
2. False confidence: schema bugs and missing indexes stay hidden if tests run
   against H2.
3. Testcontainers removes the need: a real PostgreSQL container starts quickly
   enough for local and CI runs.

## Why Not `ddl-auto: update`
1. Non-auditable: no record of what changed or when.
2. Unsafe: Hibernate does not manage schema intent as explicitly as Flyway.
3. Production risk: automatic schema changes can drift from the intended model.

## Flyway Convention
- Migrations live in `src/main/resources/db/migration/`.
- Naming: `V{version}__{description}.sql`.
- Migrations are append-only.
- `flyway_schema_history` is the source of truth.