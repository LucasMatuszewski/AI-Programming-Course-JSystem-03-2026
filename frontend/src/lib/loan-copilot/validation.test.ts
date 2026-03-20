import { getVisibleFieldKeys, validateLoanForm } from "./validation";
import { initialLoanCopilotState } from "./state";

describe("loan form validation", () => {
  it("requires collateral for large loan requests", () => {
    const errors = validateLoanForm({
      ...initialLoanCopilotState.form.values,
      identifierValue: "90010112345",
      requestedAmount: "45000",
      requestedTermMonths: "48",
      declaredPurpose: "Home renovation"
    });

    expect(errors.collateralDescription).toBe("Collateral description is required for requests above 30000.");
  });

  it("shows car-loan specific fields when needed", () => {
    const fieldKeys = getVisibleFieldKeys({
      ...initialLoanCopilotState.form.values,
      loanType: "CAR_LOAN",
      requestedAmount: "25000"
    });

    expect(fieldKeys).toContain("vehicleValue");
    expect(fieldKeys).toContain("vehicleAgeYears");
    expect(fieldKeys).not.toContain("collateralDescription");
  });
});
