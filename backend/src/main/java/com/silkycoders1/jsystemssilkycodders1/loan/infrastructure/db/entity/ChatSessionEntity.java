package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_sessions")
public class ChatSessionEntity {

	@Id
	private String id;

	@Column(name = "employee_id", nullable = false)
	private String employeeId;

	@Column(name = "customer_identifier")
	private String customerIdentifier;

	@Column(name = "customer_identifier_type")
	private String customerIdentifierType;

	@Column(nullable = false)
	private String status;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	public static ChatSessionEntity create(String id, String employeeId, String customerIdentifier, String customerIdentifierType, String status) {
		var entity = new ChatSessionEntity();
		entity.id = id == null || id.isBlank() ? UUID.randomUUID().toString() : id;
		entity.employeeId = employeeId;
		entity.customerIdentifier = customerIdentifier;
		entity.customerIdentifierType = customerIdentifierType;
		entity.status = status;
		entity.createdAt = OffsetDateTime.now();
		return entity;
	}

	public String getId() {
		return id;
	}
}
