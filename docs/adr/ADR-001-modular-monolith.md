# ADR-001: Modular Monolith

- **Status:** Accepted
- **Date:** 2026-07-18

## Decision

DataSentry will start as a **modular monolith**: one repository, one shared codebase, one Spring Boot backend module today, and a future separate worker process built from the same repository later.

## Context

The Day 1 goal is a seven-day build that proves the core backend foundation: PostgreSQL persistence, Flyway migrations, health checks, structured errors, and one clean vertical slice for dataset metadata.

The platform will eventually support:
- CSV uploads
- validation profile management
- asynchronous validation runs
- quality reporting
- a future API process and worker process

Those future capabilities do not justify microservices on Day 1.

## Why this decision

A modular monolith gives us:
- **Fast delivery:** fewer moving parts, less deployment and local-dev friction.
- **Clear boundaries:** feature-based packages (`datasets`, `shared`, later `validation`, `runs`, `reports`) keep the design modular without distributed-system overhead.
- **Future flexibility:** the same repository can later produce two deployables, API and worker, when asynchronous processing arrives.
- **Lower cost:** local-first, open-source development with PostgreSQL only. No early infra tax.
- **Simpler transactions:** dataset metadata and future validation orchestration can evolve with strong consistency inside one codebase.

## Why microservices are rejected for this seven-day build

Microservices are intentionally rejected because they would add:
- service discovery, networking, and versioning concerns
- distributed observability requirements
- cross-service contract management
- container/orchestration overhead
- higher local development complexity
- slower iteration for a team still proving domain boundaries

Those costs do not buy meaningful value for the Day 1 scope.

## Consequences

- The codebase must preserve clear module boundaries even though it is one deployable today.
- Feature packages own their controller, service, repository, DTOs, and domain model.
- Shared cross-cutting concerns stay in `shared/` only when they are truly reusable.
- When background processing is introduced, the worker can be extracted into a separate runnable process from the same repository without forcing a rewrite.
