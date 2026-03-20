package com.silkycoders1.jsystemssilkycodders1.loan.api;

import com.silkycoders1.jsystemssilkycodders1.loan.domain.CustomerIdentifierType;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanType;

public record SubmitLoanApplicationRequest(
		String chatSessionId,
		String employeeUserId,
		CustomerIdentifierType identifierType,
		String identifierValue,
		LoanType loanType,
		Integer requestedAmount,
		Integer requestedTermMonths,
		String declaredPurpose,
		Integer vehicleValue,
		Integer vehicleAgeYears,
		String collateralDescription,
		Integer statedMonthlyIncomeNet,
		Integer statedExistingLiabilitiesTotal
) {
}
