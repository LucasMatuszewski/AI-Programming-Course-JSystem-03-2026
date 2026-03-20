"use client";

import React from "react";
import Image from "next/image";

export function LoanTopbar({ employeeId }: { employeeId: string }) {
  return (
    <header className="loan-topbar">
      <div className="topbar-brand">
        <Image
          alt="Narodowy Bank Polski"
          className="topbar-logo"
          height={40}
          priority
          src="/nbp-logo.svg"
          width={174}
        />
        <div className="topbar-brand-copy">
          <p className="topbar-kicker">Internal assistant workspace</p>
          <h1 className="topbar-title">Loan Decision Copilot</h1>
        </div>
      </div>

      <div className="topbar-meta">
        <span className="topbar-badge">MVP Decision Flow</span>
        <span className="source-chip">Employee {employeeId}</span>
      </div>
    </header>
  );
}
