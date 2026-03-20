package com.silkycoders1.jsystemssilkycodders1.loan.domain;

import java.util.List;

public record FormDefinition(
		String formVersion,
		List<String> shownFields,
		List<String> requiredFields,
		String amountBand
) {
}
