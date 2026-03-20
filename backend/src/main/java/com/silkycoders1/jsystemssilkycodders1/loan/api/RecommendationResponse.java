package com.silkycoders1.jsystemssilkycodders1.loan.api;

import java.util.List;

public record RecommendationResponse(
		String status,
		int score,
		String ruleSetVersion,
		String llmRiskLabel,
		double llmConfidence,
		List<String> topFactors,
		String explanation,
		String nextSteps
) {
}
