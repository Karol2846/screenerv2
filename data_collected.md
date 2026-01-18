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

ticker
sector
marketCap
quickRatio
totalDebt
totalAssets
interestConverageRatio
altmanZScore
revenue
forwardRevenueGrowth
forwardEpsGrowth
operativeCashFlow
netIncome
psCurrent
peratio (jeśli forward nawet lepiej)
peRatio (jeśli forward to nawet lepiej)
targetPrice
currentPrice - moze być z yhFinance

// będzie pobierane z innego API
analystRatings - strongbuy, buy, hold, sell, strong sell, counts

//FIXME: obawiam się, że yhFInance jest zbyt niestabilne i nie mogę z nieg pobierać danych :(
// ale być może alphaVantege będzie lepsze - wtedy mogę poierać z kilku tokenów na raz 


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


3. Obliczanie Altman Z''-Score (Dla firm nieprodukcyjnych/Tech)
   Wzór: Z'' = 6.56*T1 + 3.26*T2 + 6.72*T3 + 1.05*T4

Do tego potrzebujesz danych, których kompletnie nie ma w Twoim YH JSON:

T1 = (Current Assets - Current Liabilities) / Total Assets -> Wszystkie 3 składniki musisz wziąć z Alpha Vantage.
T2 = Retained Earnings / Total Assets -> Oba z Alpha Vantage.
T3 = EBIT / Total Assets -> Oba z Alpha Vantage.
T4 = Book Value of Equity / Total Liabilities -> Total Liabilities z Alpha Vantage.