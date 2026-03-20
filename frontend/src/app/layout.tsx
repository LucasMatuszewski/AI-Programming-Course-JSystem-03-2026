import type { Metadata } from "next";
import { CopilotKit } from "@copilotkit/react-core";
import { brygada1918, libreFranklin } from "@/lib/fonts";
import "@copilotkit/react-ui/styles.css";
import "./globals.css";

export const metadata: Metadata = {
  title: "Loan Decision Copilot",
  description: "NBP-aligned loan decision workspace powered by CopilotKit and AG-UI."
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html className={`${libreFranklin.variable} ${brygada1918.variable}`} lang="en">
      <body>
        <CopilotKit runtimeUrl="/api/copilotkit" agent="agent">
          {children}
        </CopilotKit>
      </body>
    </html>
  );
}
