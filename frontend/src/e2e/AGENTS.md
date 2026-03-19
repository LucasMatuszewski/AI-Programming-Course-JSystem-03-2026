# Frontend E2E Test Guidelines

Use this file for Playwright end-to-end tests in `frontend/src/e2e/`.

## Scope
- Real browser verification of the running frontend
- Full user-visible flows through the frontend route and backend agent integration
- Final verification of rendering, interactions, and browser-side failures

## Core Rule
- E2E tests must exercise the running application. If the frontend route, backend, or required runtime dependency is down, the test should fail clearly.
- Do not turn E2E tests into mocked component tests.

## What E2E Should Cover
- App loads and the main chat shell is usable
- A user can send a message through the UI
- Agent-driven UI changes appear when the backend emits them
- Important approval or form flows work across the real frontend/backend boundary
- No browser console or page errors occur in the tested flow

## Selector Rules
- Prefer `getByRole`, `getByLabel`, and stable visible text.
- Use `getByTestId` for app-specific observable elements when semantic selectors are not enough.
- Never use CSS classes or fragile DOM paths as primary selectors.

## Failure Rules
- Do not filter out backend, network, or browser errors.
- Do not hide failures with permissive assertions or optional outcomes that are not part of the real spec.
- If a flow depends on backend output, the test should fail when that output never arrives.

## E2E Quality Rules
- Cover the real flow through `/api/copilotkit`, not direct shortcuts around it.
- Keep tests focused on user-observable outcomes, not internal implementation details.
- Add stable test handles when needed for flows that cannot be selected reliably with semantics alone.
- If a scenario depends on seeded data or database state, make that dependency explicit and keep it aligned with the project PostgreSQL seed/migration setup.

## Maintenance Rule
- When Playwright patterns become clearer, update this file or a more specific nested `AGENTS.md` instead of duplicating E2E guidance elsewhere.
