package com.silkycoders1.jsystemssilkycodders1.loan.application;

import com.silkycoders1.jsystemssilkycodders1.loan.domain.CustomerFinancialSnapshot;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanDecision;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.LoanProductTerms;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.RecommendationStatus;
import com.silkycoders1.jsystemssilkycodders1.loan.domain.RepaymentHistorySnapshot;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeterministicDecisionEngine {

	public LoanDecision evaluate(
			LoanProductTerms loanTerms,
			CustomerFinancialSnapshot financialSnapshot,
			RepaymentHistorySnapshot repaymentHistory
	) {
		var topFactors = new ArrayList<String>();
		var score = 100;
		var projectedInstallment = projectedInstallment(loanTerms.requestedAmount(), loanTerms.requestedTermMonths());
		var liabilities = valueOrZero(financialSnapshot.existingLiabilitiesTotal());
		var expenses = valueOrZero(financialSnapshot.monthlyExpenses());
		var income = valueOrZero(financialSnapshot.monthlyIncomeNet());
		var dti = income == 0 ? 1d : (double) (liabilities + projectedInstallment) / income;
		var disposableIncome = income - expenses - liabilities;

		if (financialSnapshot.hasIncomeVerification() == null || !financialSnapshot.hasIncomeVerification()) {
			score -= 20;
			topFactors.add("Income verification is missing or outdated.");
			return verificationDecision(score, topFactors, "Provide the latest income verification document.");
		}

		if (financialSnapshot.employmentMonths() == null || financialSnapshot.employmentMonths() < 6) {
			score -= 15;
			topFactors.add("Employment stability is too short for an immediate approval.");
			return verificationDecision(score, topFactors, "Confirm employment stability or collect additional proof of income.");
		}

		if (repaymentHistory.delinquencyFlag() != null && repaymentHistory.delinquencyFlag()) {
			score -= 40;
			topFactors.add("Recent repayment issues indicate elevated credit risk.");
			return rejectDecision(score, topFactors, "Review repayment history before reconsidering the case.");
		}

		if (valueOrZero(repaymentHistory.latePayments12m()) >= 2) {
			score -= 35;
			topFactors.add("Multiple late payments were recorded in the last 12 months.");
			return rejectDecision(score, topFactors, "Review repayment behaviour and discuss debt restructuring options.");
		}

		if (dti > 0.55d) {
			score -= 25;
			topFactors.add("Debt-to-income ratio exceeds the acceptable threshold.");
			return rejectDecision(score, topFactors, "Reduce the requested amount or review current liabilities.");
		}

		if (disposableIncome < projectedInstallment) {
			score -= 25;
			topFactors.add("Disposable income does not cover the projected installment.");
			return rejectDecision(score, topFactors, "Confirm lower expenses or adjust requested loan terms.");
		}

		if (dti <= 0.35d) {
			score += 10;
			topFactors.add("Debt-to-income ratio is within the preferred range.");
		}

		if (valueOrZero(financialSnapshot.creditHistoryLengthMonths()) >= 24) {
			score += 10;
			topFactors.add("Credit history is long enough to support a stable assessment.");
		}

		score = Math.max(0, Math.min(120, score));
		if (score >= 70) {
			if (topFactors.isEmpty()) {
				topFactors.add("Stable affordability and clean repayment behaviour support approval.");
			}
			return new LoanDecision(
					score,
					RecommendationStatus.APPROVE,
					topFactors.stream().limit(4).toList(),
					"Approve based on strong affordability, stable employment, and clean repayment behaviour.",
					"Proceed with the standard application handoff.",
					"LOW_RISK",
					0.82d
			);
		}

		topFactors.add("Additional review is required before the case can be finalized.");
		return verificationDecision(score, topFactors, "Collect supporting documents and confirm affordability details.");
	}

	private LoanDecision rejectDecision(int score, List<String> topFactors, String nextSteps) {
		return new LoanDecision(
				Math.max(0, score),
				RecommendationStatus.REJECT,
				topFactors.stream().limit(4).toList(),
				"Reject because the current financial profile does not meet the MVP affordability and repayment rules.",
				nextSteps,
				"HIGH_RISK",
				0.9d
		);
	}

	private LoanDecision verificationDecision(int score, List<String> topFactors, String nextSteps) {
		return new LoanDecision(
				Math.max(0, score),
				RecommendationStatus.NEEDS_VERIFICATION,
				topFactors.stream().limit(4).toList(),
				"Needs verification because one or more critical decision inputs are missing or require confirmation.",
				nextSteps,
				"MEDIUM_RISK",
				0.7d
		);
	}

	private int projectedInstallment(int amount, int termMonths) {
		return Math.max(1, (int) Math.ceil((amount * 1.08d) / termMonths));
	}

	private int valueOrZero(Integer value) {
		return value == null ? 0 : value;
	}
}
