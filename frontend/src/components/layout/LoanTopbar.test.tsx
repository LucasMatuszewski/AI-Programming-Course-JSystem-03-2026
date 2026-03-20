import React from "react";
import { render, screen } from "@testing-library/react";
import { vi } from "vitest";
import { LoanTopbar } from "./LoanTopbar";

vi.mock("next/image", () => ({
  default: ({
    priority,
    alt,
    ...props
  }: React.ImgHTMLAttributes<HTMLImageElement> & { priority?: boolean }) => {
    void priority;

    return React.createElement("img", { alt: alt ?? "", ...props });
  }
}));

describe("LoanTopbar", () => {
  it("renders the NBP logo and employee context", () => {
    render(<LoanTopbar employeeId="demo-employee-1" />);

    expect(screen.getByAltText("Narodowy Bank Polski")).toBeInTheDocument();
    expect(screen.getByText("Loan Decision Copilot")).toBeInTheDocument();
    expect(screen.getByText("Employee demo-employee-1")).toBeInTheDocument();
  });
});
