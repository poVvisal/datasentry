# ADR-001: Modular Monolith Architecture

**Date:** 2026-07-18  
**Status:** Accepted

## Decision
DataSentry is built as a modular monolith — a single deployable JAR organized
into feature-based packages (`datasets/`, future `validations/`) with explicit
layer boundaries enforced by convention.

The API server and future background worker share one Maven module and Git
repository but will be launched as separate JVM processes once the worker is
introduced on Day 4.

## Why Not Microservices

| Factor | Modular Monolith | Microservices |
|--------|-----------------|---------------|
| Local dev startup | `./mvnw spring-boot:run` | Docker Compose + discovery overhead |
| Refactor cost | IDE-safe, single codebase | Cross-service contract changes |
| Domain confidence | Low on Day 1 | Must be proven before extraction |
| Operational overhead | One process | Discovery, tracing, distributed config |
| Team size | 1 developer | Unjustified |

Microservices are explicitly rejected for this seven-day build because the
boundaries are not yet proven. Extracting too early creates wrong seams that
are expensive to fix across service contracts. A modular monolith can be split
later with confidence once the domain is stable.

## Consequences
- Feature packages must not import each other's internal types directly.
- The worker will be a separate Spring Boot `main` class in the same repo.
- This decision is revisited after Day 7 when the domain is proven.