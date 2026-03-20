"use client";

import React, { startTransition, useState } from "react";
import { useCoAgent } from "@copilotkit/react-core";
import { LoanConversationPanel } from "@/components/chat/LoanConversationPanel";
import { LoanTopbar } from "@/components/layout/LoanTopbar";
import { CustomerProfileCard } from "@/components/loan/CustomerProfileCard";
import { DecisionSummaryCard } from "@/components/loan/DecisionSummaryCard";
import { EmployeeActionCard } from "@/components/loan/EmployeeActionCard";
import { LoanApplicationFormCard } from "@/components/loan/LoanApplicationFormCard";
import { lookupCustomerProfile, recordEmployeeAction, submitLoanApplication } from "@/lib/loan-copilot/api";
import { initialLoanCopilotState, normalizeLoanCopilotState } from "@/lib/loan-copilot/state";
import {
  EmployeeDecisionAction,
  LoanCopilotState,
  LoanFormValues
} from "@/lib/loan-copilot/types";
import { getVisibleFieldKeys, validateLoanForm } from "@/lib/loan-copilot/validation";

function mergeFormState(state: LoanCopilotState, values: LoanFormValues): LoanCopilotState {
  return {
    ...state,
    showLoanForm: true,
    currentStep: "form",
    form: {
      ...state.form,
      values,
      shownFieldKeys: getVisibleFieldKeys(values),
      errors: {}
    }
  };
}

export function LoanCopilotWorkspace() {
  const { state, setState } = useCoAgent<Record<string, unknown> | LoanCopilotState>({
    name: "agent",
    initialState: initialLoanCopilotState
  });
  const normalizedState = normalizeLoanCopilotState(state as LoanCopilotState | Record<string, unknown> | undefined);
  const [busy, setBusy] = useState(false);
  const [workflowError, setWorkflowError] = useState("");

  const updateField = (field: keyof LoanFormValues, value: string) => {
    startTransition(() => {
      const nextValues = {
        ...normalizedState.form.values,
        [field]: value
      };
      const editedFieldKeys = normalizedState.form.sourceFieldKeys.includes(field)
        ? Array.from(new Set([...normalizedState.form.editedFieldKeys, field]))
        : normalizedState.form.editedFieldKeys;

      setState({
        ...mergeFormState(normalizedState, nextValues),
        form: {
          ...mergeFormState(normalizedState, nextValues).form,
          editedFieldKeys
        }
      });
    });
  };

  const handleLookup = async () => {
    setBusy(true);
    setWorkflowError("");

    try {
      const response = await lookupCustomerProfile({
        identifierType: normalizedState.form.values.identifierType,
        identifierValue: normalizedState.form.values.identifierValue,
        sessionId: normalizedState.sessionId
      });

      startTransition(() => setState(response.state));
    } catch (error) {
      startTransition(() => {
        setState({
          ...normalizedState,
          showLoanForm: true,
          currentStep: "form",
          form: {
            ...normalizedState.form,
            errors: normalizedState.form.values.identifierValue
              ? {}
              : { identifierValue: "Customer identifier is required." },
            statusMessage: "Backend lookup is unavailable. Continue with manual entry if needed."
          }
        });
        setWorkflowError(error instanceof Error ? error.message : "Customer lookup failed.");
      });
    } finally {
      setBusy(false);
    }
  };

  const handleSubmit = async () => {
    const errors = validateLoanForm(normalizedState.form.values);

    if (Object.keys(errors).length > 0) {
      startTransition(() => {
        setState({
          ...normalizedState,
          showLoanForm: true,
          currentStep: "form",
          form: {
            ...normalizedState.form,
            errors
          }
        });
      });
      return;
    }

    setBusy(true);
    setWorkflowError("");

    try {
      const response = await submitLoanApplication({
        ...normalizedState.form.values,
        employeeId: normalizedState.employeeId,
        sessionId: normalizedState.sessionId
      });

      startTransition(() => setState(response.state));
    } catch (error) {
      startTransition(() => {
        setState({
          ...normalizedState,
          showLoanForm: true,
          currentStep: "form",
          form: {
            ...normalizedState.form,
            statusMessage: "Submission requires the backend application contract to be available."
          }
        });
        setWorkflowError(error instanceof Error ? error.message : "Submission failed.");
      });
    } finally {
      setBusy(false);
    }
  };

  const handleAction = async ({
    action,
    overrideReason,
    note
  }: {
    action: EmployeeDecisionAction;
    overrideReason?: string;
    note?: string;
  }) => {
    if (!normalizedState.recommendation?.applicationId) {
      startTransition(() => {
        setState({
          ...normalizedState,
          employeeAction: {
            action,
            overrideReason,
            note,
            recordedAt: new Date().toISOString()
          }
        });
      });
      return;
    }

    try {
      const response = await recordEmployeeAction(normalizedState.recommendation.applicationId, {
        employeeId: normalizedState.employeeId,
        action,
        overrideReason,
        note
      });

      startTransition(() => setState(response.state));
    } catch (error) {
      setWorkflowError(error instanceof Error ? error.message : "Employee action could not be saved.");
    }
  };

  return (
    <main className="loan-shell">
      <LoanTopbar employeeId={normalizedState.employeeId} />

      <section className="loan-hero-panel">
        <div>
          <p className="panel-eyebrow">Guided decisioning</p>
          <h2 className="hero-display">Detect intent in chat, complete the form, and explain the recommendation.</h2>
        </div>
        <p className="hero-copy">
          The workspace keeps the conversation, customer profile, and preliminary decision in one auditable screen.
        </p>
      </section>

      {workflowError ? (
        <div className="workflow-alert" role="alert">
          {workflowError}
        </div>
      ) : null}

      <section className="loan-layout">
        <LoanConversationPanel />

        <aside className="workflow-column">
          <CustomerProfileCard form={normalizedState.form} profile={normalizedState.customerProfile} />

          {normalizedState.showLoanForm || normalizedState.currentStep === "form" ? (
            <LoanApplicationFormCard
              busy={busy}
              form={normalizedState.form}
              onFieldChange={updateField}
              onLookup={handleLookup}
              onSubmit={handleSubmit}
            />
          ) : (
            <section className="workflow-card muted-card">
              <div className="workflow-card-header">
                <div>
                  <p className="panel-eyebrow">Application form</p>
                  <h2 className="panel-title">Waiting for intent</h2>
                </div>
              </div>
              <p className="metric-copy">
                When the assistant detects a loan request, the application form opens here within the same workspace.
              </p>
            </section>
          )}

          {normalizedState.recommendation ? (
            <DecisionSummaryCard recommendation={normalizedState.recommendation} />
          ) : null}

          {normalizedState.recommendation ? (
            <EmployeeActionCard
              applicationId={normalizedState.recommendation.applicationId}
              onSubmit={handleAction}
              recommendationStatus={normalizedState.recommendation.status}
            />
          ) : null}
        </aside>
      </section>
    </main>
  );
}
