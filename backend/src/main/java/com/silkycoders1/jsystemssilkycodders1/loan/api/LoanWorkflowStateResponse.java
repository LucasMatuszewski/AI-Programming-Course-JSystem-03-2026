package com.silkycoders1.jsystemssilkycodders1.loan.api;

public record LoanWorkflowStateResponse(
		boolean showLoanForm,
		String currentStep,
		String assistantMessage,
		IntentStateResponse intent,
		FormStateResponse form,
		CustomerProfileResponse customerProfile,
		RecommendationResponse recommendation,
		EmployeeActionStateResponse employeeAction
) {
}
