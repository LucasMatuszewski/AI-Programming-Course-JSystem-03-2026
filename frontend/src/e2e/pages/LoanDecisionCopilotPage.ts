import { expect, type Locator, type Page } from "@playwright/test";

export class LoanDecisionCopilotPage {
  readonly page: Page;

  constructor(page: Page) {
    this.page = page;
  }

  async goto() {
    await this.page.goto("/", { waitUntil: "domcontentloaded", timeout: 60_000 });
  }

  chatInput(): Locator {
    return this.page.getByRole("textbox").last();
  }

  sendButton(): Locator {
    return this.page.getByRole("button", { name: /send/i });
  }

  loanForm(): Locator {
    return this.page.getByTestId("loan-application-form");
  }

  recommendationCard(): Locator {
    return this.page.getByTestId("decision-summary-card");
  }

  prepareApplicationButton(): Locator {
    return this.page.getByRole("button", { name: "Prepare application" });
  }

  submitApplicationButton(): Locator {
    return this.page.getByRole("button", { name: "Submit application" });
  }

  lookupCustomerButton(): Locator {
    return this.page.getByRole("button", { name: "Lookup customer" });
  }

  async openApplicationForm() {
    await this.prepareApplicationButton().click();
    await expect(this.page.getByTestId("loan-application-form-card")).toBeVisible();
  }

  async assertShellLoaded() {
    await expect(this.page.getByRole("heading", { name: "Loan Decision Copilot" }).first()).toBeVisible();
  }
}
