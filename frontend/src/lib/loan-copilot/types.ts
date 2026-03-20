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
  employmentStatus?: string;
  monthlyIncomeNet?: number;
  existingLiabilitiesTotal?: number;
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
  threshold: number;
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
