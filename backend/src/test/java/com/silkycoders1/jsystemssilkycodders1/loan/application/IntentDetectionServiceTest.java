package com.silkycoders1.jsystemssilkycodders1.loan.application;

import com.silkycoders1.jsystemssilkycodders1.loan.domain.IntentLabel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntentDetectionServiceTest {

	@Test
	void shouldDetectLoanIntentForBorrowingRequest() {
		var service = new IntentDetectionService();

		var result = service.detect("I want to apply for a personal loan for a home renovation.");

		assertThat(result.intentLabel()).isEqualTo(IntentLabel.LOAN_APPLICATION);
		assertThat(result.shouldShowForm()).isTrue();
		assertThat(result.confidence()).isGreaterThanOrEqualTo(0.75d);
	}

	@Test
	void shouldNotDetectLoanIntentForGeneralMoneyQuestion() {
		var service = new IntentDetectionService();

		var result = service.detect("Can you tell me what my current account balance is?");

		assertThat(result.intentLabel()).isEqualTo(IntentLabel.GENERAL_QUESTION);
		assertThat(result.shouldShowForm()).isFalse();
		assertThat(result.confidence()).isLessThan(0.75d);
	}
}
