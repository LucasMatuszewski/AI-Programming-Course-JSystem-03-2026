package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "customers")
public class CustomerEntity {

	@Id
	private Long id;

	@Column(name = "customer_type", nullable = false)
	private String customerType;

	@Column(name = "full_name", nullable = false)
	private String fullName;

	private String pesel;

	@Column(name = "vat_id")
	private String vatId;

	private String email;
	private String phone;

	@Column(name = "date_of_birth")
	private String dateOfBirth;

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	public Long getId() {
		return id;
	}

	public String getCustomerType() {
		return customerType;
	}

	public String getFullName() {
		return fullName;
	}

	public String getPesel() {
		return pesel;
	}

	public String getVatId() {
		return vatId;
	}
}
