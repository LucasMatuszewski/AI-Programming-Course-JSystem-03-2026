package com.silkycoders1.jsystemssilkycodders1.loan.api;

public record LoanApplicationResponse(
		Long applicationId,
		String chatSessionId,
		LoanWorkflowStateResponse state
) {
}
