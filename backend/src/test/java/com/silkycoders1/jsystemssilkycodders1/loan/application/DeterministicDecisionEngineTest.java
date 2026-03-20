package com.silkycoders1.jsystemssilkycodders1.loan.application;

import com.silkycoders1.jsystemssilkycodders1.loan.domain.CustomerFinancialSnapshot;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanDecision;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanProductTerms;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanType;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.RecommendationStatus;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.RepaymentHistorySnapshot;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeterministicDecisionEngineTest {

	private final DeterministicDecisionEngine engine = new DeterministicDecisionEngine();

	@Test
	void shouldApproveStrongApplicant() {
		var decision = engine.evaluate(
				new LoanProductTerms(LoanType.PERSONAL_LOAN, 20000, 36),
				new CustomerFinancialSnapshot("FULL_TIME", 48, 8500, 2600, 500, true, 60),
				new RepaymentHistorySnapshot(0, false, null)
		);

		assertThat(decision.recommendationStatus()).isEqualTo(RecommendationStatus.APPROVE);
		assertThat(decision.score()).isGreaterThanOrEqualTo(70);
		assertThat(decision.topFactors()).isNotEmpty();
	}

	@Test
	void shouldRejectApplicantWithDelinquency() {
		var decision = engine.evaluate(
				new LoanProductTerms(LoanType.CAR_LOAN, 25000, 48),
				new CustomerFinancialSnapshot("FULL_TIME", 36, 7000, 2500, 1000, true, 48),
				new RepaymentHistorySnapshot(3, true, "2025-11-10")
		);

		assertThat(decision.recommendationStatus()).isEqualTo(RecommendationStatus.REJECT);
		assertThat(decision.explanation()).containsIgnoringCase("repayment");
	}

	@Test
	void shouldReturnNeedsVerificationWhenIncomeVerificationMissing() {
		LoanDecision decision = engine.evaluate(
				new LoanProductTerms(LoanType.CASH_LOAN, 15000, 24),
				new CustomerFinancialSnapshot("SELF_EMPLOYED", 18, 6200, 2400, 300, false, 30),
				new RepaymentHistorySnapshot(0, false, null)
		);

		assertThat(decision.recommendationStatus()).isEqualTo(RecommendationStatus.NEEDS_VERIFICATION);
		assertThat(decision.nextSteps()).isNotBlank();
	}
}
