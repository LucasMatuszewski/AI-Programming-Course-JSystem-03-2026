"use client";

import React, { startTransition, useState } from "react";
import { useCoAgent } from "@copilotkit/react-core";
import { LoanConversationPanel } from "@/components/chat/LoanConversationPanel";
import { CustomerProfileCard } from "@/components/loan/CustomerProfileCard";
import { DecisionSummaryCard } from "@/components/loan/DecisionSummaryCard";
import { EmployeeActionCard } from "@/components/loan/EmployeeActionCard";
import { LoanApplicationFormCard } from "@/components/loan/LoanApplicationFormCard";
import { lookupCustomerProfile, recordEmployeeAction, submitLoanApplication } from "@/lib/loan-copilot/api";
import { initialLoanCopilotState } from "@/lib/loan-copilot/state";
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
  const { state, setState } = useCoAgent<LoanCopilotState>({
    name: "agent",
    initialState: initialLoanCopilotState
  });
  const [busy, setBusy] = useState(false);
  const [workflowError, setWorkflowError] = useState("");

  const updateField = (field: keyof LoanFormValues, value: string) => {
    startTransition(() => {
      const nextValues = {
        ...state.form.values,
        [field]: value
      };
      const editedFieldKeys = state.form.sourceFieldKeys.includes(field)
        ? Array.from(new Set([...state.form.editedFieldKeys, field]))
        : state.form.editedFieldKeys;

      setState({
        ...mergeFormState(state, nextValues),
        form: {
          ...mergeFormState(state, nextValues).form,
          editedFieldKeys
        }
      });
    });
  };

  const openForm = () => {
    startTransition(() => {
      setState({
        ...state,
        showLoanForm: true,
        currentStep: "form",
        form: {
          ...state.form,
          shownFieldKeys: getVisibleFieldKeys(state.form.values),
          statusMessage: "Capture the customer identifier first to request prefilled details."
        }
      });
    });
  };

  const handleLookup = async () => {
    setBusy(true);
    setWorkflowError("");

    try {
      const response = await lookupCustomerProfile({
        identifierType: state.form.values.identifierType,
        identifierValue: state.form.values.identifierValue,
        sessionId: state.sessionId
      });

      startTransition(() => setState(response.state));
    } catch (error) {
      startTransition(() => {
        setState({
          ...state,
          showLoanForm: true,
          currentStep: "form",
          form: {
            ...state.form,
            errors: state.form.values.identifierValue
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
    const errors = validateLoanForm(state.form.values);

    if (Object.keys(errors).length > 0) {
      startTransition(() => {
        setState({
          ...state,
          showLoanForm: true,
          currentStep: "form",
          form: {
            ...state.form,
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
        ...state.form.values,
        employeeId: state.employeeId,
        sessionId: state.sessionId
      });

      startTransition(() => setState(response.state));
    } catch (error) {
      startTransition(() => {
        setState({
          ...state,
          showLoanForm: true,
          currentStep: "form",
          form: {
            ...state.form,
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
    if (!state.recommendation?.applicationId) {
      startTransition(() => {
        setState({
          ...state,
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
      const response = await recordEmployeeAction(state.recommendation.applicationId, {
        employeeId: state.employeeId,
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
      <header className="loan-topbar">
        <div>
          <p className="topbar-kicker">Narodowy Bank Polski</p>
          <h1 className="topbar-title">Loan Decision Copilot</h1>
        </div>
        <div className="topbar-meta">
          <span className="source-chip">Employee {state.employeeId}</span>
          <button className="secondary-action-button" onClick={openForm} type="button">
            Prepare application
          </button>
        </div>
      </header>

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
          <CustomerProfileCard form={state.form} profile={state.customerProfile} />

          {state.showLoanForm || state.currentStep === "form" ? (
            <LoanApplicationFormCard
              busy={busy}
              form={state.form}
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
                When the assistant detects a loan request, the application form should open here within the same workspace.
              </p>
            </section>
          )}

          {state.recommendation ? <DecisionSummaryCard recommendation={state.recommendation} /> : null}

          {state.recommendation ? (
            <EmployeeActionCard
              applicationId={state.recommendation.applicationId}
              onSubmit={handleAction}
              recommendationStatus={state.recommendation.status}
            />
          ) : null}
        </aside>
      </section>
    </main>
  );
}
