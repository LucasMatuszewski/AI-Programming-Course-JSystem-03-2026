import React from "react";
import { render, screen } from "@testing-library/react";
import { LoanApplicationFormCard } from "./LoanApplicationFormCard";
import { initialLoanCopilotState } from "@/lib/loan-copilot/state";

describe("LoanApplicationFormCard", () => {
  it("renders prefilled badges and dynamic fields", () => {
    render(
      <LoanApplicationFormCard
        form={{
          ...initialLoanCopilotState.form,
          sourceFieldKeys: ["identifierValue", "requestedAmount"],
          values: {
            ...initialLoanCopilotState.form.values,
            identifierValue: "90010112345",
            loanType: "CAR_LOAN",
            requestedAmount: "45000"
          }
        }}
        onFieldChange={() => undefined}
        onLookup={() => undefined}
        onSubmit={() => undefined}
      />
    );

    expect(screen.getAllByText("Prefilled from bank record").length).toBeGreaterThan(0);
    expect(screen.getByTestId("loan-form-vehicleValue")).toBeInTheDocument();
    expect(screen.getByTestId("loan-form-collateralDescription")).toBeInTheDocument();
  });
});
