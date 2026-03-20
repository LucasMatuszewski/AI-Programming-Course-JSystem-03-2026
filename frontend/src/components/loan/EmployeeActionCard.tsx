"use client";

import React, { useState } from "react";
import { EmployeeDecisionAction, RecommendationStatus } from "@/lib/loan-copilot/types";

const actionLabels: Record<EmployeeDecisionAction, string> = {
  ACCEPT_RECOMMENDATION: "Accept recommendation",
  REJECT_RECOMMENDATION: "Reject recommendation",
  OVERRIDE_RECOMMENDATION: "Override recommendation",
  FOLLOW_UP: "Mark for follow-up"
};

export function EmployeeActionCard({
  applicationId,
  recommendationStatus,
  onSubmit
}: {
  applicationId?: string;
  recommendationStatus: RecommendationStatus;
  onSubmit: (payload: {
    action: EmployeeDecisionAction;
    overrideReason?: string;
    note?: string;
  }) => Promise<void> | void;
}) {
  const [action, setAction] = useState<EmployeeDecisionAction>("ACCEPT_RECOMMENDATION");
  const [overrideReason, setOverrideReason] = useState("");
  const [note, setNote] = useState("");
  const [error, setError] = useState("");

  return (
    <section className="workflow-card" data-testid="employee-action-card">
      <div className="workflow-card-header">
        <div>
          <p className="panel-eyebrow">Employee action</p>
          <h2 className="panel-title">Record the final handling step</h2>
        </div>
        {applicationId ? <span className="source-chip">Application {applicationId}</span> : null}
      </div>

      <p className="metric-copy">Current system recommendation: {recommendationStatus.replaceAll("_", " ")}</p>

      <div className="action-grid">
        {Object.entries(actionLabels).map(([value, label]) => (
          <button
            className={`action-option${action === value ? " action-option-active" : ""}`}
            key={value}
            onClick={() => {
              setAction(value as EmployeeDecisionAction);
              setError("");
            }}
            type="button"
          >
            {label}
          </button>
        ))}
      </div>

      <label className="field-group">
        <span className="field-label">
          Case note <em className="field-label-optional">optional</em>
        </span>
        <textarea
          className="field-input field-textarea"
          onChange={(event) => setNote(event.target.value)}
          rows={3}
          value={note}
        />
      </label>

      {action === "OVERRIDE_RECOMMENDATION" ? (
        <label className="field-group">
          <span className="field-label">
            Override reason <strong className="required-mark">*</strong>
          </span>
          <textarea
            className="field-input field-textarea"
            onChange={(event) => setOverrideReason(event.target.value)}
            rows={3}
            value={overrideReason}
          />
        </label>
      ) : null}

      {error ? (
        <p className="field-error" role="alert">
          {error}
        </p>
      ) : null}

      <button
        className="primary-action-button"
        onClick={async () => {
          if (action === "OVERRIDE_RECOMMENDATION" && !overrideReason.trim()) {
            setError("Override reason is required.");
            return;
          }

          await onSubmit({
            action,
            overrideReason: overrideReason.trim() || undefined,
            note: note.trim() || undefined
          });
        }}
        type="button"
      >
        Save employee action
      </button>
    </section>
  );
}
