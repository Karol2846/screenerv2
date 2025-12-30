## Plan dziaøana aplikacji do analizy spółek

Założenia:
 - znalizuję tylko rynek USA
 - nie znalizuje 'penny stocks', minimalny market cap = 3 mld USD
 - strategia treli nie jest dla każdej spółki, najlepiej sprawdza się dla spółek growth/tech
   - wykluczone sektory: Financials, Real Estate (REIT), Energy, Utilities, Basic Materials.
- dane mogą być pobierane z różnych API, w zależności od potrzeb - chcę mieć dokumentację w JSON-ie dla każdego z tych API


Przepływ danych i logika aplikacji:
1. Filtrowanie bazowe:
   1. if not US market -> odrzuć
   2. if sector in [Financials, Real Estate, Energy, Utilities, Basic Materials, Unknown] -> odrzuć
   3. if marketCap < 3 mld USD -> odrzuć

2. pobieranie danych (z bazy lub api) <- repository pattern
    - Dla danych fundamentalnych (zmieniających się raz na kwartał) sprawdź: `lastUpdateTime < 90 dni`
      - tak: użyj danych z bazy
      - nie: pobierz z api i zaktualizuj bazę
    - Dla danych zmieniających się często (np. ceny) sprawdź: `lastUpdateTime < 1 dzień`
      - tak: użyj danych z bazy
      - nie: pobierz z api i zaktualizuj bazę
    - DLa danych od analityków (price target, revenueGrowth, estimateEps itd), sprawdź: `lastUpdateTime < 30 dni`
      - tak: użyj danych z bazy
    - nie: pobierz z api i zaktualizuj bazę
   
   `Co zrobić, gdy dane nie są dostępne ani w bazie, ani w api?`
      - spróbuj pobrać dane z alternatywnego api (jeśli dostępne)?
      - oznaczyć spółke jako 'insufficient data' i odrzucić ją z analizy?

3. Analysis Pipeline (Scoring engine)
Spółki będą oceniane w skali od 0-100 punktów. <br>
w przypadku braku jakichkolwiek danych do obliczenia danego kryterium:
- spółka otrzymuje 0 punktów w tym kryterium
- maksymalna liczba punktów (w tym kryterium) zostaje zmniejszona + spółka ma dodaną odpowiednią anomalię do listy

    1. Bezpieczeństwo - odrzucenie + punkty (max 15):
       - quickRatio in [1.0, 2.5] punkty zależnie od wyniku (im niższy tym lepiej) +5             quickRatio < 0.8 -> REJECT
       - totalDebt/totalAssets < 0.5 = +5  
       - altman-Z score in [1.8, 3.0] punkty zależenie od wyniku (im wyższy tym lepiej)           altman-Z score < 1.8 -> REJECT

    2. Wzrost spółki (max 30):
       - revenue_growth_4_YoY za ostatnie 4 lata <- jeśli rośnie spółka otrzymuje punkty w zależności od tempa wzrostu
           - [21, 100] - punkty w zależności od wyniku (10 - 20) ????
           - [0, 20%] - punkty w zależności od wyniku (max 10)
           - < 0 - REJECT. ujemny wzrost to dyskfalifikacja
       - forward_revenue_growth_2_years > 0 = +7                     (brak ocen analityków -> 0 punktów + flaga)
       - dane z ostatniego kwartału:  operating_cash_flow > net_income -> wysoka jakość zysków = +7

    3. Wycena spółki - czy wchodzić teraz? (max 30)
       - zależność od wskaźnika P/S (price to sales) w stosunku do mediany z ostatnich 4 lat
         - ps_current < median_ps_4y = +20 (jest dużo taniej niż zwykle)
         - ps_current in [median_ps_4y * 0.9, median_ps_4y*1.1] = +10 (wycena w normie)
         - ps_current in [median_ps_4y * 1.1, median_ps_4y*1.3] = +5 punktów (trochę drogo, ale można się zastanowić)
         - ps_current > median_ps_4y * 1.3 = 0 punktów (za drogo, nie wchodzić)
       - peg_ratio:
         - peg_ratio < 1.5 = +10
         - peg_ratio in [1.5, 2.0] = +5
         - peg_ratio > 2.0 = 0

    4. Ocena analityków (25 punktów)
       - upside_potential (target_price vs current_price):
         - upside > 30% = +20
         - upside in [15%, 30%] - zależnie od wyniku (5 - 15)
         - upside in [0%, 15%] - zależnie od wyniku (0 - 5)
         - upside < 0% = 0 punktów + FLAGA (analitycy uważają, że spółka straci na wartości)
       - price_target_trend (3 miesiące - przechowuj dane historyczne dla 1 roku):
         - rosnący = +5
         - płaski = +2
         - malejący = 0 + FLAGA (ta sama co dla upside)



Na koniec analizy wychodzi opbiekt z wynikiem punktowym i listą anomalii (jeśli wystąpiły):
```java
public class AnalysisReport {
    private String ticker;
    private int totalScore;                             // 0-100 (lub mniej)
    private int maxPossibleScore;                      // maksymalna możliwa do uzyskania liczba punktów (jeśli były braki danych, to mniej niż 100)
    private ScoreBreakdown breakdown;                   // Punkty za każdą sekcję
    private List<Anomaly> anomalies;                    // Lista flag np. MISSING_FORECASTS, SHORT_HISTORY
    private String rejestReason;                        // jeśli odrzucono, to powód odrzucenia
    private AnalystRecomendation analystRecommendation; // ocena analityków (tak doatkowo, bo warto)
}
```

TODO: dodaj shcemat bazy danych dla encji zapisywanych raz na kwartał/miesiąc i ew. dzień



