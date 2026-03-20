package com.silkycoders1.jsystemssilkycodders1.loan.application;

import com.silkycoders1.jsystemssilkycodders1.loan.domain.IntentLabel;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanIntentResult;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanType;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class IntentDetectionService {

	private static final Set<String> LOAN_HINTS = Set.of(
			"loan", "borrow", "credit", "financing", "finance", "cash loan", "personal loan", "car loan", "installment"
	);

	private static final Set<String> GENERAL_HINTS = Set.of(
			"balance", "statement", "opening hours", "card", "transfer", "account"
	);

	public LoanIntentResult detect(String message) {
		var normalized = message == null ? "" : message.toLowerCase(Locale.ROOT);
		var loanMatches = LOAN_HINTS.stream().filter(normalized::contains).count();
		var generalMatches = GENERAL_HINTS.stream().filter(normalized::contains).count();

		if (loanMatches > 0) {
			var confidence = Math.min(0.95d, 0.72d + (loanMatches * 0.08d));
			return new LoanIntentResult(IntentLabel.LOAN_APPLICATION, confidence, confidence >= 0.75d, inferLoanType(normalized));
		}

		if (generalMatches > 0) {
			return new LoanIntentResult(IntentLabel.GENERAL_QUESTION, 0.35d, false, LoanType.PERSONAL_LOAN);
		}

		return new LoanIntentResult(IntentLabel.OTHER, 0.2d, false, LoanType.PERSONAL_LOAN);
	}

	private LoanType inferLoanType(String normalizedMessage) {
		if (normalizedMessage.contains("car") || normalizedMessage.contains("auto")) {
			return LoanType.CAR_LOAN;
		}
		if (normalizedMessage.contains("cash")) {
			return LoanType.CASH_LOAN;
		}
		return LoanType.PERSONAL_LOAN;
	}
}
