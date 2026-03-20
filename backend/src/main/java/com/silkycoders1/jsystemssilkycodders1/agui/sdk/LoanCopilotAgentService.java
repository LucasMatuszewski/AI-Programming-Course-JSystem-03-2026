package com.silkycoders1.jsystemssilkycodders1.agui.sdk;

import com.agui.core.agent.RunAgentParameters;
import com.agui.core.event.BaseEvent;
import com.agui.core.state.State;
import com.agui.server.EventFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silkycoders1.jsystemssilkycodders1.loan.application.AuditService;
import com.silkycoders1.jsystemssilkycodders1.loan.application.IntentDetectionService;
import com.silkycoders1.jsystemssilkycodders1.loan.application.LoanApplicationValidator;
import com.silkycoders1.jsystemssilkycodders1.loan.application.LoanWorkflowStateFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class LoanCopilotAgentService {

	private final IntentDetectionService intentDetectionService;
	private final LoanApplicationValidator validator;
	private final LoanWorkflowStateFactory stateFactory;
	private final AuditService auditService;
	private final ObjectMapper objectMapper;

	public LoanCopilotAgentService(
			IntentDetectionService intentDetectionService,
			LoanApplicationValidator validator,
			LoanWorkflowStateFactory stateFactory,
			AuditService auditService,
			ObjectMapper objectMapper
	) {
		this.intentDetectionService = intentDetectionService;
		this.validator = validator;
		this.stateFactory = stateFactory;
		this.auditService = auditService;
		this.objectMapper = objectMapper;
	}

	public Flux<? extends BaseEvent> run(RunAgentParameters input) {
		var lastMessage = input.getMessages() == null || input.getMessages().isEmpty()
				? ""
				: input.getMessages().get(input.getMessages().size() - 1).getContent();
		var intent = intentDetectionService.detect(lastMessage);
		var formDefinition = validator.formDefinition(intent.inferredLoanType(), null);
		var assistantMessage = intent.shouldShowForm()
				? "I can help start the loan application. The relevant form is ready."
				: "I can continue the conversation without starting a loan application yet.";
		var state = new State(objectMapper.convertValue(
				stateFactory.fromIntent(intent, formDefinition, assistantMessage),
				new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {}
		));
		var employeeId = "demo-employee-1";

		if (intent.shouldShowForm()) {
			auditService.record(null, input.getThreadId(), employeeId, "INTENT_DETECTED", java.util.Map.of(
					"message", lastMessage,
					"intentLabel", intent.intentLabel().name(),
					"confidence", intent.confidence()
			));
			auditService.record(null, input.getThreadId(), employeeId, "FORM_DISPLAYED", java.util.Map.of(
					"formVersion", formDefinition.formVersion(),
					"fieldsShown", formDefinition.shownFields()
			));
		}

		var messageId = String.valueOf(System.currentTimeMillis());
		return Flux.fromIterable(List.of(
				EventFactory.runStartedEvent(input.getThreadId(), input.getRunId()),
				EventFactory.stateSnapshotEvent(state),
				EventFactory.textMessageStartEvent(messageId, "assistant"),
				EventFactory.textMessageContentEvent(messageId, assistantMessage),
				EventFactory.textMessageEndEvent(messageId),
				EventFactory.runFinishedEvent(input.getThreadId(), input.getRunId())
		));
	}
}
