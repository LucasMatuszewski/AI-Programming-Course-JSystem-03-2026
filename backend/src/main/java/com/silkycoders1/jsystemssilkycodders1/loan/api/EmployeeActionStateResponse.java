package com.silkycoders1.jsystemssilkycodders1.loan.api;

public record EmployeeActionStateResponse(
		String actionType,
		String overrideReason,
		String note,
		String actionTimestamp
) {
}
