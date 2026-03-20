package com.silkycoders1.jsystemssilkycodders1.loan.domain;

public record RepaymentHistorySnapshot(
		Integer latePayments12m,
		Boolean delinquencyFlag,
		String lastDelinquencyDate
) {
}
