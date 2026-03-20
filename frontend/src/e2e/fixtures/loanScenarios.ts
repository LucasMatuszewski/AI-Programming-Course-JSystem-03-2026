export type LoanRecommendation = "Approve" | "Reject" | "Needs Verification";

export type LoanScenario = {
  id: string;
  title: string;
  openingMessage: string;
  identifierLabel: "PESEL" | "VAT ID";
  identifierValue: string;
  loanType: string;
  loanAmount: string;
  expectedRecommendation: LoanRecommendation;
};

export const loanScenarios: LoanScenario[] = [
  {
    id: "strong-applicant",
    title: "Strong applicant",
    openingMessage: "The customer wants to apply for a personal loan for 20000 PLN.",
    identifierLabel: "PESEL",
    identifierValue: "90010112345",
    loanType: "Personal loan",
    loanAmount: "20000",
    expectedRecommendation: "Approve"
  },
  {
    id: "high-debt-burden",
    title: "High debt burden",
    openingMessage: "The customer needs a 35000 PLN personal loan and already has several liabilities.",
    identifierLabel: "PESEL",
    identifierValue: "90020212345",
    loanType: "Personal loan",
    loanAmount: "35000",
    expectedRecommendation: "Reject"
  },
  {
    id: "missing-income-verification",
    title: "Missing income verification",
    openingMessage: "Please help start a cash loan application for 15000 PLN.",
    identifierLabel: "PESEL",
    identifierValue: "90030312345",
    loanType: "Cash loan",
    loanAmount: "15000",
    expectedRecommendation: "Needs Verification"
  }
];
