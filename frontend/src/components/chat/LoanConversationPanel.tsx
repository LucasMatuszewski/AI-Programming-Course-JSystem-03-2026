"use client";

import React from "react";
import { CopilotChat } from "@copilotkit/react-ui";

export function LoanConversationPanel() {
  return (
    <section className="conversation-panel" data-testid="loan-conversation-panel">
      <div className="panel-header">
        <div>
          <p className="panel-eyebrow">Conversation</p>
          <h2 className="panel-title">Customer dialogue</h2>
        </div>
      </div>
      <CopilotChat
        className="loan-copilot-chat"
        instructions="You help bank staff classify loan intent, gather missing application details, and explain preliminary recommendations in plain language."
        labels={{
          title: "Loan Decision Copilot",
          initial: "Try: The customer wants a personal loan for 20,000 and would like to know if they qualify.",
          placeholder: "Describe the customer's request..."
        }}
      />
    </section>
  );
}
