2. pobieranie danych (z bazy lub api):
    1. Podstawowe dane fundamentalne (pobierane raz na 90 dni):
        - operativeCashFlow (operating cash flow)
        - revenue,
        - netIncome,
        - quickRatio,
        - totalDebt,
        - totalAssets,
        - interest converage ratio (zysk operacyjny)
        - altman-z score

pobierane raz na zawsze XD
 - ticker
 - sector (enum)

pobierane raz na kwartał (90 dni)
    marketCap ???
    quickRatio
    totalDebt
    totalAssets
    interestConverageRatio    <- ebit / interestExpense
    revenue
    operativeCashFlow
    netIncome
    altmanZScore
    

pobierane raz na miesiąc:
    forwardRevenueGrowth2Y
    forwardEpsGrowth2Y
    targetPrice
    analystRatings - strongbuy, buy, hold, sell, strong sell, counts


pobierane codzinnie / na potrzebe analizy:
    peRatio
    psCurrent
    currentPrice
    pegRatio - obliczane na podstawie peRatio/eps (jako liczba całkowita)


marketCap
quickRatio
totalDebt
totalAssets
interestConverageRatio    <- ebit / interestExpense
altmanZScore
revenue (lube revenueGrowthMean3Years)
forwardRevenueGrowth2Y
forwardEpsGrowth2Y
operativeCashFlow
netIncome
psCurrent
historicalPs (do liczenia mediany)
peRatio (jeśli forward to nawet lepiej)
targetPrice (mean target price od analityków)
currentPrice
analystRatings - strongbuy, buy, hold, sell, strong sell, counts


finhub.io
quote - currentPrice
Recommendation Trends - analystRatings



api yh finance - https://rapidapi.com/3b-data-3b-data-default/api/yahoo-finance-real-time1
recomendation trend - analystRatings (można odrazu za 3 miesiące)
balance sheet:
- forwardPE
- totalAssets - annualTotalAssets[-1].reportedValue.raw
- marketCap - summaryDetail.marketCap or price.marketCap
- revenue - incomeStatementHistory.incomeStatementHistory[0].totalRevenue.raw or financialsChart.quaterly.revenue   <- możeliwe też załadowanie danymi z yearly
- netIncome - incomeStatementHistory.incomeStatementHistory[0].netIncome.raw



Okej, jak na razie stanęło na tym, żę jednk zostaje yhFinance i ew. pomocniczy finhub, bo ma fajne recomendations
Jeszcze walczę z gemini jakie dokładnie dane najlepiej z którego api
https://gemini.google.com/app/def9d35b6be9c839?hl=pl


alpha vantage też ma baardzo dobre api, ale tylko 25req/day - a to zdecydowanie za mało
