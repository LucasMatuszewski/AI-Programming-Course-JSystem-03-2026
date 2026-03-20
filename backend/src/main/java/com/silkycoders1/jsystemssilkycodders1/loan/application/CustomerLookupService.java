package com.silkycoders1.jsystemssilkycodders1.loan.application;

import com.silkycoders1.jsystemssilkycodders1.loan.domain.CustomerIdentifierType;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity.CustomerEntity;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository.CustomerFinancialProfileRepository;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository.CustomerRepository;
import com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.repository.RepaymentHistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerLookupService {

	private final CustomerRepository customerRepository;
	private final CustomerFinancialProfileRepository financialProfileRepository;
	private final RepaymentHistoryRepository repaymentHistoryRepository;

	public CustomerLookupService(
			CustomerRepository customerRepository,
			CustomerFinancialProfileRepository financialProfileRepository,
			RepaymentHistoryRepository repaymentHistoryRepository
	) {
		this.customerRepository = customerRepository;
		this.financialProfileRepository = financialProfileRepository;
		this.repaymentHistoryRepository = repaymentHistoryRepository;
	}

	public CustomerAggregate findRequired(CustomerIdentifierType identifierType, String identifierValue) {
		var customer = (switch (identifierType) {
			case PESEL -> customerRepository.findByPesel(identifierValue);
			case VAT_ID -> customerRepository.findByVatId(identifierValue);
		}).orElseThrow(() -> new IllegalArgumentException("Customer not found for " + identifierType + ": " + identifierValue));

		return aggregateFor(customer);
	}

	public CustomerAggregate findRequiredByCustomerId(Long customerId) {
		var customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
		return aggregateFor(customer);
	}

	private CustomerAggregate aggregateFor(CustomerEntity customer) {
		var financialProfile = financialProfileRepository.findByCustomerId(customer.getId())
				.orElseThrow(() -> new IllegalArgumentException("Financial profile not found for customer " + customer.getId()));
		var repaymentHistory = repaymentHistoryRepository.findByCustomerId(customer.getId())
				.orElseThrow(() -> new IllegalArgumentException("Repayment history not found for customer " + customer.getId()));
		return new CustomerAggregate(customer, financialProfile, repaymentHistory);
	}
}
