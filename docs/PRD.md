# PRD: Loan Decision Copilot
## MVP szkoleniowe

## 1. Cel dokumentu

Niniejszy dokument definiuje wymagania produktowe dla MVP szkoleniowego systemu **Loan Decision Copilot**: chatowego asystenta wspierającego klienta w procesie uzyskania rekomendacji kredytowej w środowisku bankowym.

Dokument obejmuje:
- zakres funkcjonalny MVP,
- zachowanie systemu,
- wymagania UX/UI,
- scenariusze użytkownika,
- mierzalne kryteria akceptacji,
- ograniczenia niefunkcjonalne,
- zakres wyłączony z MVP,
- założenia i ryzyka dotyczące danych demonstracyjnych.

Dokument nie obejmuje:
- architektury rozwiązania,
- stosu technologicznego,
- decyzji implementacyjnych,
- projektu integracji technicznej,
- doboru modeli AI.

---

## 2. Problem Statement

### 2.1. Problem biznesowy

W procesie samoobsługowego wnioskowania o kredyt klient często nie wie:
- jak rozpocząć proces,
- jakie dane należy podać,
- dlaczego otrzymał określony wynik,
- czy system pracuje na właściwych danych klienta.

W środowisku szkoleniowym bank potrzebuje prostego, deterministycznego procesu, który:
- zaczyna się od rozmowy naturalnym językiem,
- automatycznie przechodzi do ustrukturyzowanego formularza,
- wykorzystuje dane klienta zapisane w bazie,
- wylicza wynik według jawnej reguły biznesowej,
- pozostawia pełny ślad audytowy.

### 2.2. Problem operacyjny

Bez ustrukturyzowanego chatowego przepływu:
- klient musi sam odnaleźć odpowiednią ścieżkę,
- dane są wpisywane ręcznie mimo dostępności w bazie,
- uzasadnienie wyniku może być niespójne,
- trudniej odtworzyć przebieg procesu,
- trudno ocenić poprawność działania systemu w warunkach szkoleniowych.

### 2.3. Dla kogo system rozwiązuje problem

System jest przeznaczony przede wszystkim dla:
- **klienta**, który od początku rozmawia bezpośrednio z agentem AI i samodzielnie inicjuje proces kredytowy.

Dodatkowym odbiorcą pośrednim jest:
- **audytor / trener**, który ocenia poprawność procesu na podstawie zapisanych zdarzeń.

### 2.4. Kontekst użycia

System działa jako osadzony interfejs chatowy w środowisku bankowym. Klient rozpoczyna rozmowę z agentem AI. Po wykryciu intencji związanej z uzyskaniem kredytu agent uruchamia dynamiczny formularz w obrębie chatu.

W MVP:
- nie ma udziału konsultanta,
- nie ma logowania,
- nie ma klasycznego uwierzytelnienia,
- identyfikacja klienta odbywa się wyłącznie przez `PESEL` albo `NIP/VAT ID`.

Po podaniu identyfikatora system:
- wyszukuje klienta w bazie,
- automatycznie uzupełnia podstawowe dane z tabeli `client`,
- pobiera historię finansową z tabeli `client_products`,
- oblicza wynik według uproszczonej reguły,
- zwraca rekomendację z uzasadnieniem,
- zapisuje decyzję i pełny audit trail.

### 2.5. Cel MVP

Celem MVP jest dostarczenie procesu, który:
- wykrywa intencję kredytową w rozmowie,
- uruchamia dynamiczny formularz w chatcie,
- identyfikuje klienta po `PESEL` albo `NIP/VAT ID`,
- automatycznie uzupełnia podstawowe dane klienta,
- oblicza wynik na podstawie historii spłaty wcześniejszych kredytów,
- przedstawia rekomendację z uzasadnieniem,
- zapisuje wszystkie działania i decyzje w ścieżce audytowej.

---

## 3. Użytkownicy / Persony

## 3.1. Persona A: Klient samoobsługowy

**Opis**  
Klient banku korzystający z chatowego interfejsu osadzonego w środowisku bankowym. Od początku komunikuje się wyłącznie z agentem AI.

**Cele**
- szybko rozpocząć proces kredytowy bez szukania odpowiedniego formularza,
- ograniczyć ręczne wpisywanie danych,
- uzyskać jednoznaczną rekomendację,
- zrozumieć, dlaczego wynik jest pozytywny lub negatywny.

**Kontekst użycia**
- kanał samoobsługowy,
- brak wsparcia konsultanta,
- użytkownik może nie znać procesu kredytowego,
- użytkownik oczekuje prowadzenia krok po kroku.

**Problemy / pain points**
- niepewność, jak rozpocząć wniosek,
- obawa przed rozbudowanym formularzem,
- brak transparentności decyzji,
- frustracja związana z wpisywaniem danych, które bank już posiada,
- ryzyko pomyłki przy podaniu identyfikatora.

## 3.2. Persona B: Audytor / trener

**Opis**  
Osoba oceniająca poprawność działania MVP szkoleniowego na podstawie przebiegu sesji i zapisanych zdarzeń.

**Cele**
- odtworzyć pełen przebieg procesu,
- sprawdzić, jakie dane zostały użyte do decyzji,
- potwierdzić zgodność działania z regułą biznesową MVP,
- ocenić poprawność obsługi przypadków pozytywnych, negatywnych i błędnych.

**Kontekst użycia**
- analiza po zakończonej sesji,
- środowisko szkoleniowe,
- nacisk na jednoznaczność i powtarzalność wyników.

**Problemy / pain points**
- brak pełnej historii zdarzeń,
- brak powiązania decyzji z danymi wejściowymi,
- trudność w rozróżnieniu działań klienta i działań systemu,
- niejednoznaczne przypadki graniczne.

---

## 4. Main Flow

## 4.1. Założenia ogólne przepływu

- Klient rozpoczyna interakcję w chatcie z agentem AI.
- Agent AI wykrywa intencję złożenia wniosku kredytowego lub sprawdzenia możliwości kredytowej.
- Po wykryciu intencji agent uruchamia formularz dynamiczny w obrębie rozmowy.
- Formularz rozpoczyna się od identyfikacji klienta.
- W MVP dozwolone są dwa identyfikatory:
  - `PESEL`,
  - `NIP/VAT ID`.
- System wyszukuje klienta na podstawie wybranego identyfikatora.
- Po odnalezieniu klienta system uzupełnia dane z tabeli `client`: imię, nazwisko, adres.
- System pobiera historię finansową klienta z tabeli `client_products`.
- Reguła decyzyjna MVP:
  - jeśli klient nie spłacił poprawnie co najmniej 10% wcześniej zaciągniętych kredytów, wynik jest negatywny,
  - jeśli udział niespłaconych kredytów jest poniżej 10%, wynik jest pozytywny,
  - jeśli klient nie ma historii wcześniejszych kredytów, system zwraca status „brak możliwości oceny”.
- System zwraca rekomendację wraz z uzasadnieniem.
- Każda akcja klienta i systemu jest rejestrowana w audycie.

## 4.2. Główny przepływ użytkownika

1. Klient rozpoczyna rozmowę z agentem AI.
2. Klient wpisuje komunikat wyrażający potrzebę uzyskania kredytu, sprawdzenia oferty kredytowej albo zdolności kredytowej.
3. Agent AI wykrywa intencję kredytową.
4. Agent informuje klienta, że rozpoczyna proces oceny i wyświetla dynamiczny formularz w obrębie chatu.
5. System prosi o wybór typu identyfikatora:
   - `PESEL`,
   - `NIP/VAT ID`.
6. Klient wybiera typ identyfikatora.
7. System wyświetla pole do wpisania wartości wybranego identyfikatora.
8. Klient wpisuje `PESEL` albo `NIP/VAT ID`.
9. System wykonuje walidację formatu zgodną z typem identyfikatora.
10. Jeśli walidacja nie powiedzie się, system wyświetla komunikat błędu i żąda poprawy danych.
11. Jeśli walidacja powiedzie się, system wyszukuje klienta w bazie danych.
12. Jeśli klient zostanie odnaleziony jednoznacznie, system automatycznie uzupełnia pola: imię, nazwisko, adres.
13. Agent prezentuje odnalezione dane klientowi w chatcie lub w formularzu osadzonym.
14. System pobiera historię produktów klienta z tabeli `client_products`.
15. System identyfikuje rekordy odpowiadające wcześniejszym kredytom.
16. System oblicza udział kredytów niespłaconych.
17. System wyznacza wynik:
   - rekomendacja negatywna dla udziału `>= 10%`,
   - rekomendacja pozytywna dla udziału `< 10%`.
18. Agent zwraca klientowi wynik oraz uzasadnienie.
19. System zapisuje wynik, dane użyte do obliczenia i wszystkie zdarzenia procesu.
20. System potwierdza zakończenie procesu.

## 4.3. Zachowanie po wykryciu intencji

Po wykryciu intencji agent AI:
- nie powinien kontynuować wyłącznie swobodnej rozmowy,
- powinien przejść do trybu prowadzonego procesu,
- powinien jasno zakomunikować, że do dalszej obsługi potrzebna jest identyfikacja klienta,
- powinien prowadzić użytkownika krok po kroku.

## 4.4. Obsługa przypadków wyjątkowych

### Niepoprawny format identyfikatora
- System wskazuje, że format wpisanego identyfikatora jest niepoprawny.
- System nie pobiera danych klienta.
- System nie przechodzi do etapu decyzji.
- Zdarzenie walidacyjne jest zapisywane w audycie.

### Brak klienta w bazie
- System informuje, że nie odnaleziono klienta dla podanego identyfikatora.
- System umożliwia ponowne podanie identyfikatora, maksymalnie 5 razy w ramach jednej sesji procesu.
- System nie oblicza wyniku.
- Zdarzenie „brak dopasowania” jest zapisywane w audycie.

### Niejednoznaczne dopasowanie
- Jeśli podany identyfikator zwraca więcej niż jeden rekord, system nie może kontynuować procesu.
- System wyświetla komunikat o braku jednoznacznej identyfikacji.
- System zapisuje zdarzenie błędu dopasowania w audycie.

### Brak historii kredytowej
- Dla klienta bez wcześniejszych kredytów system zwraca status „brak możliwości oceny”.
- System nie powinien w tym przypadku zwracać rekomendacji pozytywnej ani negatywnej.
- Komunikat i dane demo muszą jednoznacznie odzwierciedlać ten przypadek.

### Błąd dostępu do danych
- System informuje o czasowej niedostępności danych.
- System nie pokazuje częściowej ani niezweryfikowanej rekomendacji.
- Zdarzenie błędu musi zostać zapisane w audycie.

### Przerwanie procesu
- Jeśli klient przerwie proces przed uzyskaniem wyniku, system zapisuje stan procesu jako nieukończony.
- Audit trail musi zawierać ostatni poprawnie zakończony krok.

---

## 5. User Stories

1. **Jako klient chcę rozpocząć proces w języku naturalnym, aby agent AI sam rozpoznał, że chcę ubiegać się o kredyt.**

2. **Jako klient chcę zostać automatycznie przeprowadzony z rozmowy do formularza dynamicznego, aby nie wyszukiwać ręcznie odpowiedniej ścieżki.**

3. **Jako klient chcę zidentyfikować się przy użyciu PESEL albo NIP/VAT ID, aby system mógł odnaleźć moje dane bez logowania.**

4. **Jako klient chcę otrzymać informację o błędnym identyfikatorze, aby poprawić dane i nie kontynuować procesu na błędnym rekordzie.**

5. **Jako klient chcę zobaczyć automatycznie uzupełnione imię, nazwisko i adres, aby potwierdzić, że system odnalazł właściwy profil.**

6. **Jako klient chcę otrzymać rekomendację kredytową albo status „brak możliwości oceny” wraz z uzasadnieniem, aby rozumieć wynik procesu.**

7. **Jako klient chcę, aby wynik był liczony według jednej jawnej reguły MVP, aby rezultat był przewidywalny i spójny.**

8. **Jako klient chcę otrzymać komunikat o problemie z dostępem do danych, aby wiedzieć, że proces nie został zakończony prawidłowo.**

9. **Jako audytor chcę mieć pełną historię działań klienta i systemu, aby móc odtworzyć przebieg sesji i źródło decyzji.**

10. **Jako trener procesu chcę, aby każda rekomendacja była zapisana razem z danymi wejściowymi i uzasadnieniem, aby można było ocenić poprawność działania MVP.**

---

## 6. Mierzalne kryteria akceptacji

## 6.1. Wykrycie intencji i uruchomienie procesu

- System musi rozpoznać intencję kredytową na podstawie komunikatu użytkownika w chatcie.
- Po rozpoznaniu intencji system musi przejść do formularza dynamicznego w tej samej sesji.
- Przejście do formularza nie może wymagać przejścia do oddzielnego kanału lub osobnego modułu poza chatem.

## 6.2. Identyfikacja klienta

- Pierwszym krokiem formularza musi być wybór typu identyfikatora: `PESEL` albo `NIP/VAT ID`.
- System musi wymagać podania wartości wybranego identyfikatora przed przejściem dalej.
- System musi stosować walidację formatową odpowiednią do wybranego typu identyfikatora.
- Dla niepoprawnego identyfikatora system nie może pobierać danych klienta ani obliczać wyniku.
- Dla poprawnego identyfikatora system musi wykonać próbę wyszukania klienta.

## 6.3. Wyszukanie klienta i auto-uzupełnienie danych

- Jeśli klient zostanie odnaleziony jednoznacznie, system musi automatycznie uzupełnić co najmniej:
  - imię,
  - nazwisko,
  - adres.
- Uzupełnione dane muszą zostać pokazane użytkownikowi przed prezentacją wyniku.
- Uzupełnione dane muszą być tylko do odczytu w MVP.
- Jeśli klient nie zostanie odnaleziony, system nie może przejść do etapu oceny kredytowej.
- Jeśli identyfikator prowadzi do więcej niż jednego dopasowania, system nie może kontynuować procesu.

## 6.4. Obliczenie rekomendacji

- System musi pobrać dane historyczne z tabeli `client_products`.
- System musi zidentyfikować wcześniejsze kredyty klienta.
- Za kredyt niespłacony system musi uznawać rekord, dla którego pole opisujące spłatę kredytu ma wartość `nie`.
- System musi obliczyć udział kredytów niespłaconych.
- Jeśli udział kredytów niespłaconych jest `>= 10%`, system musi zwrócić rekomendację negatywną.
- Jeśli udział kredytów niespłaconych jest `< 10%`, system musi zwrócić rekomendację pozytywną.
- Jeśli klient nie posiada żadnych wcześniejszych kredytów, system musi zwrócić status „brak możliwości oceny”.
- System nie może sugerować, że użyto innych kryteriów niż historia spłaty wcześniejszych kredytów.

## 6.5. Prezentacja wyniku

- System musi wyświetlić wynik w jasnej, jednoznacznej formie.
- Uzasadnienie musi wskazywać, że rekomendacja została oparta na historii spłaty wcześniejszych kredytów.
- Uzasadnienie musi zawierać liczby: liczbę wcześniejszych kredytów, liczbę kredytów niespłaconych oraz wyliczony procent.
- Uzasadnienie musi wskazywać, czy próg 10% został osiągnięty lub przekroczony.
- Komunikat końcowy musi wskazywać, że wynik ma charakter szkoleniowy i dotyczy MVP.

## 6.6. Audit trail

- System musi rejestrować co najmniej:
  - rozpoczęcie sesji,
  - wykrycie intencji,
  - uruchomienie formularza,
  - wybór typu identyfikatora,
  - wpisanie identyfikatora,
  - wynik walidacji identyfikatora,
  - wynik wyszukania klienta,
  - auto-uzupełnienie danych,
  - pobranie historii produktów,
  - obliczenie wyniku,
  - prezentację rekomendacji,
  - zapis wyniku końcowego,
  - błędy,
  - przerwanie procesu.
- Każde zdarzenie audytowe musi zawierać:
  - znacznik czasu,
  - identyfikator sesji,
  - typ zdarzenia,
  - aktora,
  - status operacji.
- Audit trail musi umożliwiać odtworzenie całej sekwencji zdarzeń w porządku chronologicznym.
- Audit trail musi wskazywać, że sesja była niezalogowana i że identyfikacja została wykonana na podstawie identyfikatora podanego przez użytkownika.

## 6.7. Obsługa błędów

- Dla błędnego formatu identyfikatora system musi wyświetlić komunikat naprawczy.
- Dla braku dopasowania klienta system musi zatrzymać proces przed wyliczeniem wyniku.
- Po 5 nieudanych próbach identyfikacji w ramach jednej sesji system musi zakończyć proces i zapisać ten fakt w audycie.
- Dla błędu dostępu do danych system musi zatrzymać proces i zapisać błąd w audycie.
- System nie może wygenerować rekomendacji, jeśli dane wejściowe są niepełne, niespójne albo niezweryfikowane.

---

## 7. Ograniczenia niefunkcjonalne

## 7.1. Wydajność

- Interakcja z formularzem powinna być płynna z perspektywy użytkownika.
- Po poprawnym podaniu identyfikatora prezentacja danych klienta powinna następować bez zauważalnej zwłoki operacyjnej.
- Prezentacja wyniku powinna mieścić się w czasie akceptowalnym dla interakcji konwersacyjnej.
- W przypadku opóźnienia system musi pokazywać stan przetwarzania.

## 7.2. Dostępność

- System powinien być dostępny w czasie sesji szkoleniowych.
- Błąd pojedynczego kroku nie może powodować utraty wcześniej zapisanych zdarzeń audytowych.
- Po błędzie system powinien umożliwiać ponowną próbę lub rozpoczęcie procesu od nowa.

## 7.3. Bezpieczeństwo i prywatność

- Zakres prezentowanych danych klienta musi być ograniczony do minimum wymaganego przez MVP.
- System nie może ujawniać danych klienta przed poprawnym dopasowaniem rekordu.
- Po poprawnym dopasowaniu system pokazuje pełny adres klienta.
- W związku z brakiem logowania i uwierzytelnienia MVP musi być traktowane jako środowisko podwyższonego ryzyka w zakresie ekspozycji danych.
- Interfejs i komunikaty nie mogą sugerować wyższego poziomu weryfikacji tożsamości niż faktycznie zastosowany.

## 7.4. Audytowalność

- Każda istotna akcja klienta i systemu musi pozostawiać ślad operacyjny.
- Audit trail musi rozróżniać zdarzenia inicjowane przez klienta od zdarzeń automatycznych.
- Wynik końcowy musi być powiązany z konkretnymi danymi wejściowymi użytymi do obliczenia.

## 7.5. Determinizm szkoleniowy

- Dla tych samych danych wejściowych system musi zwracać ten sam wynik.
- Dane demonstracyjne muszą pozwalać na powtarzalne odtworzenie scenariuszy.
- Komunikaty końcowe i uzasadnienie muszą być spójne z regułą MVP.

## 7.6. Użyteczność

- Interfejs ma prowadzić klienta krok po kroku bez potrzeby znajomości procedury bankowej.
- Formularz ma być osadzony w rozmowie.
- Błędy muszą wskazywać, co jest niepoprawne i co należy zrobić dalej.
- System nie powinien wymagać od klienta wiedzy o strukturze danych bankowych ani o procesie scoringowym.

---

## 8. Out of Scope

Z zakresu MVP wyłączone są:

- udział konsultanta lub doradcy bankowego,
- logowanie użytkownika,
- uwierzytelnienie wieloskładnikowe,
- dodatkowa weryfikacja tożsamości poza `PESEL` albo `NIP/VAT ID`,
- pełny proces kredytowy kończący się podpisaniem umowy,
- integracje z zewnętrznymi biurami informacji kredytowej,
- zaawansowany scoring wieloczynnikowy,
- ręczna zmiana danych auto-uzupełnionych, chyba że zostanie osobno zatwierdzona,
- upload dokumentów,
- podpis elektroniczny,
- workflow odwołań i ponownej oceny,
- obsługa konsultanta działającego w imieniu klienta,
- rekomendacje sprzedażowe i cross-sell,
- analityka menedżerska,
- projekt architektury i wybór technologii.

---

## 9. Ryzyka i założenia dla danych demonstracyjnych

## 9.1. Założenia dotyczące danych demo

W środowisku szkoleniowym zakłada się istnienie:
- tabeli `client`, zawierającej co najmniej:
  - `PESEL` lub `NIP/VAT ID`,
  - imię,
  - nazwisko,
  - adres,
- tabeli `client_products`, zawierającej historię produktów finansowych klienta.

Dane demo powinny obejmować co najmniej następujące przypadki:
- klient odnajdywany po `PESEL`,
- klient odnajdywany po `NIP/VAT ID` jako alternatywnym identyfikatorze tego samego klienta,
- klient bez historii kredytowej,
- klient z historią wyłącznie pozytywną,
- klient z udziałem niespłaconych kredytów poniżej 10%,
- klient z udziałem niespłaconych kredytów równym 10%,
- klient z udziałem niespłaconych kredytów powyżej 10%,
- identyfikator nieistniejący w bazie,
- dane powodujące brak jednoznacznego dopasowania,
- dane niespójne lub niepełne dla testów błędów.

## 9.2. Ryzyka związane z danymi demo

- Brak logowania oznacza, że samo podanie poprawnego identyfikatora może prowadzić do wyświetlenia danych klienta.
- Użycie `PESEL` lub `NIP/VAT ID` jako jedynego mechanizmu dopasowania zwiększa ryzyko nieuprawnionego dostępu do danych w warunkach zbliżonych do rzeczywistych.
- Dane demo zbyt uproszczone obniżą wartość szkoleniową rozwiązania.
- Pole opisujące spłatę kredytu musi być jednoznaczne i pozwalać odróżnić wartości `tak` oraz `nie`.
- Brak przypadków granicznych utrudni ocenę poprawności implementacji.
- Dane zbyt podobne do rzeczywistych mogą generować ryzyko naruszenia zasad ochrony danych.

## 9.3. Założenia operacyjne dla danych demo

- Dane demonstracyjne nie są danymi produkcyjnymi ani danymi rzeczywistych klientów.
- Każdy rekord testowy powinien mieć przypisany oczekiwany wynik.
- Dane powinny umożliwiać wielokrotne odtwarzanie tych samych scenariuszy.
- Dane powinny być stabilne przez cały okres szkolenia.
- Każdy przypadek testowy powinien umożliwiać sprawdzenie zarówno wyniku końcowego, jak i kompletności śladu audytowego.

---

## 10. Dodatkowe doprecyzowania produktowe

## 10.1. Charakter wyniku
W MVP system zwraca **wynik szkoleniowy / rekomendację MVP** opartą na zdefiniowanej regule szkoleniowej. System nie powinien komunikować wyniku jako pełnej, finalnej decyzji kredytowej, chyba że zostanie to formalnie zmienione.

## 10.2. Minimalny zakres UI/UX
Interfejs powinien zawierać:
- okno rozmowy z agentem AI,
- dynamiczny formularz osadzony w rozmowie,
- wybór typu identyfikatora,
- pole na wartość identyfikatora,
- sekcję prezentacji danych klienta,
- sekcję wyniku i uzasadnienia,
- komunikaty błędów,
- potwierdzenie zapisania procesu,
- wskaźnik stanu przetwarzania.

## 10.3. Zasady komunikatów
Komunikaty powinny być:
- krótkie,
- jednoznaczne,
- proceduralne,
- pozbawione języka marketingowego,
- zgodne z faktycznym zakresem weryfikacji klienta,
- zrozumiałe dla użytkownika bez wiedzy bankowej,
- spójne z tym, że wynik ma charakter szkoleniowy / MVP.

## 10.4. Minimalna zawartość uzasadnienia
Uzasadnienie rekomendacji powinno zawierać:
- odniesienie do historii wcześniejszych kredytów,
- liczbę wcześniejszych kredytów,
- liczbę kredytów niespłaconych,
- wyliczony procent kredytów niespłaconych,
- informację, czy próg 10% został osiągnięty lub przekroczony,
- wskazanie, że wynik opiera się na danych historycznych zapisanych w systemie.

---

## 11. Otwarte kwestie do doprecyzowania

1. Należy doprecyzować docelowy mechanizm utrzymywania pola określającego, czy kredyt został spłacony.
2. Należy formalnie zdefiniować, które rekordy w `client_products` liczą się jako kredyty.
3. Należy potwierdzić szczegółowy format i reguły walidacji dla `NIP/VAT ID`.
4. Należy doprecyzować, czy po wykorzystaniu 5 prób klient może rozpocząć nową sesję natychmiast.
5. Należy określić dokładną postać komunikatu końcowego dla statusu „brak możliwości oceny”.
6. Należy doprecyzować szczegółowy katalog zdarzeń procesowych przechowywanych w audycie.

## Pytania doprecyzowujące

1. **Jak dokładnie ma wyglądać komunikat dla statusu „brak możliwości oceny”?**  
To wpływa na UX, testy akceptacyjne i spójność komunikatów końcowych.

2. **Jakie dokładnie wartości i nazwa pola w `client_products` będą oznaczać stan „spłacony / niespłacony”?**  
To jest potrzebne do jednoznacznej implementacji reguły scoringowej.

3. **Jaki format `NIP/VAT ID` ma być uznawany za poprawny w MVP?**  
To wpływa na walidację danych wejściowych i scenariusze błędów.

4. **Czy po wykorzystaniu 5 prób identyfikacji klient może od razu zacząć nową sesję i spróbować ponownie?**  
To zmienia model stanów procesu i zasady blokowania dalszych prób.

5. **Czy pełny adres ma być pokazywany zawsze po dopasowaniu klienta, czy tylko w określonym układzie maskowania lub formatowania?**  
To wpływa na projekt UI i poziom ekspozycji danych.

6. **Czy wynik liczbowy w uzasadnieniu ma być zaokrąglany, a jeśli tak, to do ilu miejsc po przecinku?**  
To jest ważne dla spójności prezentacji i kryteriów testowych.

7. **Czy „jedna rekomendacja” oznacza jeden wspólny typ produktu kredytowego bez wyboru produktu w formularzu?**  
To wpływa na zakres formularza i komunikatów procesowych.

8. **Czy uporządkowane zdarzenia procesowe w audycie mają obejmować również treść komunikatów błędów zwracanych klientowi?**  
To wpływa na poziom szczegółowości audytu i możliwość odtworzenia doświadczenia użytkownika.

9. **Czy klient ma widzieć licznik pozostałych prób identyfikacji, czy limit 5 prób ma działać wyłącznie po stronie systemu?**  
To wpływa na UX i zachowanie użytkownika przy błędach.

10. **Czy komunikat końcowy dla wyniku pozytywnego i negatywnego ma zawsze zawierać sformułowanie „wynik szkoleniowy / MVP”?**  
To wpływa na spójność wszystkich komunikatów końcowych i zgodność z celem środowiska.
