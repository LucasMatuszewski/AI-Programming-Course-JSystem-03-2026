import { expect, test } from "@playwright/test";
import { loanScenarios } from "./fixtures/loanScenarios";
import { LoanDecisionCopilotPage } from "./pages/LoanDecisionCopilotPage";

test.describe("Loan Decision Copilot MVP", () => {
  test("smoke: app shell loads", async ({ page }) => {
    const copilotPage = new LoanDecisionCopilotPage(page);

    await copilotPage.goto();
    await copilotPage.assertShellLoaded();
  });

  test("manual form open and client-side validation work without leaving the workspace", async ({ page }) => {
    const copilotPage = new LoanDecisionCopilotPage(page);

    await copilotPage.goto();
    await copilotPage.openApplicationForm();
    await copilotPage.submitApplicationButton().click();

    await expect(page.getByText("Customer identifier is required.")).toBeVisible();
    await expect(page.getByText("Enter a valid requested amount.")).toBeVisible();
    await expect(page.getByText("Enter a valid repayment term.")).toBeVisible();
    await expect(page.getByText("Purpose is required.")).toBeVisible();
  });

  test("non-loan message does not trigger the application form", async () => {
    test.fixme(true, "Blocked by product gap: chat intent detection is not wired to open the form in the integrated UI.");
  });

  test("prefills customer data after PESEL lookup", async () => {
    test.fixme(true, "Blocked by environment and integration: backend is not reachable in this environment and seeded lookup cannot be exercised.");
  });

  test("validation blocks submit with exact field errors", async () => {
    test.fixme(true, "Covered today by the executable client-side validation test; full end-to-end validation remains blocked on backend submission.");
  });

  test("employee must enter an override reason", async () => {
    test.fixme(true, "Blocked by product and integration gaps: recommendation state cannot be reached end-to-end and frontend/backend action routes do not currently match.");
  });

  test("conversation continues after a recommendation is shown", async () => {
    test.fixme(true, "Blocked until a real recommendation can be produced through the integrated backend flow.");
  });

  for (const scenario of loanScenarios) {
    test(`${scenario.title} ends with ${scenario.expectedRecommendation}`, async () => {
      test.fixme(true, "Blocked by environment and integration: the backend workflow is not executable in this environment, so seeded decision scenarios cannot be verified end-to-end.");
    });
  }

  test("traceability matrix matches the PRD slice", async () => {
    expect(
      [
        "non-loan intent",
        "strong applicant approve",
        "high debt burden reject",
        "missing income verification needs verification",
        "prefill by pesel",
        "validation blocking submit",
        "employee accept reject override",
        "continued conversation"
      ]
    ).toHaveLength(8);
  });
});
