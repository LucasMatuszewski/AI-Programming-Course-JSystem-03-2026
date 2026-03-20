package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "loan_products")
public class LoanProductEntity {

	@Id
	private Long id;

	@Column(name = "product_code", nullable = false)
	private String productCode;

	@Column(name = "display_name", nullable = false)
	private String displayName;

	@Column(name = "min_amount", nullable = false)
	private Integer minAmount;

	@Column(name = "max_amount", nullable = false)
	private Integer maxAmount;

	@Column(name = "default_term_min", nullable = false)
	private Integer defaultTermMin;

	@Column(name = "default_term_max", nullable = false)
	private Integer defaultTermMax;

	public Long getId() {
		return id;
	}

	public String getProductCode() {
		return productCode;
	}

	public Integer getMinAmount() {
		return minAmount;
	}

	public Integer getMaxAmount() {
		return maxAmount;
	}
}
