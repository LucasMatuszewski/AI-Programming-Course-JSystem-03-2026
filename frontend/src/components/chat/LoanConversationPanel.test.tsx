import React from "react";
import { render, screen } from "@testing-library/react";
import { vi } from "vitest";
import { LoanConversationPanel } from "./LoanConversationPanel";

const copilotChatMock = vi.fn((props: Record<string, unknown>) => (
  <div data-testid="copilot-chat-mock">{JSON.stringify(props)}</div>
));

vi.mock("@copilotkit/react-ui", () => ({
  CopilotChat: (props: Record<string, unknown>) => copilotChatMock(props)
}));

describe("LoanConversationPanel", () => {
  it("uses an embedded CopilotChat instead of the floating sidebar variant", () => {
    render(<LoanConversationPanel />);

    expect(screen.getByText("Customer dialogue")).toBeInTheDocument();
    expect(screen.getByTestId("copilot-chat-mock")).toBeInTheDocument();

    const props = copilotChatMock.mock.calls[0][0] as {
      className?: string;
      labels?: Record<string, string>;
      instructions?: string;
    };

    expect(props.className).toBe("loan-copilot-chat");
    expect(props.labels?.title).toBe("Loan Decision Copilot");
    expect(props.instructions).toContain("classify loan intent");
  });
});
