package com.silkycoders1.jsystemssilkycodders1.loan.domain;

public record LoanProductTerms(
		LoanType loanType,
		int requestedAmount,
		int requestedTermMonths
) {
}
