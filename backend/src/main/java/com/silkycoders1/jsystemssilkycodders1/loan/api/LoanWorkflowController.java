package com.silkycoders1.jsystemssilkycodders1.loan.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silkycoders1.jsystemssilkycodders1.loan.application.LoanWorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loan")
public class LoanWorkflowController {

	private final LoanWorkflowService loanWorkflowService;
	private final ObjectMapper objectMapper;

	public LoanWorkflowController(LoanWorkflowService loanWorkflowService, ObjectMapper objectMapper) {
		this.loanWorkflowService = loanWorkflowService;
		this.objectMapper = objectMapper;
	}

	@PostMapping("/customers/lookup")
	public CustomerLookupResponse lookupCustomer(@RequestBody CustomerLookupRequest request) {
		return loanWorkflowService.lookupCustomer(request);
	}

	@PostMapping("/applications")
	public LoanApplicationResponse submitApplication(@RequestBody SubmitLoanApplicationRequest request) {
		return loanWorkflowService.submitApplication(request);
	}

	@PostMapping("/applications/{applicationId}/employee-action")
	public EmployeeDecisionActionResponse recordEmployeeAction(
			@PathVariable("applicationId") Long applicationId,
			@RequestBody EmployeeDecisionActionRequest request
	) {
		return loanWorkflowService.recordEmployeeAction(applicationId, request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Object handleIllegalArgument(IllegalArgumentException exception) {
		try {
			return objectMapper.readValue(exception.getMessage(), new TypeReference<java.util.Map<String, String>>() {});
		} catch (Exception ignored) {
			return java.util.Map.of("error", exception.getMessage());
		}
	}
}
