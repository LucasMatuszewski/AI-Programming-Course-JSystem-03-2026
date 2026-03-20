package com.silkycoders1.jsystemssilkycodders1.loan.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silkycoders1.jsystemssilkycodders1.loan.api.CustomerProfileResponse;
import com.silkycoders1.jsystemssilkycodders1.loan.api.EmployeeActionStateResponse;
import com.silkycoders1.jsystemssilkycodders1.loan.api.FormStateResponse;
import com.silkycoders1.jsystemssilkycodders1.loan.api.IntentStateResponse;
import com.silkycoders1.jsystemssilkycodders1.loan.api.LoanWorkflowStateResponse;
import com.silkycoders1.jsystemssilkycodders1.loan.api.RecommendationResponse;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.FormDefinition;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanIntentResult;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.DecisionResultEntity;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.LoanApplicationEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LoanWorkflowStateFactory {

	private final ObjectMapper objectMapper;

	public LoanWorkflowStateFactory(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public LoanWorkflowStateResponse fromIntent(LoanIntentResult intentResult, FormDefinition formDefinition, String assistantMessage) {
		return new LoanWorkflowStateResponse(
				intentResult.shouldShowForm(),
				intentResult.shouldShowForm() ? "FORM" : "CHAT",
				assistantMessage,
				new IntentStateResponse(intentResult.intentLabel().name(), intentResult.confidence(), intentResult.shouldShowForm()),
				new FormStateResponse(
						formDefinition.formVersion(),
						intentResult.inferredLoanType().name(),
						formDefinition.amountBand(),
						formDefinition.shownFields(),
						formDefinition.requiredFields(),
						List.of(),
						Map.of("loanType", intentResult.inferredLoanType().name()),
						Map.of()
				),
				null,
				null,
				null
		);
	}

	public LoanWorkflowStateResponse fromLookup(LoanIntentResult intentResult, FormDefinition formDefinition, CustomerAggregate aggregate, Map<String, Object> values, List<String> prefilledFields, String assistantMessage) {
		return new LoanWorkflowStateResponse(
				true,
				"FORM",
				assistantMessage,
				new IntentStateResponse(intentResult.intentLabel().name(), intentResult.confidence(), intentResult.shouldShowForm()),
				new FormStateResponse(formDefinition.formVersion(), values.get("loanType").toString(), formDefinition.amountBand(), formDefinition.shownFields(), formDefinition.requiredFields(), prefilledFields, values, Map.of()),
				customerProfile(aggregate),
				null,
				null
		);
	}

	public LoanWorkflowStateResponse fromDecision(FormDefinition formDefinition, CustomerAggregate aggregate, Map<String, Object> values, List<String> prefilledFields, DecisionResultEntity resultEntity) {
		return new LoanWorkflowStateResponse(
				true,
				"DECISION",
				"Recommendation prepared.",
				null,
				new FormStateResponse(formDefinition.formVersion(), values.get("loanType").toString(), formDefinition.amountBand(), formDefinition.shownFields(), formDefinition.requiredFields(), prefilledFields, values, Map.of()),
				customerProfile(aggregate),
				new RecommendationResponse(
						resultEntity.getRecommendation(),
						resultEntity.getDeterministicScore(),
						resultEntity.getRuleSetVersion(),
						resultEntity.getLlmRiskLabel(),
						resultEntity.getLlmConfidence(),
						readTopFactors(resultEntity.getTopFactorsJson()),
						resultEntity.getExplanationText(),
						resultEntity.getNextStepsText()
				),
				null
		);
	}

	public LoanWorkflowStateResponse withEmployeeAction(LoanWorkflowStateResponse state, LoanApplicationEntity applicationEntity) {
		return new LoanWorkflowStateResponse(
				state.showLoanForm(),
				state.currentStep(),
				"Employee action saved.",
				state.intent(),
				state.form(),
				state.customerProfile(),
				state.recommendation(),
				new EmployeeActionStateResponse(
						applicationEntity.getEmployeeFinalAction(),
						applicationEntity.getEmployeeOverrideReason(),
						applicationEntity.getEmployeeActionNote(),
						applicationEntity.getEmployeeActionAt() == null ? null : applicationEntity.getEmployeeActionAt().toString()
				)
		);
	}

	private CustomerProfileResponse customerProfile(CustomerAggregate aggregate) {
		return new CustomerProfileResponse(
				aggregate.customer().getId(),
				aggregate.customer().getFullName(),
				aggregate.customer().getPesel() != null ? "PESEL" : "VAT_ID",
				aggregate.customer().getPesel() != null ? aggregate.customer().getPesel() : aggregate.customer().getVatId(),
				aggregate.financialProfile().getEmploymentStatus(),
				aggregate.financialProfile().getEmploymentMonths(),
				aggregate.financialProfile().getMonthlyIncomeNet(),
				aggregate.financialProfile().getMonthlyExpenses(),
				aggregate.financialProfile().getExistingLiabilitiesTotal(),
				aggregate.financialProfile().getHasIncomeVerification(),
				aggregate.financialProfile().getCreditHistoryLengthMonths(),
				aggregate.repaymentHistory().getLatePayments12m(),
				aggregate.repaymentHistory().getDelinquencyFlag()
		);
	}

	private List<String> readTopFactors(String topFactorsJson) {
		try {
			return objectMapper.readValue(topFactorsJson, new TypeReference<>() {});
		} catch (Exception exception) {
			throw new IllegalStateException("Unable to read top factors JSON", exception);
		}
	}
}
