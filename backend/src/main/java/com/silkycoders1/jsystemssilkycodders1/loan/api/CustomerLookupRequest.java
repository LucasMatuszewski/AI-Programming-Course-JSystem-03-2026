package com.silkycoders1.jsystemssilkycodders1.loan.api;

import com.silkycoders1.jsystemssilkycodders1.loan.domain.CustomerIdentifierType;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanType;

public record CustomerLookupRequest(
		String chatSessionId,
		String employeeUserId,
		CustomerIdentifierType identifierType,
		String identifierValue,
		LoanType loanType,
		Integer requestedAmount
) {
}
