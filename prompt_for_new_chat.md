Stack: Quarkus + Hibernate Panache (Active Record) + Lombok + MicroProfile Rest Client + Gradle Architektura: Clean Architecture + DDD. Moduły: collector (pobieranie danych) i analyzer (analiza). Pakiety: adapter (web, file), application (service, mapper, port), domain (entity, valueobject).

✅ Co zostało zaimplementowane (aktualny stan)
Warstwa aplikacji (collector)
StockDataCollectorService — orkiestrator z @Transactional. Pobiera dane z AlphaVantage i YahooFinance, mapuje je przez StockDataMapper, zapisuje encje Stock, MonthlyReport, QuarterlyReport.
StockDataMapper — klasa mapująca. Dwie metody publiczne:
toMarketDataSnapshot(RawOverview, YhFinanceResponse) → MarketDataSnapshot
toFinancialDataSnapshot(RawBalanceSheet, RawIncomeStatement, RawCashFlow) → FinancialDataSnapshot
Warstwa adapterów (collector.adapter.out.web.alphavantage)
AlphaVantageGateway implementuje AlphaVantageClient. Posiada metody: fetchOverview, fetchBalanceSheet, fetchIncomeStatement, fetchCashFlow.
AlphaVantageApiClient — MicroProfile REST Client interface. Odpowiednie REST metody dla Overview, BalanceSheet, IncomeStatement, CashFlow.
AlphaVantageResponseMapper — mapuje odpowiedzi HTTP (*Response) na porty wyjściowe (Raw*).
Modele odpowiedzi (model/): OverviewResponse, BalanceSheetResponse, IncomeStatementResponse, CashFlowResponse, CashFlowReport (oddzielny plik!).
Porty wyjściowe (collector.application.port.out.alphavantage)
RawOverview, RawBalanceSheet, RawIncomeStatement, RawCashFlow — rekordy z @Builder od Lomboka.
Domena (domain)
Stock — encja z polem marketData: MarketData.
MonthlyReport — encja z updateMetrics(MarketDataSnapshot).
QuarterlyReport — encja ze wszystkimi polami bilansowymi/przepływowymi. Metoda updateMetrics(FinancialDataSnapshot, String sector) oblicza QuickRatio i AltmanZScore.
FinancialDataSnapshot — @Builder record z polami: totalCurrentAssets, totalCurrentLiabilities, totalAssets, totalLiabilities, retainedEarnings, ebit, interestExpense, totalShareholderEquity, inventory, totalRevenue, totalDebt, netIncome, operatingCashFlow.
🔴 Zidentyfikowane błędy — do natychmiastowej poprawy
1. currentPrice = movingAverage50Day — krytyczny błąd semantyczny
Plik: StockDataMapper.java, linia ~25

java
.currentPrice(overview != null ? overview.movingAverage50Day() : null)
Problem: Używamy 50-dniowej średniej ruchomej jako "ceny", bo AlphaVantage nie zwraca ceny bieżącej w OVERVIEW. Prawidłowo należy użyć osobnego endpointu GLOBAL_QUOTE. Wszystkie obliczenia bazujące na aktualnej cenie (np. P/S ratio w MonthlyReport) zwracają fałszywe dane. Wymaganie z CSV (linia 5): MarketCap alternatywne przez GLOBAL_QUOTE lub domyślnie OVERVIEW — natomiast cena bieżąca MUSI wywodzić się z GLOBAL_QUOTE.price.

2. totalDebt — prawdopodobne podwójne liczenie longTermDebt
Plik: StockDataMapper.java, linia ~56–63

java
BigDecimal shortLongTermDebtTotal = ... // To jest JUŻ suma krótko + długoterm.
BigDecimal longTermDebt = ...
BigDecimal totalDebt = shortLongTermDebtTotal.add(longTermDebt); // ❌ longTermDebt dwa razy!
Problem: W API Alpha Vantage pole shortLongTermDebtTotal jest prekalkulowaną sumą całkowitego zadłużenia. Dodawanie do niej jeszcze raz longTermDebt powoduje podwójne zaliczenie długu długoterminowego. Wymaganie z CSV (linia 15): shortLongTermDebtTotal + longTermDebt — weryfikacja dokumentacji API jest konieczna, bo CSV może być błędny.

3. Brak endpointu GLOBAL_QUOTE — AlphaVantageClient jest niekompletny
Plik: AlphaVantageClient.java (interfejs) Brak metody fetchGlobalQuote(String ticker) → RawGlobalQuote. Bez tego nie ma jak pobrać aktualnej ceny.

4. Brak PEGRatio w MarketDataSnapshot
Plik: StockDataMapper.java, toMarketDataSnapshot Wymaganie z CSV (linia 8): PEGRatio (Forward PEG) z OVERVIEW.PEGRatio musi być pobierany i przekazywany dalej do analiz.

5. revenueTTM — uproszczenie niezgodne z definicją TTM
Plik: StockDataMapper.java, linia ~32 Problem: Pobieramy overview.revenueTTM() — to gotowe pole z OVERVIEW. Tymczasem CSV (linia 10) definiuje TTM jako: "Suma totalRevenue z kwartalnych raportów [0..3]" z INCOME_STATEMENT. Obecne rozwiązanie może być wystarczająco dobre, ale jest niezgodne z dokumentem wymagań.

6. toFinancialDataSnapshot łamie SRP — zbyt wiele odpowiedzialności
Plik: StockDataMapper.java, linia 50–109 Metoda jednocześnie: ustawia wartości domyślne, oblicza totalDebt, wywodzi retainedEarnings, estymuje ebit. Należy rozbić na prywatne metody pomocnicze: calculateTotalDebt(...), resolveRetainedEarnings(...), resolveEbit(...).

7. Potencjalnie nieczytelny null-guard przy retainedEarnings
Plik: StockDataMapper.java, linia 78

java
if (retainedEarnings == null && totalShareholderEquity != null && latestBalance.commonStock() != null)
IDE zgłasza warning — latestBalance może być null. Logika jest faktycznie bezpieczna (jeśli totalShareholderEquity != null, to latestBalance nie był null), ale jest nieczytelna. Należy wyodrębnić to do metody.

8. 3 przeciążone metody getLatestReportOrNull — naruszenie DRY
Trzy prawie identyczne metody prywatne dla RawBalanceSheet, RawIncomeStatement, RawCashFlow. Brakuje wspólnego interfejsu lub generycznego rozwiązania.

📋 Plik wymagań
Wszelkie wymagania dotyczące mapowania pól finansowych opisane są w pliku:

c:\Users\karol\IdeaProjects\screenerv2\alpha_vantage_plan.csv
🧪 Testy
Projekt buduje się i przechodzi testy: ./gradlew test (BUILD SUCCESSFUL).