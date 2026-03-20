package com.silkycoders1.jsystemssilkycodders1.loan.api;

public record CustomerProfileResponse(
		Long customerId,
		String fullName,
		String identifierType,
		String identifierValue,
		String employmentStatus,
		Integer employmentMonths,
		Integer monthlyIncomeNet,
		Integer monthlyExpenses,
		Integer existingLiabilitiesTotal,
		Boolean hasIncomeVerification,
		Integer creditHistoryLengthMonths,
		Integer latePayments12m,
		Boolean delinquencyFlag
) {
}
