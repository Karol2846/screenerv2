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
 - main data source (api name) ??
 - secondary data source ??


pobierane raz na kwartał (90 dni)
    marketCap ???
    quickRatio
    totalDebt
    totalAssets
    interestConverageRatio
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
    pegRatio - obliczane na podstawie peRatio/forwardEpsGrowth2Y (jako liczba całkowita)


marketCap
quickRatio
totalDebt
totalAssets
interestConverageRatio
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



TODO przeczytaj oceny chatów:
https://gemini.google.com/app/d8a726b4779a68ce?hl=pl
https://chatgpt.com/c/69541458-40a4-8332-88e1-b8816f94f92f
https://www.perplexity.ai/search/przeszukaj-internet-i-wskaz-ap-KwowuPZ9QEuLo72E4h87mQ
https://claude.ai/chat/742e6ce1-4da4-4114-8a81-43d4c1e66ba1