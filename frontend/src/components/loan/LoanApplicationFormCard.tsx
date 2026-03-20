"use client";

import React, { ChangeEvent } from "react";
import { LoanFormState, LoanFormValues } from "@/lib/loan-copilot/types";
import { getVisibleFieldKeys } from "@/lib/loan-copilot/validation";

const fieldLabels: Record<keyof LoanFormValues, string> = {
  identifierType: "Identifier type",
  identifierValue: "PESEL or VAT ID",
  loanType: "Loan product",
  requestedAmount: "Requested amount",
  requestedTermMonths: "Requested term (months)",
  declaredPurpose: "Declared purpose",
  vehicleValue: "Vehicle value",
  vehicleAgeYears: "Vehicle age (years)",
  collateralDescription: "Collateral description"
};

function FieldHint({
  fieldKey,
  form
}: {
  fieldKey: keyof LoanFormValues;
  form: LoanFormState;
}) {
  const isSourceField = form.sourceFieldKeys.includes(fieldKey);
  const isEditedField = form.editedFieldKeys.includes(fieldKey);

  if (!isSourceField && !isEditedField) {
    return null;
  }

  return (
    <span className={`field-origin${isEditedField ? " field-origin-edited" : ""}`}>
      {isEditedField ? "Edited after prefill" : "Prefilled from bank record"}
    </span>
  );
}

export function LoanApplicationFormCard({
  form,
  busy,
  onLookup,
  onFieldChange,
  onSubmit
}: {
  form: LoanFormState;
  busy?: boolean;
  onLookup: () => Promise<void> | void;
  onFieldChange: (field: keyof LoanFormValues, value: string) => void;
  onSubmit: () => Promise<void> | void;
}) {
  const visibleFieldKeys = getVisibleFieldKeys(form.values);
  const requiredFieldKeys = [
    "identifierValue",
    "requestedAmount",
    "requestedTermMonths",
    "declaredPurpose",
    ...(form.values.loanType === "CAR_LOAN" ? ["vehicleValue", "vehicleAgeYears"] : []),
    ...(Number(form.values.requestedAmount) > 30000 ? ["collateralDescription"] : [])
  ];

  const renderField = (fieldKey: keyof LoanFormValues) => {
    if (fieldKey === "identifierType") {
      return (
        <label className="field-group" key={fieldKey}>
          <span className="field-label">{fieldLabels[fieldKey]}</span>
          <select
            className="field-input"
            onChange={(event) => onFieldChange(fieldKey, event.target.value)}
            value={form.values.identifierType}
          >
            <option value="PESEL">PESEL</option>
            <option value="VAT_ID">VAT ID</option>
          </select>
        </label>
      );
    }

    if (fieldKey === "loanType") {
      return (
        <label className="field-group" key={fieldKey}>
          <span className="field-label">{fieldLabels[fieldKey]}</span>
          <select
            className="field-input"
            onChange={(event) => onFieldChange(fieldKey, event.target.value)}
            value={form.values.loanType}
          >
            <option value="PERSONAL_LOAN">Personal loan</option>
            <option value="CAR_LOAN">Car loan</option>
            <option value="CASH_LOAN">Cash loan</option>
          </select>
        </label>
      );
    }

    const isTextarea = fieldKey === "declaredPurpose" || fieldKey === "collateralDescription";
    const onChange = (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) =>
      onFieldChange(fieldKey, event.target.value);

    return (
      <label className="field-group" key={fieldKey}>
        <span className="field-label">
          {fieldLabels[fieldKey]}
          {requiredFieldKeys.includes(fieldKey) ? <strong className="required-mark">*</strong> : null}
        </span>
        <FieldHint fieldKey={fieldKey} form={form} />
        {isTextarea ? (
          <textarea
            className="field-input field-textarea"
            data-testid={`loan-form-${fieldKey}`}
            onChange={onChange}
            rows={3}
            value={form.values[fieldKey]}
          />
        ) : (
          <input
            className="field-input"
            data-testid={`loan-form-${fieldKey}`}
            inputMode={fieldKey === "requestedAmount" || fieldKey === "requestedTermMonths" ? "numeric" : "text"}
            onChange={onChange}
            value={form.values[fieldKey]}
          />
        )}
        {form.errors[fieldKey] ? (
          <span className="field-error" role="alert">
            {form.errors[fieldKey]}
          </span>
        ) : null}
      </label>
    );
  };

  return (
    <section className="workflow-card" data-testid="loan-application-form-card">
      <div className="workflow-card-header">
        <div>
          <p className="panel-eyebrow">Application form</p>
          <h2 className="panel-title">Collect application details in-chat</h2>
        </div>
        <span className="source-chip">Form {form.formVersion}</span>
      </div>

      <div className="field-grid">
        {visibleFieldKeys.map((fieldKey) => renderField(fieldKey as keyof LoanFormValues))}
      </div>

      {form.statusMessage ? <p className="metric-copy">{form.statusMessage}</p> : null}

      <div className="workflow-actions">
        <button className="secondary-action-button" disabled={busy} onClick={() => void onLookup()} type="button">
          {busy ? "Checking…" : "Lookup customer"}
        </button>
        <button className="primary-action-button" disabled={busy} onClick={() => void onSubmit()} type="button">
          {busy ? "Submitting…" : "Submit application"}
        </button>
      </div>
    </section>
  );
}
