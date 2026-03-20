package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "repayment_history")
public class RepaymentHistoryEntity {

	@Id
	private Long id;

	@Column(name = "customer_id", nullable = false)
	private Long customerId;

	@Column(name = "late_payments_12m")
	private Integer latePayments12m;

	@Column(name = "delinquency_flag")
	private Boolean delinquencyFlag;

	@Column(name = "last_delinquency_date")
	private String lastDelinquencyDate;

	public Long getCustomerId() {
		return customerId;
	}

	public Integer getLatePayments12m() {
		return latePayments12m;
	}

	public Boolean getDelinquencyFlag() {
		return delinquencyFlag;
	}

	public String getLastDelinquencyDate() {
		return lastDelinquencyDate;
	}
}
