package com.silkycoders1.jsystemssilkycodders1.loan.api;

public record IntentStateResponse(
		String label,
		double confidence,
		boolean thresholdMet
) {
}
