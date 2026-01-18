ticker
sector
marketCap
quickRatio
totalDebt
totalAssets
interestConverageRatio
altmanZScore
revenue                     - do liczenia historii wzrostu
forwardRevenueGrowth        - yhFinance - earningsTrend.revenueEstimate
forwardEpsGrowth            - yhFinance - earningsTrend.earningsEstimate
operativeCashFlow           (operatingCashFlow)
netIncome

//SPRRAWDZONE DOTĄD 0 z excelem B)
psCurrent                   - potrzebne aktualne + będzie liczona mediana z 4 lat
forwardPEG
forwardPE
targetPrice                 - trzeba przechowywać dane 1/m
currentPrice                - do oblczenia upside
analystRatings              - strongbuy, buy, hold, sell, strong sell, counts

**obliczanie current PS**
    PS = marketCap / revenue (TTM)


**Obliczanie Altman Z''-Score (Dla firm nieprodukcyjnych/Tech)**
        Z'' = 6.56*T1 + 3.26*T2 + 6.72*T3 + 1.05*T4

opis jest w pliku alpha_vantage_plan na dysku
https://docs.google.com/spreadsheets/d/1rUkWL9rVZo66Zlli0_L7dWfBYrdXTv9_O_wT6KcLfVI/edit?gid=1650940839#gid=1650940839
