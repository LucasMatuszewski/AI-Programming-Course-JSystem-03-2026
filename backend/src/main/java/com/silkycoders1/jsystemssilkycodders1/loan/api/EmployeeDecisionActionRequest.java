package com.silkycoders1.jsystemssilkycodders1.loan.api;

import com.silkycoders1.jsystemssilkycodders1.loan.domain.EmployeeActionType;

public record EmployeeDecisionActionRequest(
		String employeeUserId,
		EmployeeActionType actionType,
		String overrideReason,
		String note
) {
}
