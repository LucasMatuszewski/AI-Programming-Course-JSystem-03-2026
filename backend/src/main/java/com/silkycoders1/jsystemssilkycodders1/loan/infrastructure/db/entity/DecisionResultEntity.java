package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "decision_results")
public class DecisionResultEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "rule_set_version", nullable = false)
	private String ruleSetVersion;

	@Column(name = "deterministic_score", nullable = false)
	private Integer deterministicScore;

	@Column(name = "llm_risk_label", nullable = false)
	private String llmRiskLabel;

	@Column(name = "llm_confidence", nullable = false)
	private Double llmConfidence;

	@Column(nullable = false)
	private String recommendation;

	@Column(name = "top_factors_json", nullable = false, columnDefinition = "TEXT")
	private String topFactorsJson;

	@Column(name = "explanation_text", nullable = false, columnDefinition = "TEXT")
	private String explanationText;

	@Column(name = "next_steps_text", nullable = false, columnDefinition = "TEXT")
	private String nextStepsText;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	public static DecisionResultEntity create(Long applicationId, String ruleSetVersion, Integer deterministicScore, String llmRiskLabel, Double llmConfidence, String recommendation, String topFactorsJson, String explanationText, String nextStepsText) {
		var entity = new DecisionResultEntity();
		entity.applicationId = applicationId;
		entity.ruleSetVersion = ruleSetVersion;
		entity.deterministicScore = deterministicScore;
		entity.llmRiskLabel = llmRiskLabel;
		entity.llmConfidence = llmConfidence;
		entity.recommendation = recommendation;
		entity.topFactorsJson = topFactorsJson;
		entity.explanationText = explanationText;
		entity.nextStepsText = nextStepsText;
		entity.createdAt = OffsetDateTime.now();
		return entity;
	}

	public Integer getDeterministicScore() {
		return deterministicScore;
	}

	public String getRuleSetVersion() {
		return ruleSetVersion;
	}

	public String getLlmRiskLabel() {
		return llmRiskLabel;
	}

	public Double getLlmConfidence() {
		return llmConfidence;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public String getTopFactorsJson() {
		return topFactorsJson;
	}

	public String getExplanationText() {
		return explanationText;
	}

	public String getNextStepsText() {
		return nextStepsText;
	}
}
