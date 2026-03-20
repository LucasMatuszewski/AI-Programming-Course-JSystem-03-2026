package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository;

import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {
}
