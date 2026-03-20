import { normalizeLoanCopilotState } from "./state";
import { BackendLoanWorkflowState } from "./types";

describe("normalizeLoanCopilotState", () => {
  it("maps backend intent state into a form-open frontend state", () => {
    const backendState: BackendLoanWorkflowState = {
      showLoanForm: true,
      currentStep: "FORM",
      assistantMessage: "I can help start the loan application. The relevant form is ready.",
      intent: {
        label: "LOAN_APPLICATION",
        confidence: 0.93,
        thresholdMet: true
      },
      form: {
        formVersion: "mvp-v1",
        loanType: "PERSONAL_LOAN",
        amountBand: "STANDARD",
        shownFields: [
          "identifierType",
          "identifierValue",
          "loanType",
          "requestedAmount",
          "requestedTermMonths",
          "declaredPurpose"
        ],
        requiredFields: ["identifierValue", "requestedAmount"],
        prefilledFields: ["identifierValue"],
        values: {
          identifierType: "PESEL",
          identifierValue: "90010112345",
          requestedAmount: 20000,
          requestedTermMonths: 48,
          declaredPurpose: "Home renovation"
        },
        validationErrors: {}
      },
      customerProfile: null,
      recommendation: null,
      employeeAction: null
    };

    const state = normalizeLoanCopilotState(backendState, { sessionId: "session-42" });

    expect(state.showLoanForm).toBe(true);
    expect(state.currentStep).toBe("form");
    expect(state.intent?.thresholdMet).toBe(true);
    expect(state.form.values.identifierValue).toBe("90010112345");
    expect(state.form.values.requestedAmount).toBe("20000");
    expect(state.form.sourceFieldKeys).toEqual(["identifierValue"]);
    expect(state.form.statusMessage).toContain("loan application");
    expect(state.sessionId).toBe("session-42");
  });
});
