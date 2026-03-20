# Frontend Guidelines

Use this file for frontend-specific rules. Keep frontend details here instead of expanding the root `AGENTS.md`.

## Scope
- Next.js App Router frontend in `frontend/src/app`
- CopilotKit chat UI and runtime bridge to the backend AG-UI endpoint
- Tailwind-based styling aligned with `docs/nbp-design-system.md`
- Frontend tests should use Vitest for unit/integration and Playwright for E2E

## Current Frontend Shape
- `src/app/page.tsx` is the main page entry
- `src/app/layout.tsx` defines the root layout
- `src/app/globals.css` contains global styles
- `src/app/api/copilotkit/route.ts` is the server route that bridges the frontend runtime to the backend
- `src/app/component/` currently holds UI pieces such as `chatApproval.tsx`

## Architecture Notes
- The frontend is not a generic marketing site. It is an app shell around chat, agent actions, and guided data collection.
- CopilotKit is the UI/runtime layer on the frontend. The backend drives behavior through AG-UI events and tools.
- Keep the route layer thin. Business logic, mapping, validation, and UI behavior should not accumulate inside a single page or route file.

## Structure To Grow Toward
As the app grows, keep the structure predictable and split by responsibility. Prefer adding focused folders instead of overloading `src/app`.

Use this direction when creating new code:
- `src/app/` for routes, layouts, route-local loading/error states, and API handlers
- `src/components/` for reusable UI and chat-related components
- `src/features/` for larger vertical slices such as loan intake, decision summary, or approval flows
- `src/lib/` for shared helpers, API adapters, schemas, and pure utilities
- `src/test/` for shared frontend test utilities when needed
- `src/e2e/` for Playwright coverage when introduced

Do not move files just to satisfy a target structure. Apply this shape incrementally when touching or adding code.

## Frontend Rules
- Use 2-space indentation.
- Use `PascalCase` for components and camelCase for functions and variables.
- Prefer strict typing. Avoid `any` unless there is a temporary, justified boundary with an external library.
- Keep styling consistent with the NBP design system. Avoid ad hoc colors, spacing, and inline styles unless there is a clear reason.
- Prefer small presentational components plus focused feature logic over large mixed files.
- Keep validation schemas and request/response mapping outside UI components when the logic is non-trivial.
- Keep formatting aligned with Prettier and linting aligned with ESLint as this area evolves.

## Testing and Validation
- Every meaningful UI behavior should get automated coverage, not only static rendering checks.
- Use Vitest for component and integration tests. Add the test setup if the task needs it and it is missing.
- Use Playwright for user-visible flows and final browser verification.
- For frontend changes, verify not only lint/build but also real rendering, interactions, accessibility structure, and browser console cleanliness.
- When mocking backend behavior in frontend tests, do not mock away the behavior actually under test.

## Implementation Workflow
- Start from the PRD, ADR, and design system requirements relevant to the screen or flow.
- Check current docs for any library API you use, especially Next.js, React, CopilotKit, Tailwind, Vitest, and Playwright.
- Before adding a new component or flow, decide whether it belongs to a route, reusable component, feature module, or shared utility.
- Write tests first for the behavior you are adding, then implement the minimum code to make them pass.
- After implementation, verify the running UI in the browser, not only the test output.

## Maintenance Rule
- When a frontend mistake or missing rule becomes clear, update this file or a more specific nested frontend `AGENTS.md` instead of adding more general guidance to the root file.
