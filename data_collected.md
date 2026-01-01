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


marketCap - quoteSummary.marketCap or price.marketCap
quickRatio - financialData.quickRatio
totalDebt - financialData.totalDebt
totalAssets - summaryDetails.totalAssets or defaultKeyStatistics.totalAssets
interestConverageRatio    <- ebit / interestExpense
altmanZScore
revenue (lube revenueGrowthMean3Years)
forwardRevenueGrowth2Y
forwardEpsGrowth2Y     - defaultKeyStatistics.forwardEps
operativeCashFlow
netIncome
psCurrent
historicalPs (do liczenia mediany)
peRatio (jeśli forward to nawet lepiej)   - defaultKeyStatistics.forwardPE
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


tutaj udało mi się ustalić na prawdę spoko rzeczy:
https://gemini.google.com/app/def9d35b6be9c839?hl=pl

i tutaj fajna rozpiska co skąd brać:
https://docs.google.com/spreadsheets/d/1bqiRxCcWwADv6OdTYUyjw4_D0TAGLnE4hmxwwRExeu0/edit?gid=268937394#gid=268937394
