# Backend Guidelines

Use this file for backend-specific rules. Keep backend details here instead of expanding the root `AGENTS.md`.

## Scope
- Spring Boot backend in `backend/src/main/java`
- Config in `backend/src/main/resources`
- Tests in `backend/src/test/java`
- Current packages live under `com.silkycoders1.jsystemssilkycodders1`

## Current Backend Shape
- `config/` contains application wiring and OpenRouter properties
- `agui/sdk/` contains the app-side AG-UI bridge and SSE controller
- `agui/template/` contains the current template agent executor and tools
- `JSystemsSilkyCodders1Application.java` is the application entry point

## Architecture Notes
- This backend is the app integration layer around LangGraph4j, Spring AI, and the AG-UI Java SDK.
- It currently uses Spring Boot web plus Reactor streaming, not a separate custom AG-UI protocol implementation inside the repo root and not the older example structure.
- The frontend talks to the backend through the frontend route, which then reaches the backend SSE endpoint. Preserve that streaming contract when changing agent or controller code.
- OpenRouter-compatible model configuration is centralized in `config/OpenRouterProperties.java` and `config/ApplicationConfig.java`. Keep provider wiring there instead of scattering it across the codebase.
- Local data should follow the project PostgreSQL setup in Docker under WSL. Keep backend persistence aligned with that runtime model.

## Structure To Grow Toward
As backend features grow, keep responsibilities separated:
- `config/` for Spring wiring and provider configuration
- `agui/` for AG-UI transport and adapter concerns
- feature-focused packages for business logic, domain services, persistence, and API models
- test packages mirrored under `src/test/java`

Do not keep expanding template classes into a monolith. Extract reusable domain logic and integrations into focused classes as soon as the code stops being template-sized.
For persistence work, add migrations and seed updates as part of the same change instead of hiding schema setup in ad hoc manual steps.

## Backend Rules
- Use tabs in Java files.
- Use `PascalCase` for classes and keep packages under `com.silkycoders1.jsystemssilkycodders1`.
- Prefer constructor injection and explicit configuration over hidden static state.
- Keep controllers thin. Mapping, orchestration, model/provider logic, and domain rules should live outside controller classes.
- Keep provider-specific code behind configuration or adapter boundaries so it can change without spreading through the app.
- Do not call live AI providers in tests. Mock or stub model behavior.
- When changing streamed events or tool/approval flow, verify the frontend contract still holds.
- Do not introduce H2, SQLite, or file-based local database defaults for backend development. Use the project PostgreSQL path.

## Testing and Validation
- Backend changes need automated tests for success paths, edge cases, and error handling.
- If you change agent execution, model wiring, serialization, or SSE behavior, add focused tests for that contract rather than only context-load coverage.
- Prefer fast, isolated tests for pure logic and focused Spring tests only where framework wiring matters.
- Check current docs before using new Spring Boot, Spring AI, LangGraph4j, Reactor, or AG-UI APIs.

## Maintenance Rule
- When a backend mistake or missing rule becomes clear, update this file or a more specific nested backend `AGENTS.md` instead of adding more general guidance to the root file.
