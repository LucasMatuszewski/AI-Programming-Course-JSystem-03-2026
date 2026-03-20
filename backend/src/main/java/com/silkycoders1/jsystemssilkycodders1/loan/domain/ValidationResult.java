package com.silkycoders1.jsystemssilkycodders1.loan.domain;

import java.util.Map;

public record ValidationResult(
		boolean valid,
		Map<String, String> errors
) {
}
