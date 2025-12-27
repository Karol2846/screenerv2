### Stock Analysis System – Technical Specification (DDD approach)
1. Executive Summary & Feasibility
   Analiza pliku yhfinance-swgger.json potwierdza, że realizacja strategii "Jakość -> Bezpieczeństwo -> Wycena" jest wykonalna w 95%.
   Kluczowy endpoint to Aggregator: GET /v11/finance/quoteSummary/{symbol}.
   Pozwala on pobrać większość wymaganych modułów w jednym zapytaniu, co jest krytyczne dla wydajności.
   Wymagane moduły w zapytaniu:
   ?modules=financialData,defaultKeyStatistics,balanceSheetHistory,incomeStatementHistory,cashflowStatementHistory,earningsTrend,summaryDetail,price

2. Architecture: Bounded Contexts
   System implementuje wzorzec Anti-Corruption Layer (ACL), aby oddzielić logikę domenową od struktury DTO z Yahoo Finance (np. obiektów { raw: 123, fmt: "123" }).
    - Infrastructure Layer (ACL)
   Odpowiada za komunikację z API i mapowanie JSON na czyste obiekty (DTO).
   Tabela Mapowania (Mapping Table):

   | Kryterium Biznesowe (Trela Rule) | Źródło w API (quoteSummary)     | Ścieżka JSON (Path)                            | Uwagi Implementacyjne                                    |
   |----------------------------------|---------------------------------|------------------------------------------------|----------------------------------------------------------|
   | Przychody (Revenue Growth)       | incomeStatementHistory          | history[].totalRevenue.raw                     | Pobierz listę, sortuj po dacie, licz CAGR/YoY.           |
   | Gotówka Operacyjna (CFO)         | cashflowStatementHistory        | history[].totalCashFromOperatingActivities.raw | Sprawdź trend rosnący.                                   |
   | Zysk Netto (Net Income)          | incomeStatementHistory          | history[].netIncome.raw                        | Pomocnicze, mniej ważne niż CFO.                         |
   | Prognozy (Estimates)             | earningsTrend                   | trend[].revenueEstimate.growth.raw             | Sprawdź 0y (obecny rok) i +1y.                           |
   | Dług Całkowity                   | financialData                   | totalDebt.raw                                  | -                                                        |
   | Aktywa (Assets)                  | balanceSheetHistory             | history[0].totalAssets.raw                     | Użyj najnowszego raportu rocznego.                       |
   | Płynność (Quick Ratio)           | financialData                   | quickRatio.raw                                 | Gotowy wskaźnik.                                         |
   | Pokrycie Odsetek (ICR)           | financialData / incomeStatement | ebit / interestExpense                         | Często trzeba wyliczyć ręcznie z incomeStatementHistory. |
   | Wycena P/S (TTM)                 | summaryDetail                   | priceToSalesTrailing12Months.raw               | Wersja podstawowa.                                       |
   | Target Analityków                | financialData                   | targetMeanPrice.raw                            | Do obliczenia Upside.                                    |
   | Cena Aktualna                    | price                           | regularMarketPrice.raw                         | Do obliczenia Upside.                                    |

   - Core Domain (Analysis Context)
   Serce systemu. Brak zależności do frameworków czy bibliotek HTTP. Czysta Java.
   
   1. Aggregate Root: AnalyzedCompany
      Główny obiekt reprezentujący wynik analizy.
       ```java
        public class AnalyzedCompany {
            private Ticker ticker;
            private QualityReport quality; // Wynik etapu 1
            private SafetyReport safety;   // Wynik etapu 2
            private ValuationReport valuation; // Wynik etapu 3
         
            // Metoda wymuszająca kolejność analizy (Fail-Fast)
            public AnalysisResult analyze() {
                if (!quality.isInvestable()) return AnalysisResult.REJECTED_POOR_QUALITY;
                if (!safety.isSafe()) return AnalysisResult.REJECTED_HIGH_RISK;
            
                return AnalysisResult.APPROVED(valuation.getScore());
             }
        }
       ```
   
   2. Value Objects (VO)
      Używamy typów silnych, by uniknąć "Primitive Obsession".
      Trend (Enum): RISING, FLAT, DECLINING. Wyliczany na podstawie serii danych historycznych (np. 4 lata Revenue).
      CoverageRatio: Obiekt opakowujący np. Interest Coverage. Ma metodę isHealthy() (np. > 3.0).
      UpsidePotential: Obiekt wyliczany jako (TargetPrice - CurrentPrice) / CurrentPrice.
      Money: BigDecimal z walutą jako dolar amerykański (USD). Nigdy double.

   3. Domain Policies (Reguły biznesowe)
      Wzorzec Policy lub Specification ułatwia modyfikację kryteriów bez ingerencji w logikę agregatu.
      RevenueGrowthPolicy (wymaga: Trend wzrostowy + CAGR > 5%)
      SolvencyPolicy (wymaga: Debt/Assets < 0.5 && QuickRatio > 1.0)
      FairValuePolicy (wymaga: P/S < Median P/S lub Upside > 15%)

   4. Implementation Plan for AI Agent (Prompt)
      Skopiuj poniższy prompt dla swojego Agenta AI, aby wygenerował szkielet kodu:
      "Act as a Senior Java Developer specializing in DDD. Create a domain model for a Stock Analysis application.
      Requirements:
      Create an Aggregate Root AnalyzedCompany.
      Implement Value Objects for FinancialRatio, Money (use BigDecimal), and GrowthTrend.
      Create a Domain Service StockScoringService that implements the following logic flow:
      Step 1 (Quality): Check if Revenue and Operating Cash Flow have a positive trend over the last 4 periods. If not, disqualify.
      Step 2 (Safety): Check if TotalDebt / TotalAssets < 0.35 AND QuickRatio > 1.0. If not, disqualify.
      Step 3 (Valuation): Calculate Upside based on Target Price. Calculate P/S Ratio.
      Define an interface StockDataProvider (Infrastructure Port) that returns a pure domain DTO CompanyFinancials, decoupling us from the specific Yahoo Finance JSON structure."

   5. Known Limitations & Workarounds (Edge Cases)
      Braki w API wymagające obejścia (Workarounds):
      Brak historycznych estymacji (Analyst Revisions):
      Problem: Endpoint earningsTrend pokazuje tylko obecny trend. Nie ma historii sprzed roku.
      Rozwiązanie: Własna baza danych zapisująca "snapshoty" tych danych raz w tygodniu, by budować historię rewizji.
      Brak gotowej Mediany P/S (Price-to-Sales):
      Problem: API zwraca tylko obecne P/S. Metoda Treli wymaga porównania do historycznej mediany.
      Rozwiązanie (Algorytm):
      Pobierz historię cen z /v8/finance/chart/{ticker}?range=5y&interval=1mo.
      Pobierz historię przychodów (roczną) z incomeStatementHistory.
      Zmapuj ceny na końce kwartałów/lat do ówczesnych przychodów.
      Wylicz historyczne P/S dla każdego punktu -> Wyciągnij medianę.
      Dane surowe (Raw Data):
      Uwaga: Zawsze pobieraj pola .raw z JSON-a. Pola .fmt są tylko do wyświetlania i ich parsowanie jest ryzykowne.
