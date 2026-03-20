package com.silkycoders1.jsystemssilkycodders1.loan.domain;

public record CustomerFinancialSnapshot(
		String employmentStatus,
		Integer employmentMonths,
		Integer monthlyIncomeNet,
		Integer monthlyExpenses,
		Integer existingLiabilitiesTotal,
		Boolean hasIncomeVerification,
		Integer creditHistoryLengthMonths
) {
}
