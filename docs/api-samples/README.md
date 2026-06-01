# API Sample Responses

Raw JSON responses captured from live APIs on **2026-06-01**, used to verify sector taxonomy
and field availability for the Altman Z-Score mapping and `Sector.fromString` implementation.

## Endpoints

| API                       | Endpoint                                                                           | Auth header      |
|---------------------------|------------------------------------------------------------------------------------|------------------|
| AlphaVantage (RapidAPI)   | `GET https://alpha-vantage.p.rapidapi.com/query?function=OVERVIEW&symbol=<TICKER>` | `x-rapidapi-key` |
| Yahoo Finance (yfapi.net) | `GET https://yfapi.net/v11/finance/quoteSummary/<TICKER>?modules=assetProfile`     | `X-API-KEY`      |

## Key Finding — Taxonomy

Both APIs use the **Morningstar sector taxonomy** (not GICS or SIC).
- AlphaVantage (RapidAPI): **ALL CAPS** (e.g. `"FINANCIAL SERVICES"`)
- Yahoo Finance: **Title Case** (e.g. `"Financial Services"`)

`Sector.fromString` handles both via case-insensitive alias matching.

## Verified Sector Values

| Ticker | AV `Sector`               | YH `assetProfile.sector` | Enum constant            | Altman formula |
|--------|---------------------------|--------------------------|--------------------------|----------------|
| AAPL   | `TECHNOLOGY`              | `Technology`             | `TECHNOLOGY`             | Z''            |
| JPM    | `FINANCIAL SERVICES`      | `Financial Services`     | `FINANCE`                | Skip           |
| XOM    | `ENERGY`                  | `Energy`                 | `ENERGY`                 | Z''            |
| CAT    | `INDUSTRIALS`             | `Industrials`            | `INDUSTRIALS`            | Original Z     |
| JNJ    | `HEALTHCARE`              | `Healthcare`             | `HEALTHCARE`             | Z''            |
| WMT    | `CONSUMER DEFENSIVE`      | `Consumer Defensive`     | `CONSUMER_DEFENSIVE`     | Z''            |
| PLD    | `REAL ESTATE`             | `Real Estate`            | `REAL_ESTATE`            | Skip           |
| NEE    | `UTILITIES`               | `Utilities`              | `UTILITIES`              | Skip           |
| LIN    | `BASIC MATERIALS`         | `Basic Materials`        | `MINING`                 | Original Z     |
| VZ     | `COMMUNICATION SERVICES`  | `Communication Services` | `COMMUNICATION_SERVICES` | Z''            |

## Altman Formula Mapping (implemented in `AltmanScoreCalculator`)

| Formula                                        | Sectors                                                                              |
|------------------------------------------------|--------------------------------------------------------------------------------------|
| Original Z (5 factors, requires `revenueTTM`)  | `INDUSTRIALS`, `MINING`, `CONSUMER_DISCRETIONARY`                                    |
| Z'' (4 factors)                                | `TECHNOLOGY`, `HEALTHCARE`, `ENERGY`, `COMMUNICATION_SERVICES`, `CONSUMER_DEFENSIVE` |
| Skip                                           | `FINANCE`, `REAL_ESTATE`, `UTILITIES`, `OTHER`                                       |

## Files

```
alphavantage/
  overview-AAPL.json    — Technology / Consumer Electronics
  overview-JPM.json     — Financial Services / Banks - Diversified
  overview-XOM.json     — Energy / Oil & Gas Integrated
  overview-CAT.json     — Industrials / Farm & Heavy Construction Machinery
  overview-JNJ.json     — Healthcare / Drug Manufacturers - General
  overview-WMT.json     — Consumer Defensive / Discount Stores
  overview-PLD.json     — Real Estate / REIT - Industrial
  overview-NEE.json     — Utilities / Utilities - Regulated Electric
  overview-LIN.json     — Basic Materials / Specialty Chemicals
  overview-VZ.json      — Communication Services / Telecom Services

yhfinance/
  assetProfile-*.json   — same 10 tickers, Yahoo Finance assetProfile module
```
