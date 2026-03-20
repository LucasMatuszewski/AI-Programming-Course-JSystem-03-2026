package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository;

import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, String> {
}
