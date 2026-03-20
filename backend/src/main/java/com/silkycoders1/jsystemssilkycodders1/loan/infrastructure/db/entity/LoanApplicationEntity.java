package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "loan_applications")
public class LoanApplicationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "customer_id", nullable = false)
	private Long customerId;

	@Column(name = "chat_session_id", nullable = false)
	private String chatSessionId;

	@Column(name = "loan_product_id", nullable = false)
	private Long loanProductId;

	@Column(name = "requested_amount", nullable = false)
	private Integer requestedAmount;

	@Column(name = "requested_term_months", nullable = false)
	private Integer requestedTermMonths;

	@Column(name = "declared_purpose", nullable = false)
	private String declaredPurpose;

	@Column(name = "submitted_by_employee_id", nullable = false)
	private String submittedByEmployeeId;

	@Column(nullable = false)
	private String status;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "employee_final_action")
	private String employeeFinalAction;

	@Column(name = "employee_override_reason")
	private String employeeOverrideReason;

	@Column(name = "employee_action_note")
	private String employeeActionNote;

	@Column(name = "employee_action_at")
	private OffsetDateTime employeeActionAt;

	public static LoanApplicationEntity create(Long customerId, String chatSessionId, Long loanProductId, Integer requestedAmount, Integer requestedTermMonths, String declaredPurpose, String employeeId, String status) {
		var entity = new LoanApplicationEntity();
		entity.customerId = customerId;
		entity.chatSessionId = chatSessionId;
		entity.loanProductId = loanProductId;
		entity.requestedAmount = requestedAmount;
		entity.requestedTermMonths = requestedTermMonths;
		entity.declaredPurpose = declaredPurpose;
		entity.submittedByEmployeeId = employeeId;
		entity.status = status;
		entity.createdAt = OffsetDateTime.now();
		return entity;
	}

	public void recordEmployeeAction(String actionType, String overrideReason, String note) {
		this.employeeFinalAction = actionType;
		this.employeeOverrideReason = overrideReason;
		this.employeeActionNote = note;
		this.employeeActionAt = OffsetDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public String getChatSessionId() {
		return chatSessionId;
	}

	public Long getLoanProductId() {
		return loanProductId;
	}

	public Integer getRequestedAmount() {
		return requestedAmount;
	}

	public Integer getRequestedTermMonths() {
		return requestedTermMonths;
	}

	public String getEmployeeFinalAction() {
		return employeeFinalAction;
	}

	public String getEmployeeOverrideReason() {
		return employeeOverrideReason;
	}

	public String getEmployeeActionNote() {
		return employeeActionNote;
	}

	public OffsetDateTime getEmployeeActionAt() {
		return employeeActionAt;
	}
}
