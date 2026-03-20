import {
  CopilotRuntime,
  ExperimentalEmptyAdapter,
  copilotRuntimeNextJSAppRouterEndpoint
} from "@copilotkit/runtime";
import { HttpAgent } from "@ag-ui/client";
import { NextRequest } from "next/server";
import { initialLoanCopilotState } from "@/lib/loan-copilot/state";

const httpUrl = process.env.AGUI_BACKEND_URL ?? "http://127.0.0.1:8080/sse/loan-decision";

const runtime = new CopilotRuntime({
  agents: {
    agent: new HttpAgent({
      url: httpUrl,
      initialState: initialLoanCopilotState
    }) as never
  }
});

const serviceAdapter = new ExperimentalEmptyAdapter();

export const POST = async (request: NextRequest) => {
  const { handleRequest } = copilotRuntimeNextJSAppRouterEndpoint({
    runtime,
    serviceAdapter,
    endpoint: "/api/copilotkit"
  });

  return handleRequest(request);
};
