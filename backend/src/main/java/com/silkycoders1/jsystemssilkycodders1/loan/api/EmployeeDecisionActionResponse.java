package com.silkycoders1.jsystemssilkycodders1.loan.api;

public record EmployeeDecisionActionResponse(
		Long applicationId,
		LoanWorkflowStateResponse state
) {
}
