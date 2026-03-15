# Angular AI UI options + why React in class (NBP D2 note)

## Cel notatki
Krótka ściąga dla trenera: dlaczego na warsztacie pokazujemy frontend na React, jak to uczciwie przełożyć na zespoły Angular, i po co w Day 1/2 pytamy o realia on-prem.

## 1) Dlaczego React na kursie (a nie pełny tor Angular)
1. **Didaktyka i tempo 5-dniowe:** React jest tu nośnikiem demonstracyjnym, żeby szybciej zamknąć pętlę end-to-end (UI → API → test → commit).
2. **Cel kursu to workflow agentowy, nie „wojna frameworków”:** uczymy metodyki (mały slice, dobry kontekst, walidacja diffu, testy, approvals).
3. **Przenaszalność decyzji:** kontrakty API, ADR-y, kryteria akceptacji, guardraile i checklista bezpieczeństwa są niezależne od frameworka UI.

## 2) Jak tłumaczyć transfer React → Angular w projektach uczestników
- Co przenosi się 1:1:
  - definicja use case i kryteria akceptacji,
  - kontrakty API i modele danych,
  - zasady promptowania agentów,
  - pętla jakości: prompt → diff → test → poprawka → commit,
  - zasady audytu i bezpieczeństwa.
- Co mapujemy technicznie:
  - React component/state → Angular component + signals/RxJS,
  - React hooks/data fetching → Angular service + HttpClient,
  - UI event flow pozostaje ten sam (zmienia się składnia i wzorce frameworka).

## 3) Krótkie opcje AI UI dla zespołów Angular (research)

### Opcja A — AI SDK (Vercel) + Angular
- W AI SDK 5 ogłoszono typed chat integracje także dla Angular.
- Daje szybki start dla chat/streaming i spójny model pracy z backendem AI.
- Dobra opcja na PoC i zespoły, które chcą „framework-supported path”.

### Opcja B — CopilotKit (React + Angular)
- CopilotKit pozycjonuje się jako full-stack frontend dla agentów i deklaruje wsparcie React + Angular.
- Sensowne, gdy priorytetem jest generative UI / agent-human interaction i gotowe abstrakcje.
- Przed wdrożeniem enterprise: zweryfikować aktualny zakres funkcji/licencję dla Angular w dacie projektu.

### Opcja C — „Backend-first” + własny Angular UI
- Agent/LLM logikę trzymać po stronie backendu (Node/Java), a Angular traktować jako kontrolowany klient.
- Najbardziej przewidywalna ścieżka dla środowisk regulowanych (łatwiejsza kontrola kluczy, logów, audytu).
- Najmniej „magii”, najwięcej odpowiedzialności po stronie zespołu.

## 4) Dlaczego pytamy o on-prem już na starcie
Pytanie „czy narzędzia/model mają działać on-prem?” nie jest detalem technicznym — to pytanie o architekturę i ryzyko:
- gdzie mogą trafić prompty/dane,
- jakie są wymagania compliance i audytowe,
- jaki pipeline CI/CD i approvals są możliwe (np. Jenkins/GitHub Enterprise/Argo),
- czy projekt ma iść torem cloud-max, czy od razu torem enterprise/local models.

Dzięki temu od początku ustawiamy właściwe ograniczenia dla AI i unikamy późniejszego przepisywania połowy materiału.

## 5) Jak mówić to na sali (krótki komunikat)
> „W klasie używamy Reacta jako szybkiego nośnika demonstracyjnego, żeby skupić się na metodyce pracy z AI. W waszych projektach Angularowych przenosicie tę samą logikę decyzji i jakości — zmienia się framework, nie zasady inżynierskie. A pytanie o on-prem zadajemy wcześnie, bo ono ustawia bezpieczeństwo, audyt i realny tor wdrożenia.”

## Źródła do weryfikacji (stan na 2026-03-15)
- AI SDK docs (intro): https://ai-sdk.dev/docs/introduction
- Vercel AI SDK 5 announcement (Angular mention): https://vercel.com/blog/ai-sdk-5
- CopilotKit repo (pozycjonowanie React + Angular): https://github.com/CopilotKit/CopilotKit
- AG-UI protocol repo (kontekst interakcji agent-user): https://github.com/ag-ui-protocol/ag-ui

> Uwaga trenerska: przed finalnym delivery sprawdzić aktualny status funkcji Angular/enterprise w narzędziach (tempo zmian jest wysokie).