import React from "react";
import { CustomerProfileSummary, LoanFormState } from "@/lib/loan-copilot/types";

const currencyFormatter = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "PLN",
  maximumFractionDigits: 0
});

export function CustomerProfileCard({
  profile,
  form
}: {
  profile?: CustomerProfileSummary;
  form: LoanFormState;
}) {
  if (!profile) {
    return (
      <section className="workflow-card muted-card" data-testid="customer-profile-card">
        <div className="workflow-card-header">
          <div>
            <p className="panel-eyebrow">Customer profile</p>
            <h2 className="panel-title">Awaiting lookup</h2>
          </div>
        </div>
        <p className="metric-copy">Provide a PESEL or VAT ID to prefill customer details from the bank record.</p>
      </section>
    );
  }

  return (
    <section className="workflow-card" data-testid="customer-profile-card">
      <div className="workflow-card-header">
        <div>
          <p className="panel-eyebrow">Customer profile</p>
          <h2 className="panel-title">{profile.customerName}</h2>
        </div>
        <span className="source-chip">{profile.sourceLabel}</span>
      </div>

      <div className="profile-grid">
        <div className="profile-item">
          <span className="metric-label">Customer ID</span>
          <span>{profile.customerId}</span>
        </div>
        <div className="profile-item">
          <span className="metric-label">Employment</span>
          <span>{profile.employmentStatus ?? "Missing from source"}</span>
        </div>
        <div className="profile-item">
          <span className="metric-label">Income</span>
          <span>{profile.monthlyIncomeNet ? currencyFormatter.format(profile.monthlyIncomeNet) : "Not available"}</span>
        </div>
        <div className="profile-item">
          <span className="metric-label">Liabilities</span>
          <span>
            {typeof profile.existingLiabilitiesTotal === "number"
              ? currencyFormatter.format(profile.existingLiabilitiesTotal)
              : "Not available"}
          </span>
        </div>
      </div>

      {form.sourceFieldKeys.length > 0 ? (
        <div className="tag-row">
          {form.sourceFieldKeys.map((fieldKey) => (
            <span className="field-chip" key={fieldKey}>
              Source: {fieldKey}
            </span>
          ))}
        </div>
      ) : null}
    </section>
  );
}
