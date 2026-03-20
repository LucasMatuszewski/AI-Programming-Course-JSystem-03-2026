package com.silkycoders1.jsystemssilkycodders1.loan.api;

import java.util.List;
import java.util.Map;

public record FormStateResponse(
		String formVersion,
		String loanType,
		String amountBand,
		List<String> shownFields,
		List<String> requiredFields,
		List<String> prefilledFields,
		Map<String, Object> values,
		Map<String, String> validationErrors
) {
}
