package com.silkycoders1.jsystemssilkycodders1.loan.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silkycoders1.jsystemssilkycodders1.loan.api.CustomerLookupRequest;
import com.silkycoders1.jsystemssilkycodders1.loan.api.CustomerLookupResponse;
import com.silkycoders1.jsystemssilkycodders1.loan.api.EmployeeDecisionActionRequest;
import com.silkycoders1.jsystemssilkycodders1.loan.api.EmployeeDecisionActionResponse;
import com.silkycoders1.jsystemssilkycodders1.loan.api.LoanApplicationResponse;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.CustomerFinancialSnapshot;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.EmployeeActionType;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanProductTerms;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.RepaymentHistorySnapshot;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.ApplicationFormSnapshotEntity;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.ChatSessionEntity;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.DecisionResultEntity;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.LoanApplicationEntity;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository.ApplicationFormSnapshotRepository;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository.ChatSessionRepository;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository.DecisionResultRepository;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository.LoanApplicationRepository;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository.LoanProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoanWorkflowService {

	private static final String RULE_SET_VERSION = "mvp-rules-v1";

	private final CustomerLookupService customerLookupService;
	private final IntentDetectionService intentDetectionService;
	private final LoanApplicationValidator validator;
	private final DeterministicDecisionEngine decisionEngine;
	private final LoanProductRepository loanProductRepository;
	private final ChatSessionRepository chatSessionRepository;
	private final LoanApplicationRepository loanApplicationRepository;
	private final ApplicationFormSnapshotRepository formSnapshotRepository;
	private final DecisionResultRepository decisionResultRepository;
	private final LoanWorkflowStateFactory stateFactory;
	private final AuditService auditService;
	private final ObjectMapper objectMapper;

	public LoanWorkflowService(
			CustomerLookupService customerLookupService,
			IntentDetectionService intentDetectionService,
			LoanApplicationValidator validator,
			DeterministicDecisionEngine decisionEngine,
			LoanProductRepository loanProductRepository,
			ChatSessionRepository chatSessionRepository,
			LoanApplicationRepository loanApplicationRepository,
			ApplicationFormSnapshotRepository formSnapshotRepository,
			DecisionResultRepository decisionResultRepository,
			LoanWorkflowStateFactory stateFactory,
			AuditService auditService,
			ObjectMapper objectMapper
	) {
		this.customerLookupService = customerLookupService;
		this.intentDetectionService = intentDetectionService;
		this.validator = validator;
		this.decisionEngine = decisionEngine;
		this.loanProductRepository = loanProductRepository;
		this.chatSessionRepository = chatSessionRepository;
		this.loanApplicationRepository = loanApplicationRepository;
		this.formSnapshotRepository = formSnapshotRepository;
		this.decisionResultRepository = decisionResultRepository;
		this.stateFactory = stateFactory;
		this.auditService = auditService;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public CustomerLookupResponse lookupCustomer(CustomerLookupRequest request) {
		var aggregate = customerLookupService.findRequired(request.identifierType(), request.identifierValue());
		var session = ensureChatSession(request.chatSessionId(), request.employeeUserId(), request.identifierValue(), request.identifierType().name());
		var formDefinition = validator.formDefinition(request.loanType(), request.requestedAmount());
		var prefilledValues = baseFormValues(request.loanType().name(), request.requestedAmount(), request.identifierType().name(), request.identifierValue(), aggregate);
		var prefilledFields = List.of("identifierType", "identifierValue", "fullName", "employmentStatus", "monthlyIncomeNet", "existingLiabilitiesTotal");

		auditService.record(null, session.getId(), request.employeeUserId(), "CUSTOMER_LOOKUP", Map.of(
				"customerId", aggregate.customer().getId(),
				"prefilledFields", prefilledFields
		));

		return new CustomerLookupResponse(
				session.getId(),
				stateFactory.fromLookup(
						intentDetectionService.detect("loan"),
						formDefinition,
						aggregate,
						prefilledValues,
						prefilledFields,
						"Customer data loaded."
				)
		);
	}

	@Transactional
	public LoanApplicationResponse submitApplication(com.silkycoders1.jsystemssilkycodders1.loan.api.SubmitLoanApplicationRequest request) {
		var validation = validator.validate(request);
		if (!validation.valid()) {
			auditService.record(null, request.chatSessionId(), request.employeeUserId(), "VALIDATION_FAILED", validation.errors());
			throw new IllegalArgumentException(objectToJson(validation.errors()));
		}

		var aggregate = customerLookupService.findRequired(request.identifierType(), request.identifierValue());
		var product = loanProductRepository.findByProductCode(request.loanType().name())
				.orElseThrow(() -> new IllegalArgumentException("Loan product not configured for " + request.loanType()));
		var session = ensureChatSession(request.chatSessionId(), request.employeeUserId(), request.identifierValue(), request.identifierType().name());
		var application = loanApplicationRepository.save(LoanApplicationEntity.create(
				aggregate.customer().getId(),
				session.getId(),
				product.getId(),
				request.requestedAmount(),
				request.requestedTermMonths(),
				request.declaredPurpose(),
				request.employeeUserId(),
				"RECOMMENDATION_READY"
		));

		var prefilledValues = baseFormValues(request.loanType().name(), request.requestedAmount(), request.identifierType().name(), request.identifierValue(), aggregate);
		var submittedValues = new LinkedHashMap<String, Object>(prefilledValues);
		submittedValues.put("requestedTermMonths", request.requestedTermMonths());
		submittedValues.put("declaredPurpose", request.declaredPurpose());
		submittedValues.put("vehicleValue", request.vehicleValue());
		submittedValues.put("vehicleAgeYears", request.vehicleAgeYears());
		submittedValues.put("collateralDescription", request.collateralDescription());
		submittedValues.put("statedMonthlyIncomeNet", request.statedMonthlyIncomeNet());
		submittedValues.put("statedExistingLiabilitiesTotal", request.statedExistingLiabilitiesTotal());

		var formDefinition = validator.formDefinition(request.loanType(), request.requestedAmount());
		formSnapshotRepository.save(ApplicationFormSnapshotEntity.create(
				application.getId(),
				formDefinition.formVersion(),
				objectToJson(prefilledValues),
				objectToJson(submittedValues),
				objectToJson(Map.of())
		));

		auditService.record(application.getId(), session.getId(), request.employeeUserId(), "FORM_SUBMITTED", submittedValues);
		auditService.record(application.getId(), session.getId(), request.employeeUserId(), "CUSTOMER_DATA_RETRIEVED", Map.of(
				"customerId", aggregate.customer().getId(),
				"dataCategories", List.of("financialProfile", "repaymentHistory")
		));

		var decision = decisionEngine.evaluate(
				new LoanProductTerms(request.loanType(), request.requestedAmount(), request.requestedTermMonths()),
				new CustomerFinancialSnapshot(
						aggregate.financialProfile().getEmploymentStatus(),
						aggregate.financialProfile().getEmploymentMonths(),
						request.statedMonthlyIncomeNet() != null ? request.statedMonthlyIncomeNet() : aggregate.financialProfile().getMonthlyIncomeNet(),
						aggregate.financialProfile().getMonthlyExpenses(),
						request.statedExistingLiabilitiesTotal() != null ? request.statedExistingLiabilitiesTotal() : aggregate.financialProfile().getExistingLiabilitiesTotal(),
						aggregate.financialProfile().getHasIncomeVerification(),
						aggregate.financialProfile().getCreditHistoryLengthMonths()
				),
				new RepaymentHistorySnapshot(
						aggregate.repaymentHistory().getLatePayments12m(),
						aggregate.repaymentHistory().getDelinquencyFlag(),
						aggregate.repaymentHistory().getLastDelinquencyDate()
				)
		);

		var resultEntity = decisionResultRepository.save(DecisionResultEntity.create(
				application.getId(),
				RULE_SET_VERSION,
				decision.score(),
				decision.llmRiskLabel(),
				decision.llmConfidence(),
				decision.recommendationStatus().name(),
				objectToJson(decision.topFactors()),
				decision.explanation(),
				decision.nextSteps()
		));

		auditService.record(application.getId(), session.getId(), request.employeeUserId(), "CREDIT_SCORE_CALCULATED", Map.of(
				"score", decision.score(),
				"recommendation", decision.recommendationStatus().name(),
				"ruleSetVersion", RULE_SET_VERSION
		));
		auditService.record(application.getId(), session.getId(), request.employeeUserId(), "RECOMMENDATION_GENERATED", Map.of(
				"status", decision.recommendationStatus().name(),
				"topFactors", decision.topFactors()
		));

		var state = stateFactory.fromDecision(formDefinition, aggregate, submittedValues, List.of("identifierType", "identifierValue", "fullName", "employmentStatus", "monthlyIncomeNet", "existingLiabilitiesTotal"), resultEntity);
		return new LoanApplicationResponse(application.getId(), session.getId(), state);
	}

	@Transactional
	public EmployeeDecisionActionResponse recordEmployeeAction(Long applicationId, EmployeeDecisionActionRequest request) {
		if (request.actionType() == EmployeeActionType.OVERRIDE_RECOMMENDATION && (request.overrideReason() == null || request.overrideReason().isBlank())) {
			throw new IllegalArgumentException("Override reason is required.");
		}

		var application = loanApplicationRepository.findById(applicationId)
				.orElseThrow(() -> new IllegalArgumentException("Application not found: " + applicationId));
		var decisionResult = decisionResultRepository.findByApplicationId(applicationId)
				.orElseThrow(() -> new IllegalArgumentException("Decision result not found for application " + applicationId));
		var aggregate = customerLookupService.findRequiredByCustomerId(application.getCustomerId());

		application.recordEmployeeAction(request.actionType().name(), request.overrideReason(), request.note());
		var savedApplication = loanApplicationRepository.save(application);

		auditService.record(applicationId, application.getChatSessionId(), request.employeeUserId(), "EMPLOYEE_ACTION_RECORDED", Map.of(
				"actionType", request.actionType().name(),
				"overrideReason", request.overrideReason(),
				"recommendation", decisionResult.getRecommendation()
		));

		var state = stateFactory.withEmployeeAction(
				stateFactory.fromDecision(
						validator.formDefinition(productCodeToLoanTypeName(application.getLoanProductId()), application.getRequestedAmount()),
						aggregate,
						baseFormValues(productCodeToLoanTypeName(application.getLoanProductId()).name(), application.getRequestedAmount(), aggregate.customer().getPesel() != null ? "PESEL" : "VAT_ID", aggregate.customer().getPesel() != null ? aggregate.customer().getPesel() : aggregate.customer().getVatId(), aggregate),
						List.of("identifierType", "identifierValue", "fullName", "employmentStatus", "monthlyIncomeNet", "existingLiabilitiesTotal"),
						decisionResult
				),
				savedApplication
		);
		return new EmployeeDecisionActionResponse(applicationId, state);
	}

	private com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanType productCodeToLoanTypeName(Long loanProductId) {
		var product = loanProductRepository.findById(loanProductId)
				.orElseThrow(() -> new IllegalArgumentException("Loan product not found: " + loanProductId));
		return com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanType.valueOf(product.getProductCode());
	}

	private ChatSessionEntity ensureChatSession(String chatSessionId, String employeeId, String identifierValue, String identifierType) {
		return chatSessionRepository.findById(chatSessionId)
				.orElseGet(() -> chatSessionRepository.save(ChatSessionEntity.create(chatSessionId, employeeId, identifierValue, identifierType, "ACTIVE")));
	}

	private LinkedHashMap<String, Object> baseFormValues(String loanType, Integer requestedAmount, String identifierType, String identifierValue, CustomerAggregate aggregate) {
		var values = new LinkedHashMap<String, Object>();
		values.put("identifierType", identifierType);
		values.put("identifierValue", identifierValue);
		values.put("loanType", loanType);
		values.put("requestedAmount", requestedAmount);
		values.put("fullName", aggregate.customer().getFullName());
		values.put("employmentStatus", aggregate.financialProfile().getEmploymentStatus());
		values.put("monthlyIncomeNet", aggregate.financialProfile().getMonthlyIncomeNet());
		values.put("existingLiabilitiesTotal", aggregate.financialProfile().getExistingLiabilitiesTotal());
		return values;
	}

	private String objectToJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (Exception exception) {
			throw new IllegalStateException("Unable to serialize value to JSON", exception);
		}
	}
}
