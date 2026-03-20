import React from "react";
import { RecommendationStatus } from "@/lib/loan-copilot/types";

const statusClassName: Record<RecommendationStatus, string> = {
  APPROVE: "score-gauge score-gauge-approve",
  REJECT: "score-gauge score-gauge-reject",
  NEEDS_VERIFICATION: "score-gauge score-gauge-review"
};

export function DecisionScoreGauge({
  score,
  status
}: {
  score: number;
  status: RecommendationStatus;
}) {
  const boundedScore = Math.max(0, Math.min(score, 100));

  return (
    <div className={statusClassName[status]} data-testid="decision-score-gauge">
      <div className="score-gauge-ring" style={{ ["--score" as string]: `${boundedScore}` }}>
        <span>{boundedScore}</span>
      </div>
      <div>
        <p className="metric-label">Rule confidence</p>
        <p className="metric-copy">Deterministic policy score derived from affordability and repayment signals.</p>
      </div>
    </div>
  );
}
