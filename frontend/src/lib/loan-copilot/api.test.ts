import { recordEmployeeAction } from "./api";

describe("recordEmployeeAction", () => {
  it("posts employee actions to the backend employee-action endpoint", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({
        applicationId: 42,
        state: {
          showLoanForm: true,
          currentStep: "DECISION",
          assistantMessage: "Employee action saved.",
          form: {
            formVersion: "mvp-v1",
            loanType: "PERSONAL_LOAN",
            amountBand: "STANDARD",
            shownFields: [],
            requiredFields: [],
            prefilledFields: [],
            values: {},
            validationErrors: {}
          },
          recommendation: {
            status: "REJECT",
            score: 32,
            ruleSetVersion: "rules-v1",
            llmRiskLabel: "Debt burden remains above policy tolerance.",
            llmConfidence: 0.82,
            topFactors: ["Debt-to-income exceeds the threshold"],
            explanation: "The application should remain rejected.",
            nextSteps: "Explain the debt burden to the customer."
          },
          employeeAction: {
            actionType: "ACCEPT_RECOMMENDATION",
            actionTimestamp: "2026-03-20T11:00:00Z"
          }
        }
      })
    });

    vi.stubGlobal("fetch", fetchMock);

    await recordEmployeeAction("42", {
      employeeId: "demo-employee-1",
      action: "ACCEPT_RECOMMENDATION"
    });

    expect(fetchMock).toHaveBeenCalledWith(
      "http://127.0.0.1:8080/api/loan/applications/42/employee-action",
      expect.objectContaining({
        method: "POST"
      })
    );
  });
});
