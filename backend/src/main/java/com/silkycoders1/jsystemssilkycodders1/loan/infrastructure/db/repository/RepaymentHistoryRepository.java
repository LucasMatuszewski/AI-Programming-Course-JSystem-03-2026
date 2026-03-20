package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository;

import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.RepaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepaymentHistoryRepository extends JpaRepository<RepaymentHistoryEntity, Long> {
	Optional<RepaymentHistoryEntity> findByCustomerId(Long customerId);
}
