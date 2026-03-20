package com.silkycoders1.jsystemssilkycodders1.loan.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.AuditEventEntity;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository.AuditEventRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

	private final AuditEventRepository auditEventRepository;
	private final ObjectMapper objectMapper;

	public AuditService(AuditEventRepository auditEventRepository, ObjectMapper objectMapper) {
		this.auditEventRepository = auditEventRepository;
		this.objectMapper = objectMapper;
	}

	public void record(Long applicationId, String chatSessionId, String actorId, String eventType, Object payload) {
		try {
			var payloadJson = objectMapper.writeValueAsString(payload);
			auditEventRepository.save(AuditEventEntity.create(applicationId, chatSessionId, "EMPLOYEE", actorId, eventType, payloadJson));
		} catch (Exception exception) {
			throw new IllegalStateException("Unable to persist audit payload for event " + eventType, exception);
		}
	}
}
