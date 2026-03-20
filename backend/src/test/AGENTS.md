# Backend Test Guidelines

Use this file for backend test-specific rules. Keep test implementation details here instead of expanding `backend/AGENTS.md` or the root `AGENTS.md`.

## Scope
- JUnit 5 via `spring-boot-starter-test`
- Spring Boot test support for wiring and integration tests
- Mockito for mocking external collaborators
- AssertJ-style assertions where available
- PostgreSQL-backed integration tests aligned with the project runtime

## Test Location and Naming
- Keep test classes under `backend/src/test/java` mirroring production packages.
- Use the `*Tests` suffix.
- Put shared test helpers under `backend/src/test/java` only when they are reused by multiple tests.

## Choose the Right Test Type
- Use plain unit tests for pure logic, mapping, validation, and adapter behavior.
- Use focused Spring tests when configuration, serialization, HTTP wiring, or bean setup matters.
- Use integration tests for AG-UI streaming, provider wiring, repository behavior, and database-backed flows.

Do not overuse full context tests when a smaller test would cover the behavior more clearly.

## PostgreSQL Rule
- This project uses PostgreSQL in Docker under WSL, not H2, SQLite, or in-memory/file databases.
- Integration tests must stay aligned with that direction.
- Reuse the real schema/seed approach from the repo instead of inventing a separate fake database model.
- Current seed data starts in `docker/postgres/init/01-clients.sql`. Keep future tests, migrations, and fixtures compatible with that setup.
- As persistence grows, prefer migration-driven schema setup plus explicit seed data for tests.

## Backend Test Rules
- Mock AI providers and other external network boundaries. Do not call live OpenRouter or other model APIs in tests.
- Do not mock away the behavior you are trying to verify. For controller or streaming tests, keep the relevant backend flow real and mock only the true external boundary.
- When testing streamed AG-UI behavior, assert meaningful event order and payload content, not only that "something was returned".
- Add error-path tests for serialization failures, provider failures, missing config, and invalid input when those paths matter to the change.
- If a change affects database behavior, test against PostgreSQL-compatible behavior and keep seed/migration expectations explicit.

## Implementation Workflow
- Start by deciding whether the change needs a unit test, Spring test, integration test, or more than one.
- If the task adds persistence, update migrations and seed data together with the tests.
- If the task changes AG-UI events or SSE output, add tests that prove the frontend contract still holds.
- Use current library docs before relying on new Spring Boot, Spring AI, LangGraph4j, Reactor, Mockito, or PostgreSQL testing APIs.

## Maintenance Rule
- When a backend test mistake or missing rule becomes clear, update this file or a more specific nested `AGENTS.md` instead of adding more general guidance elsewhere.
