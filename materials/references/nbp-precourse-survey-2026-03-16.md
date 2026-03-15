# NBP Pre-course Survey Summary (AI dla programistów, od pomysłu do MVP)

Date context: before training starting 2026-03-16
Responses received: 3

## Raw response snapshots (normalized)

### Respondent A (Michał) — 2026-03-12 14:20
- AI level: Beginner
- Area: Full-stack / Back-end / DevOps
- Languages: Java, SQL
- OS: Windows
- IDE: IntelliJ IDEA
- Tooling: GitHub, Jira, Jenkins
- Projects: Internal tools, desktop apps, mobile apps
- AI used: ChatGPT App
- Wants to learn: Claude Code, Codex, Junie (+ trainer recommendations)
- Paid licenses: ChatGPT Plus/Teams/Ultra (private)
- AI use cases: autocomplete, learning, research, data analysis
- Company AI status: just starting
- Goals:
  - well-organized AI SDLC workflow,
  - tools/workflow comparison,
  - risk assessment,
  - security analysis usage.
- Pain points:
  - code generation,
  - repetitive task automation,
  - delivery speed,
  - security vulnerability analysis.

### Respondent B (Łukasz) — 2026-03-12 06:10
- AI level: Intermediate
- Area: Back-end
- Languages: Java, JavaScript/TypeScript
- OS: Windows
- IDE: IntelliJ IDEA
- Tooling: GitHub, Jira, Jenkins (all on-prem)
- System context:
  - banking system,
  - backend Java Spring Boot on Kubernetes,
  - CI/CD Jenkins -> Argo CD,
  - frontend Angular (also Jenkins -> Argo CD -> Kubernetes).
- AI used: ChatGPT App
- Wants from training:
  - better practical AI tooling usage,
  - full system generation (React/Angular + Java backend),
  - mobile app generation ideas.

### Respondent C — 2026-03-10 16:00
- AI level: Beginner
- Area: Back-end
- Languages: SQL, Bash/Shell, Other
- OS: Windows
- IDE/editor: Notepad++
- Tooling: Jira, Jenkins
- AI used: ChatGPT App
- AI use case: Research
- Company AI status: just starting

---

## Cross-response insights

1. **Skill profile**
- 2x beginner, 1x intermediate.
- Need clear fundamentals + practical examples.

2. **Stack profile**
- Strong Java + SQL + Windows + IntelliJ baseline.
- Angular appears in real production context (banking stack).

3. **Delivery environment**
- Strong on-prem bias (Jenkins/Jira/GitHub, Argo CD in at least one response).
- Course should explicitly map cloud-max workflows to on-prem adaptations.

4. **AI maturity**
- Mostly early stage, high curiosity, low standardized process.
- Strong demand for secure AI SDLC and operational governance.

5. **High-value themes to emphasize**
- codex-first process orchestration,
- security audit workflows (legacy + new app),
- CI/CD integration patterns (cloud and on-prem),
- risk-aware automation (not blind autonomy).

---

## Training implications (decisions)

### A) Frontend approach (Angular vs React)
- Keep **React-based teaching path** for live build due to trainer fluency and existing assets.
- Add explicit “Angular translation notes” blocks in scripts:
  - component mapping,
  - state/form equivalents,
  - API integration patterns,
  - where prompt templates should be adjusted.
- Add short research artifact listing Angular-compatible AI/UI integration options and practical alternatives.

### B) Mobile asks
- No full mobile track (out of scope vs 5-day objective and official program).
- Include one optional “mobile extension idea” at end-of-day (10–15 min) as adaptation pattern, not core workshop lane.

### C) Security
- Add two explicit security audit exercises:
  1. legacy JFTP audit (threats + remediation plan),
  2. final app audit (guardrails, secrets, input validation, abuse paths).

### D) On-prem continuity
- For each cloud demo, add short “how to adapt on-prem/local-model” note.
- Include practical references for Jenkins + GitHub Enterprise + local model endpoints.

---

## Source note
This file was compiled from the pre-course survey answers shared by Lucas in chat.
