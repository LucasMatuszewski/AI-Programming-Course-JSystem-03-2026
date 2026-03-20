package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_events")
public class AuditEventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "chat_session_id")
	private String chatSessionId;

	@Column(name = "actor_type", nullable = false)
	private String actorType;

	@Column(name = "actor_id", nullable = false)
	private String actorId;

	@Column(name = "event_type", nullable = false)
	private String eventType;

	@Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
	private String payloadJson;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	public static AuditEventEntity create(Long applicationId, String chatSessionId, String actorType, String actorId, String eventType, String payloadJson) {
		var entity = new AuditEventEntity();
		entity.applicationId = applicationId;
		entity.chatSessionId = chatSessionId;
		entity.actorType = actorType;
		entity.actorId = actorId;
		entity.eventType = eventType;
		entity.payloadJson = payloadJson;
		entity.createdAt = OffsetDateTime.now();
		return entity;
	}
}
