import { LoanCopilotState } from "./types";

export const initialLoanCopilotState: LoanCopilotState = {
  employeeId: "demo-employee-1",
  currentStep: "chat",
  showLoanForm: false,
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
    statusMessage: "Ask the assistant about a loan request or open the application manually."
  }
};
