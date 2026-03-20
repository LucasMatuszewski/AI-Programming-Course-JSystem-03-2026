package com.silkycoders1.jsystemssilkycodders1.loan.domain;

import java.util.List;

public record LoanDecision(
		int score,
		RecommendationStatus recommendationStatus,
		List<String> topFactors,
		String explanation,
		String nextSteps,
		String llmRiskLabel,
		double llmConfidence
) {
}
