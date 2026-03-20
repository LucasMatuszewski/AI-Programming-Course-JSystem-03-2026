package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository;

import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.LoanProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanProductRepository extends JpaRepository<LoanProductEntity, Long> {
	Optional<LoanProductEntity> findByProductCode(String productCode);
}
