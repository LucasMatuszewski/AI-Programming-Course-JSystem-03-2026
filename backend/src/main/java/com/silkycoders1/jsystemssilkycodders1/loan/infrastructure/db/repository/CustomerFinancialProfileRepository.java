package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository;

import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.CustomerFinancialProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerFinancialProfileRepository extends JpaRepository<CustomerFinancialProfileEntity, Long> {
	Optional<CustomerFinancialProfileEntity> findByCustomerId(Long customerId);
}
