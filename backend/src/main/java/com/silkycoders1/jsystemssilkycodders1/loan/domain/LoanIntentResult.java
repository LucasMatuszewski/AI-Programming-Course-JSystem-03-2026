package com.silkycoders1.jsystemssilkycodders1.loan.domain;

public record LoanIntentResult(
		IntentLabel intentLabel,
		double confidence,
		boolean shouldShowForm,
		LoanType inferredLoanType
) {
}
