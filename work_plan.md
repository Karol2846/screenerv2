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

    1. Bezpieczeństwo - odrzucenie + punkty (max 12):
       - quickRatio in [1.0, 2.5] punkty zależnie od wyniku (im niższy tym lepiej) +3             quickRatio < 0.8 -> REJECT
       - totalDebt/totalAssets < 0.5 = +3  
       - interestConverageRatio >= 3.0 - punkty zależnie od wyniku (im wyższy tym lepiej) +3
       - altmanZScore in [1.8, 3.0] punkty zależenie od wyniku (im wyższy tym lepiej) +3          altman-Z score < 1.8 -> REJECT

    2. Wzrost spółki (max 28):
       - revenueGrowthMean za ostatnie 3 lata <- jeśli rośnie spółka otrzymuje punkty w zależności od tempa wzrostu
           - [51, 100] = +20 punktów + Flaga (bardzo wysoki wzrost, możliwa `POSSIBLE_SPECULATION_BUBBLE`)
           - [10, 50] - punkty w zależności od wyniku (10 - 20)
           - [0, 10%] - punkty w zależności od wyniku (max 10)
           - < 0 - REJECT. ujemny wzrost to dyskfalifikacja
       - forwardRevenueGrowth2Y > 0 = +5
       - forwardEpsGrowth2Y > 10% = +10           (cena podąża za zyskami)
       - operating_cash_flow > net_income = +3    else FLAGA `CASH_FLOW_LESS_THAN_NET_INCOME`

    3. Wycena spółki - czy wchodzić teraz? (max 30)
       - zależność od wskaźnika P/S (price to sales) w stosunku do mediany z ostatnich 4 lat
         - psCurrent < medianPs4Y = +20 (jest dużo taniej niż zwykle)
         - psCurrent in [medianPs4Y * 0.9, medianPs4Y*1.1] = +10 (wycena w normie)
         - psCurrent in [medianPs4Y * 1.1, medianPs4Y*1.3] = +5 punktów (trochę drogo, ale można się zastanowić)
         - psCurrent > medianPs4Y * 1.3 = 0 punktów (za drogo, nie wchodzić)
         - psCurrent > medianPs4Y * 2.0 = 0 + FLAGA `POSSIBLE_SPECULATION_BUBBLE`
       - pegRatio:
         - pegRatio < 1.5 = +10
         - pegRatio in [1.5, 2.0] = +5
         - pegRatio > 2.0 = 0

    4. Ocena analityków (25 punktów)
       - upside potential ((targetPrice - currentPrice) / currentPrice) * 100%:
         - upside > 30 = +20
         - upside in [15, 30] - zależnie od wyniku (5 - 15)
         - upside in [0, 15] - zależnie od wyniku (0 - 5)
         - upside < 0% = 0 punktów + FLAGA (analitycy uważają, że spółka straci na wartości)
       - priceTargetTrend (3 miesiące - przechowuj dane historyczne dla 1 roku):
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


