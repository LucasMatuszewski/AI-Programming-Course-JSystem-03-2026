package com.silkycoders1.jsystemssilkycodders1.loan.application;

import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.CustomerEntity;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.CustomerFinancialProfileEntity;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.RepaymentHistoryEntity;

public record CustomerAggregate(
		CustomerEntity customer,
		CustomerFinancialProfileEntity financialProfile,
		RepaymentHistoryEntity repaymentHistory
) {
}
