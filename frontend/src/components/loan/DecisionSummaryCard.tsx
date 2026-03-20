import React from "react";
import { DecisionSummary } from "@/lib/loan-copilot/types";
import { DecisionScoreGauge } from "./DecisionScoreGauge";

const statusLabels = {
  APPROVE: "Approve",
  REJECT: "Reject",
  NEEDS_VERIFICATION: "Needs Verification"
} as const;

export function DecisionSummaryCard({ recommendation }: { recommendation: DecisionSummary }) {
  const badgeClass = `decision-badge decision-badge-${recommendation.status.toLowerCase()}`;

  return (
    <section className="workflow-card" data-testid="decision-summary-card">
      <div className="workflow-card-header">
        <div>
          <p className="panel-eyebrow">Recommendation</p>
          <h2 className="panel-title">Preliminary decision</h2>
        </div>
        <span className={badgeClass}>{statusLabels[recommendation.status]}</span>
      </div>

      <DecisionScoreGauge score={recommendation.score} status={recommendation.status} />

      <div className="decision-grid">
        <div>
          <p className="metric-label">Rule-match summary</p>
          <p className="metric-copy">{recommendation.ruleMatchSummary}</p>
        </div>
        <div>
          <p className="metric-label">Explanation</p>
          <p className="metric-copy">{recommendation.explanation}</p>
        </div>
      </div>

      <div className="decision-list-grid">
        <div>
          <p className="metric-label">Top factors</p>
          <ul className="decision-list">
            {recommendation.topFactors.map((factor) => (
              <li key={factor}>{factor}</li>
            ))}
          </ul>
        </div>
        <div>
          <p className="metric-label">Next steps</p>
          <ul className="decision-list">
            {recommendation.nextSteps.map((step) => (
              <li key={step}>{step}</li>
            ))}
          </ul>
        </div>
      </div>

      <details className="decision-details">
        <summary>Inspect contributing details</summary>
        <ul className="decision-list">
          {recommendation.details.map((detail) => (
            <li key={detail}>{detail}</li>
          ))}
        </ul>
      </details>
    </section>
  );
}
