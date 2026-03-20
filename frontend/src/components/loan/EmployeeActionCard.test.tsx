import React from "react";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { EmployeeActionCard } from "./EmployeeActionCard";

describe("EmployeeActionCard", () => {
  it("requires an override reason before submit", async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();

    render(
      <EmployeeActionCard
        applicationId="APP-42"
        onSubmit={onSubmit}
        recommendationStatus="REJECT"
      />
    );

    await user.click(screen.getByRole("button", { name: "Override recommendation" }));
    await user.click(screen.getByRole("button", { name: "Save employee action" }));

    expect(onSubmit).not.toHaveBeenCalled();
    expect(screen.getByText("Override reason is required.")).toBeInTheDocument();
  });
});
