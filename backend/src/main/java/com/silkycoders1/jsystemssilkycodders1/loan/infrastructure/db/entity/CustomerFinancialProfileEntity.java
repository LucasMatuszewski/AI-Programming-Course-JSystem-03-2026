package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_financial_profiles")
public class CustomerFinancialProfileEntity {

	@Id
	private Long id;

	@Column(name = "customer_id", nullable = false)
	private Long customerId;

	@Column(name = "employment_status")
	private String employmentStatus;

	@Column(name = "employment_months")
	private Integer employmentMonths;

	@Column(name = "monthly_income_net")
	private Integer monthlyIncomeNet;

	@Column(name = "monthly_expenses")
	private Integer monthlyExpenses;

	@Column(name = "existing_liabilities_total")
	private Integer existingLiabilitiesTotal;

	@Column(name = "has_income_verification")
	private Boolean hasIncomeVerification;

	@Column(name = "credit_history_length_months")
	private Integer creditHistoryLengthMonths;

	public Long getCustomerId() {
		return customerId;
	}

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public Integer getEmploymentMonths() {
		return employmentMonths;
	}

	public Integer getMonthlyIncomeNet() {
		return monthlyIncomeNet;
	}

	public Integer getMonthlyExpenses() {
		return monthlyExpenses;
	}

	public Integer getExistingLiabilitiesTotal() {
		return existingLiabilitiesTotal;
	}

	public Boolean getHasIncomeVerification() {
		return hasIncomeVerification;
	}

	public Integer getCreditHistoryLengthMonths() {
		return creditHistoryLengthMonths;
	}
}
