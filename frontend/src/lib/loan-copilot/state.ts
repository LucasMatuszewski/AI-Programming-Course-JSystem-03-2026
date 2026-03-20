import {
  BackendLoanWorkflowApiResponse,
  BackendFormState,
  BackendLoanWorkflowState,
  CustomerProfileSummary,
  DecisionSummary,
  EmployeeActionState,
  LoanCopilotState,
  LoanCopilotStep,
  LoanFormValues
} from "./types";

export const initialLoanCopilotState: LoanCopilotState = {
  employeeId: "demo-employee-1",
  currentStep: "chat",
  showLoanForm: false,
  assistantMessage: "Ask the assistant about a loan request to open the guided application flow.",
  form: {
    formVersion: "mvp-v1",
    shownFieldKeys: [
      "identifierType",
      "identifierValue",
      "loanType",
      "requestedAmount",
      "requestedTermMonths",
      "declaredPurpose"
    ],
    sourceFieldKeys: [],
    editedFieldKeys: [],
    values: {
      identifierType: "PESEL",
      identifierValue: "",
      loanType: "PERSONAL_LOAN",
      requestedAmount: "",
      requestedTermMonths: "",
      declaredPurpose: "",
      vehicleValue: "",
      vehicleAgeYears: "",
      collateralDescription: ""
    },
    errors: {},
    statusMessage: "Ask the assistant about a loan request to open the application flow."
  }
};

function toFrontendStep(step?: string): LoanCopilotStep {
  switch (step) {
    case "FORM":
      return "form";
    case "DECISION":
      return "decision";
    default:
      return "chat";
  }
}

function toStringValue(value: string | number | undefined): string {
  if (typeof value === "number") {
    return String(value);
  }

  return value ?? "";
}

function splitNextSteps(value?: string): string[] {
  if (!value) {
    return [];
  }

  return value
    .split(/\r?\n|;/)
    .map((entry) => entry.trim())
    .filter(Boolean);
}

function mapRecommendation(
  recommendation: BackendLoanWorkflowState["recommendation"],
  applicationId?: number
): DecisionSummary | undefined {
  if (!recommendation) {
    return undefined;
  }

  const nextSteps = splitNextSteps(recommendation.nextSteps);
  const details = [
    `Rule set: ${recommendation.ruleSetVersion}`,
    `Risk label: ${recommendation.llmRiskLabel}`,
    `LLM confidence: ${Math.round(recommendation.llmConfidence * 100)}%`
  ];

  return {
    applicationId: applicationId ? String(applicationId) : undefined,
    status: recommendation.status,
    score: recommendation.score,
    ruleMatchSummary: recommendation.llmRiskLabel,
    topFactors: recommendation.topFactors,
    explanation: recommendation.explanation,
    nextSteps,
    details,
    verificationReason:
      recommendation.status === "NEEDS_VERIFICATION" && nextSteps.length > 0 ? nextSteps[0] : undefined
  };
}

function mapCustomerProfile(profile: BackendLoanWorkflowState["customerProfile"]): CustomerProfileSummary | undefined {
  if (!profile) {
    return undefined;
  }

  return {
    customerId: String(profile.customerId),
    customerName: profile.fullName,
    identifierType: profile.identifierType,
    identifierValue: profile.identifierValue,
    employmentStatus: profile.employmentStatus,
    employmentMonths: profile.employmentMonths,
    monthlyIncomeNet: profile.monthlyIncomeNet,
    monthlyExpenses: profile.monthlyExpenses,
    existingLiabilitiesTotal: profile.existingLiabilitiesTotal,
    hasIncomeVerification: profile.hasIncomeVerification,
    creditHistoryLengthMonths: profile.creditHistoryLengthMonths,
    latePayments12m: profile.latePayments12m,
    delinquencyFlag: profile.delinquencyFlag,
    sourceLabel: "Bank record"
  };
}

function mapEmployeeAction(action: BackendLoanWorkflowState["employeeAction"]): EmployeeActionState | undefined {
  if (!action) {
    return undefined;
  }

  return {
    action: action.actionType,
    overrideReason: action.overrideReason,
    note: action.note,
    recordedAt: action.actionTimestamp
  };
}

function mapFormValues(
  values?: BackendFormState["values"],
  loanType?: BackendFormState["loanType"]
): LoanFormValues {
  return {
    ...initialLoanCopilotState.form.values,
    identifierType: values?.identifierType === "VAT_ID" ? "VAT_ID" : "PESEL",
    identifierValue: toStringValue(values?.identifierValue),
    loanType: loanType ?? (values?.loanType as LoanFormValues["loanType"]) ?? initialLoanCopilotState.form.values.loanType,
    requestedAmount: toStringValue(values?.requestedAmount),
    requestedTermMonths: toStringValue(values?.requestedTermMonths),
    declaredPurpose: toStringValue(values?.declaredPurpose),
    vehicleValue: toStringValue(values?.vehicleValue),
    vehicleAgeYears: toStringValue(values?.vehicleAgeYears),
    collateralDescription: toStringValue(values?.collateralDescription)
  };
}

export function normalizeLoanCopilotState(
  input: Partial<LoanCopilotState> | BackendLoanWorkflowState | undefined,
  options?: { sessionId?: string; applicationId?: number }
): LoanCopilotState {
  if (!input) {
    return {
      ...initialLoanCopilotState,
      sessionId: options?.sessionId ?? initialLoanCopilotState.sessionId
    };
  }

  if ("showLoanForm" in input && input.showLoanForm !== undefined && input.currentStep?.toUpperCase() === input.currentStep) {
    const backendState = input as BackendLoanWorkflowState;
    const form = backendState.form;
    const normalizedValues = mapFormValues(form?.values, form?.loanType);

    return {
      employeeId: initialLoanCopilotState.employeeId,
      sessionId: options?.sessionId,
      currentStep: toFrontendStep(backendState.currentStep),
      showLoanForm: backendState.showLoanForm,
      assistantMessage: backendState.assistantMessage,
      intent: backendState.intent
        ? {
            label: backendState.intent.label,
            confidence: backendState.intent.confidence,
            thresholdMet: backendState.intent.thresholdMet
          }
        : undefined,
      form: {
        formVersion: form?.formVersion ?? initialLoanCopilotState.form.formVersion,
        shownFieldKeys: form?.shownFields ?? initialLoanCopilotState.form.shownFieldKeys,
        sourceFieldKeys: form?.prefilledFields ?? [],
        editedFieldKeys: [],
        values: normalizedValues,
        errors: form?.validationErrors ?? {},
        statusMessage: backendState.assistantMessage ?? initialLoanCopilotState.form.statusMessage
      },
      customerProfile: mapCustomerProfile(backendState.customerProfile),
      recommendation: mapRecommendation(backendState.recommendation, options?.applicationId),
      employeeAction: mapEmployeeAction(backendState.employeeAction)
    };
  }

  const partialState = input as Partial<LoanCopilotState>;

  return {
    ...initialLoanCopilotState,
    ...partialState,
    sessionId: partialState.sessionId ?? options?.sessionId ?? initialLoanCopilotState.sessionId,
    employeeId: partialState.employeeId ?? initialLoanCopilotState.employeeId,
    assistantMessage: partialState.assistantMessage ?? initialLoanCopilotState.assistantMessage,
    form: {
      ...initialLoanCopilotState.form,
      ...partialState.form,
      values: {
        ...initialLoanCopilotState.form.values,
        ...partialState.form?.values
      },
      errors: partialState.form?.errors ?? initialLoanCopilotState.form.errors,
      shownFieldKeys: partialState.form?.shownFieldKeys ?? initialLoanCopilotState.form.shownFieldKeys,
      sourceFieldKeys: partialState.form?.sourceFieldKeys ?? initialLoanCopilotState.form.sourceFieldKeys,
      editedFieldKeys: partialState.form?.editedFieldKeys ?? initialLoanCopilotState.form.editedFieldKeys
    }
  };
}

export function normalizeLoanCopilotApiResponse(response: BackendLoanWorkflowApiResponse) {
  return {
    state: normalizeLoanCopilotState(response.state, {
      sessionId: response.chatSessionId,
      applicationId: response.applicationId
    })
  };
}
