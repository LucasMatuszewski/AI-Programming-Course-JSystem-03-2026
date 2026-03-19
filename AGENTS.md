# Repository Guidelines

## Project Structure & Module Organization
- `backend/`: Spring Boot 3.5 / Java 21 app. Main code lives in `src/main/java`, config in `src/main/resources`, tests in `src/test/java`.
- `frontend/`: Next.js 15 + React 18 UI. App Router code lives in `src/app`.
- `scripts/`: Windows PowerShell and Bash helpers for setup and local startup.
- `docker/`: PostgreSQL init assets used by `compose.yaml`.
- `docs/`, `materials/`, `prompts/`, `exercises/`: course content, not runtime code.
- `ag-ui/`: Git submodule with AG-UI SDK sources. Treat it as external unless a task explicitly targets it.

## Build, Test, and Development Commands
- `powershell -File .\scripts\run-copilot-template.ps1`: one-command Windows setup and start.
- `bash ./scripts/run-copilot-template.sh`: same flow for Bash environments.
- `powershell -File .\scripts\setup-postgres.ps1`: start local PostgreSQL on Windows/WSL.
- `cd backend; .\mvnw.cmd test`: run backend tests.
- `cd backend; .\mvnw.cmd spring-boot:run`: start backend only.
- `cd frontend; npm install`: install frontend dependencies.
- `cd frontend; npm run dev`: start Next.js on `localhost:3000`.
- `cd frontend; npm run build`: production build check.
- `cd frontend; npm run lint`: run ESLint with Next.js rules.

## Coding Style & Naming Conventions
- Java uses tabs, `PascalCase` class names, and package names under `com.silkycoders1.jsystemssilkycodders1`.
- TypeScript/React uses 2-space indentation, `PascalCase` components, and lower camel case for variables/functions.
- Keep App Router files conventional: `page.tsx`, `layout.tsx`, `route.ts`.
- Use ESLint in `frontend/` as the main formatting/linting guard. No separate Prettier config is checked in.

## Testing Guidelines
- Backend tests use JUnit 5 via `spring-boot-starter-test`.
- Keep Java test classes next to the matching package under `backend/src/test/java` and suffix them with `Tests`.
- On Frontend Run `npm run lint` and `npm run build` for UI changes. Then run `npm run test`.

## Definition of Done
- Apply these checks to small, granular changes so every step has a working state and a clear Git history that is easy to review or revert.
- For every new functionality, use TDD. On both frontend and backend, start from adding tests based on the specification if they do not already exist, and only then implement the functionality.
- Before considering any part of a task done, verify that linting passes with no errors.
- Before considering any part of a task done, verify that the changed scope builds and starts correctly for the part being worked on: backend, frontend, or both.
- Before considering any part of a task done, verify that all relevant tests pass.
- Before considering any part of a task done, verify that there are no errors, warnings, or similar issues in the affected scope.
- Only after all of the checks above pass may the agent create a commit and treat that part of the task as completed.

## Commit & Pull Request Guidelines
- Recent history favors short, imperative messages such as `Fix local setup and start scripts`; occasional Conventional Commits like `feat: add ...` are also present.
- Prefer one focused change per commit.
- PRs should include what changed, how it was verified, linked issue/task if available, and screenshots for visible frontend updates.

## Security & Configuration Tips
- Copy `.env.example` to `.env` for local setup and never commit real secrets.
- Default local PostgreSQL runs through `compose.yaml` on port `5433`.
- OpenRouter and AG-UI settings are environment-driven; keep machine-specific overrides out of source control.
