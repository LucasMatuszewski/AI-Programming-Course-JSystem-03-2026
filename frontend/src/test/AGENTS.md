# Frontend Unit and Integration Test Guidelines

Use this file for Vitest-based frontend tests in `frontend/src/test/`.

## Scope
- Component tests
- Hook and utility tests
- Frontend integration tests around the App Router UI
- Mocked frontend-to-backend interactions through `/api/copilotkit`

## File Placement
- Mirror the frontend source structure where practical.
- Use `*.test.tsx` for React components and `*.test.ts` for non-UI logic.
- Add shared test utilities here only when multiple tests need them.

## What to Test
- Rendering of important UI states
- User interactions and form behavior
- Validation messages and disabled/error states
- Conditional rendering and approval flows
- Mapping between UI state and requests sent through `/api/copilotkit`

Do not spend unit/integration tests on library internals or visual styling details.

## Query Rules
- Prefer `getByRole`, `getByLabelText`, and visible text.
- Use `getByTestId` only when semantic queries are not stable enough for the behavior under test.
- Never query by CSS classes or DOM structure.

## Mocking Rules
- Mock network boundaries, not the component behavior you are trying to verify.
- Mock `/api/copilotkit` interactions at the frontend boundary.
- Base mocked AG-UI payloads on real backend behavior when possible instead of inventing unrealistic event shapes.

## Test Quality Rules
- Test observable behavior, not implementation details.
- Cover at least one failure path for every meaningful success path.
- If a component depends on agent-driven UI updates, prove the UI reacts correctly to streamed or staged backend responses.
- Add `data-testid` only for elements that need a stable test handle and do not already have a good semantic selector.

## Maintenance Rule
- When frontend test patterns become clearer, update this file or a more specific nested `AGENTS.md` instead of pushing test detail into broader frontend docs.
