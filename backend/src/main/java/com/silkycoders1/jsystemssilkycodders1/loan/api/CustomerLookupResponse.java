package com.silkycoders1.jsystemssilkycodders1.loan.api;

public record CustomerLookupResponse(
		String chatSessionId,
		LoanWorkflowStateResponse state
) {
}
