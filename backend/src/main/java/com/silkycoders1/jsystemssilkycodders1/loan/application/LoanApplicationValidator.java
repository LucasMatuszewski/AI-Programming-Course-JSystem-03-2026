package com.silkycoders1.jsystemssilkycodders1.loan.application;

import com.silkycoders1.jsystemssilkycodders1.loan.api.SubmitLoanApplicationRequest;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.FormDefinition;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanType;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

@Service
public class LoanApplicationValidator {

	public ValidationResult validate(SubmitLoanApplicationRequest request) {
		var errors = new LinkedHashMap<String, String>();
		validateRequiredText(request.chatSessionId(), "chatSessionId", "Chat session id is required.", errors);
		validateRequiredText(request.employeeUserId(), "employeeUserId", "Employee user id is required.", errors);
		if (request.identifierType() == null) {
			errors.put("identifierType", "Identifier type is required.");
		}
		validateRequiredText(request.identifierValue(), "identifierValue", "Identifier value is required.", errors);
		if (request.loanType() == null) {
			errors.put("loanType", "Loan type is required.");
		}
		if (request.requestedAmount() == null) {
			errors.put("requestedAmount", "Requested amount is required.");
		} else if (request.requestedAmount() < 5000 || request.requestedAmount() > 50000) {
			errors.put("requestedAmount", "Requested amount must be between 5000 and 50000.");
		}
		if (request.requestedTermMonths() == null) {
			errors.put("requestedTermMonths", "Requested term is required.");
		} else if (request.requestedTermMonths() < 12 || request.requestedTermMonths() > 72) {
			errors.put("requestedTermMonths", "Requested term must be between 12 and 72 months.");
		}
		validateRequiredText(request.declaredPurpose(), "declaredPurpose", "Declared purpose is required.", errors);

		if (request.loanType() == LoanType.CAR_LOAN) {
			if (request.vehicleValue() == null) {
				errors.put("vehicleValue", "Vehicle value is required for car loans.");
			}
			if (request.vehicleAgeYears() == null) {
				errors.put("vehicleAgeYears", "Vehicle age is required for car loans.");
			}
		}

		if (request.requestedAmount() != null && request.requestedAmount() > 30000 && isBlank(request.collateralDescription())) {
			errors.put("collateralDescription", "Collateral description is required for requests above 30000.");
		}

		return new ValidationResult(errors.isEmpty(), errors);
	}

	public FormDefinition formDefinition(LoanType loanType, Integer requestedAmount) {
		var shownFields = new java.util.ArrayList<>(List.of(
				"identifierType",
				"identifierValue",
				"loanType",
				"requestedAmount",
				"requestedTermMonths",
				"declaredPurpose"
		));
		var requiredFields = new java.util.ArrayList<>(shownFields);
		if (loanType == LoanType.CAR_LOAN) {
			shownFields.add("vehicleValue");
			shownFields.add("vehicleAgeYears");
			requiredFields.add("vehicleValue");
			requiredFields.add("vehicleAgeYears");
		}
		if (requestedAmount != null && requestedAmount > 30000) {
			shownFields.add("collateralDescription");
			requiredFields.add("collateralDescription");
		}
		var amountBand = requestedAmount == null ? "STANDARD" : requestedAmount > 30000 ? "HIGH_VALUE" : "STANDARD";
		return new FormDefinition("loan-application-v1", shownFields, requiredFields, amountBand);
	}

	private void validateRequiredText(String value, String field, String error, LinkedHashMap<String, String> errors) {
		if (isBlank(value)) {
			errors.put(field, error);
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
