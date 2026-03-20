import {
  CustomerLookupPayload,
  LoanCopilotApiResponse,
  RecordEmployeeActionPayload,
  SubmitLoanApplicationPayload
} from "./types";

const loanApiBaseUrl = process.env.NEXT_PUBLIC_LOAN_API_BASE_URL ?? "http://127.0.0.1:8080/api/loan";

async function postJson<T>(path: string, body: unknown): Promise<T> {
  const response = await fetch(`${loanApiBaseUrl}${path}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  });

  if (!response.ok) {
    const errorMessage = await response.text();
    throw new Error(errorMessage || `Request failed with status ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export function lookupCustomerProfile(payload: CustomerLookupPayload) {
  return postJson<LoanCopilotApiResponse>("/customers/lookup", payload);
}

export function submitLoanApplication(payload: SubmitLoanApplicationPayload) {
  return postJson<LoanCopilotApiResponse>("/applications", payload);
}

export function recordEmployeeAction(applicationId: string, payload: RecordEmployeeActionPayload) {
  return postJson<LoanCopilotApiResponse>(`/applications/${applicationId}/actions`, payload);
}
