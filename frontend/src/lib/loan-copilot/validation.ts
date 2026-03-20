import { LoanFormValues } from "./types";

export function getVisibleFieldKeys(values: LoanFormValues): string[] {
  const fields = [
    "identifierType",
    "identifierValue",
    "loanType",
    "requestedAmount",
    "requestedTermMonths",
    "declaredPurpose"
  ];

  if (values.loanType === "CAR_LOAN") {
    fields.push("vehicleValue", "vehicleAgeYears");
  }

  const amount = Number(values.requestedAmount);
  if (Number.isFinite(amount) && amount > 30000) {
    fields.push("collateralDescription");
  }

  return fields;
}

export function validateLoanForm(values: LoanFormValues): Record<string, string> {
  const errors: Record<string, string> = {};
  const amount = Number(values.requestedAmount);
  const term = Number(values.requestedTermMonths);

  if (!values.identifierValue.trim()) {
    errors.identifierValue = "Customer identifier is required.";
  }

  if (!values.declaredPurpose.trim()) {
    errors.declaredPurpose = "Purpose is required.";
  }

  if (!Number.isFinite(amount) || amount <= 0) {
    errors.requestedAmount = "Enter a valid requested amount.";
  }

  if (!Number.isFinite(term) || term <= 0) {
    errors.requestedTermMonths = "Enter a valid repayment term.";
  }

  if (values.loanType === "CAR_LOAN") {
    if (!values.vehicleValue.trim()) {
      errors.vehicleValue = "Vehicle value is required for car loans.";
    }

    if (!values.vehicleAgeYears.trim()) {
      errors.vehicleAgeYears = "Vehicle age is required for car loans.";
    }
  }

  if (Number.isFinite(amount) && amount > 30000 && !values.collateralDescription.trim()) {
    errors.collateralDescription = "Collateral description is required for requests above 30000.";
  }

  return errors;
}
