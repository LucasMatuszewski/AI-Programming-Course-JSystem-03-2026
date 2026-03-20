import React from "react";
import { render, screen } from "@testing-library/react";
import { DecisionSummaryCard } from "./DecisionSummaryCard";

describe("DecisionSummaryCard", () => {
  it("renders needs verification distinctly with next steps", () => {
    render(
      <DecisionSummaryCard
        recommendation={{
          status: "NEEDS_VERIFICATION",
          score: 58,
          ruleMatchSummary: "Manual review is required before a preliminary approval can be considered.",
          topFactors: ["Income verification is missing", "Employment stability is recent"],
          explanation: "The case should move to manual verification because the available income evidence is incomplete.",
          nextSteps: ["Collect updated income documentation"],
          details: ["Verification status remains open"]
        }}
      />
    );

    expect(screen.getByText("Needs Verification")).toBeInTheDocument();
    expect(screen.getByText("Collect updated income documentation")).toBeInTheDocument();
    expect(screen.getByTestId("decision-score-gauge")).toBeInTheDocument();
  });
});
