package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository;

import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.DecisionResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DecisionResultRepository extends JpaRepository<DecisionResultEntity, Long> {
	Optional<DecisionResultEntity> findByApplicationId(Long applicationId);
}
