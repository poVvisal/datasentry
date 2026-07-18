# ADR-002: PostgreSQL and Flyway

- **Status:** Accepted
- **Date:** 2026-07-18

## Decision

DataSentry will use **PostgreSQL** as the primary local and future deployed relational database, and **Flyway** as the only schema migration mechanism.

## Context

The project direction requires:
- Java 21 + Spring Boot
- Spring Data JPA + Hibernate
- PostgreSQL
- environment parity between local development and later deployment
- a clean, reproducible database bootstrap from a fresh checkout

The Day 1 vertical slice persists dataset metadata that must behave consistently across environments.

## Why PostgreSQL

PostgreSQL is chosen because it provides:
- strong SQL support and mature tooling
- reliable UUID and timestamp support
- behavior that matches the future deployed environment
- excellent compatibility with Flyway, Hibernate, and Testcontainers
- zero cost for local development

## Why Flyway

Flyway is the single source of truth for schema changes because it gives us:
- deterministic, versioned schema evolution
- migration history visibility
- repeatable clean-checkout database setup
- safer collaboration than ad hoc Hibernate-generated updates

## Why H2 is rejected

H2 is rejected for Day 1 because it creates false confidence:
- SQL dialect behavior differs from PostgreSQL
- constraints, indexes, timestamp handling, and UUID behavior may diverge
- tests can pass locally while production-like PostgreSQL behavior fails later

Because DataSentry is database-sensitive, local behavior must match the real target database from the beginning.

## Why `hibernate.ddl-auto=update` is rejected

`ddl-auto=update` is rejected because it:
- mutates schema implicitly at application startup
- is not versioned or reviewable
- can drift between machines
- makes rollback and auditability harder

Hibernate may validate mappings against the schema, but it must not create or update it outside tests.

## Consequences

- Every schema change requires a new Flyway migration.
- Local startup assumes PostgreSQL is available.
- Integration tests use real PostgreSQL via Testcontainers where possible.
- The application uses `ddl-auto=validate` so mapping/schema mismatches fail fast.
