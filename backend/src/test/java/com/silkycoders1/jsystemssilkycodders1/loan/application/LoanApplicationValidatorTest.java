package com.silkycoders1.jsystemssilkycodders1.loan.application;

import com.silkycoders1.jsystemssilkycodders1.loan.api.SubmitLoanApplicationRequest;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.CustomerIdentifierType;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoanApplicationValidatorTest {

	@Test
	void shouldRequireCollateralForLargeLoans() {
		var validator = new LoanApplicationValidator();
		var request = new SubmitLoanApplicationRequest(
				"thread-1",
				"demo-employee-1",
				CustomerIdentifierType.PESEL,
				"90010112345",
				LoanType.PERSONAL_LOAN,
				45000,
				48,
				"Home improvement",
				null,
				null,
				null,
				null,
				null
		);

		var result = validator.validate(request);

		assertThat(result.valid()).isFalse();
		assertThat(result.errors()).containsEntry("collateralDescription", "Collateral description is required for requests above 30000.");
	}

	@Test
	void shouldRequireVehicleFieldsForCarLoan() {
		var validator = new LoanApplicationValidator();
		var request = new SubmitLoanApplicationRequest(
				"thread-1",
				"demo-employee-1",
				CustomerIdentifierType.PESEL,
				"90010112345",
				LoanType.CAR_LOAN,
				25000,
				60,
				"Used family car",
				null,
				null,
				null,
				null,
				null
		);

		var result = validator.validate(request);

		assertThat(result.valid()).isFalse();
		assertThat(result.errors()).containsEntry("vehicleValue", "Vehicle value is required for car loans.");
		assertThat(result.errors()).containsEntry("vehicleAgeYears", "Vehicle age is required for car loans.");
	}
}
