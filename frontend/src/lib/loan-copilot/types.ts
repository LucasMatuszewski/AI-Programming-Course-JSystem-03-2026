export type CustomerIdentifierType = "PESEL" | "VAT_ID";
export type LoanType = "PERSONAL_LOAN" | "CAR_LOAN" | "CASH_LOAN";
export type RecommendationStatus = "APPROVE" | "REJECT" | "NEEDS_VERIFICATION";
export type EmployeeDecisionAction =
  | "ACCEPT_RECOMMENDATION"
  | "REJECT_RECOMMENDATION"
  | "OVERRIDE_RECOMMENDATION"
  | "FOLLOW_UP";

export type LoanCopilotStep = "chat" | "form" | "decision";

export interface LoanFormValues {
  identifierType: CustomerIdentifierType;
  identifierValue: string;
  loanType: LoanType;
  requestedAmount: string;
  requestedTermMonths: string;
  declaredPurpose: string;
  vehicleValue: string;
  vehicleAgeYears: string;
  collateralDescription: string;
}

export interface LoanFormState {
  formVersion: string;
  shownFieldKeys: string[];
  sourceFieldKeys: string[];
  editedFieldKeys: string[];
  values: LoanFormValues;
  errors: Record<string, string>;
  statusMessage?: string;
}

export interface CustomerProfileSummary {
  customerId: string;
  customerName: string;
  identifierType?: CustomerIdentifierType;
  identifierValue?: string;
  employmentStatus?: string;
  employmentMonths?: number;
  monthlyIncomeNet?: number;
  monthlyExpenses?: number;
  existingLiabilitiesTotal?: number;
  hasIncomeVerification?: boolean;
  creditHistoryLengthMonths?: number;
  latePayments12m?: number;
  delinquencyFlag?: boolean;
  sourceLabel: string;
}

export interface DecisionSummary {
  applicationId?: string;
  status: RecommendationStatus;
  score: number;
  ruleMatchSummary: string;
  topFactors: string[];
  explanation: string;
  nextSteps: string[];
  details: string[];
  verificationReason?: string;
}

export interface EmployeeActionState {
  action?: EmployeeDecisionAction;
  overrideReason?: string;
  note?: string;
  recordedAt?: string;
}

export interface LoanIntentState {
  label: "LOAN_APPLICATION" | "GENERAL_QUESTION" | "OTHER";
  confidence: number;
  thresholdMet: boolean;
}

export interface LoanCopilotState {
  employeeId: string;
  sessionId?: string;
  currentStep: LoanCopilotStep;
  showLoanForm: boolean;
  intent?: LoanIntentState;
  form: LoanFormState;
  customerProfile?: CustomerProfileSummary;
  recommendation?: DecisionSummary;
  employeeAction?: EmployeeActionState;
  assistantMessage?: string;
}

export interface CustomerLookupPayload {
  identifierType: CustomerIdentifierType;
  identifierValue: string;
  sessionId?: string;
}

export interface SubmitLoanApplicationPayload extends LoanFormValues {
  sessionId?: string;
  employeeId: string;
}

export interface RecordEmployeeActionPayload {
  employeeId: string;
  action: EmployeeDecisionAction;
  overrideReason?: string;
  note?: string;
}

export interface LoanCopilotApiResponse {
  state: LoanCopilotState;
}

export interface BackendIntentState {
  label: LoanIntentState["label"];
  confidence: number;
  thresholdMet: boolean;
}

export interface BackendFormState {
  formVersion: string;
  loanType?: LoanType;
  amountBand?: string;
  shownFields: string[];
  requiredFields: string[];
  prefilledFields: string[];
  values: Partial<Record<keyof LoanFormValues, string | number>>;
  validationErrors: Record<string, string>;
}

export interface BackendCustomerProfile {
  customerId: number;
  fullName: string;
  identifierType: CustomerIdentifierType;
  identifierValue: string;
  employmentStatus?: string;
  employmentMonths?: number;
  monthlyIncomeNet?: number;
  monthlyExpenses?: number;
  existingLiabilitiesTotal?: number;
  hasIncomeVerification?: boolean;
  creditHistoryLengthMonths?: number;
  latePayments12m?: number;
  delinquencyFlag?: boolean;
}

export interface BackendRecommendation {
  status: RecommendationStatus;
  score: number;
  ruleSetVersion: string;
  llmRiskLabel: string;
  llmConfidence: number;
  topFactors: string[];
  explanation: string;
  nextSteps?: string;
}

export interface BackendEmployeeActionState {
  actionType?: EmployeeDecisionAction;
  overrideReason?: string;
  note?: string;
  actionTimestamp?: string;
}

export interface BackendLoanWorkflowState {
  showLoanForm: boolean;
  currentStep: "CHAT" | "FORM" | "DECISION";
  assistantMessage?: string;
  intent?: BackendIntentState | null;
  form?: BackendFormState | null;
  customerProfile?: BackendCustomerProfile | null;
  recommendation?: BackendRecommendation | null;
  employeeAction?: BackendEmployeeActionState | null;
}

export interface BackendLoanWorkflowApiResponse {
  applicationId?: number;
  chatSessionId?: string;
  state: BackendLoanWorkflowState;
}
