# Loan Decision Copilot E2E Traceability

## Covered Scenarios

| PRD area | Playwright coverage | Status |
|---|---|---|
| Non-loan message should not show form | `non-loan message does not trigger the application form` | Blocked by product gap: intent is not wired from chat to form |
| Strong applicant should approve | `Strong applicant ends with Approve` | Blocked by backend/environment integration |
| High debt burden should reject | `High debt burden ends with Reject` | Blocked by backend/environment integration |
| Missing income verification should need verification | `Missing income verification ends with Needs Verification` | Blocked by backend/environment integration |
| Prefill by PESEL | `prefills customer data after PESEL lookup` | Blocked by backend/environment integration |
| Validation blocks submit | `manual form open and client-side validation work without leaving the workspace` | Executable |
| Employee accept/reject/override | `employee must enter an override reason` | Blocked by recommendation path + route mismatch |
| Continued conversation | `conversation continues after a recommendation is shown` | Blocked by missing end-to-end recommendation path |

## Current Observable Slice

- `smoke: app shell loads` verifies the running Next.js application can render the starter chat shell.
- `manual form open and client-side validation work without leaving the workspace` verifies the integrated UI can open the form and enforce required fields before any backend call.
- Remaining blocked scenarios are split between true product gaps and environment/integration blockers.
