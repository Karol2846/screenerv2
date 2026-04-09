# agents.md — Project Context for AI Agents

## Project Overview

**screenerv2** — a backend application for automated analysis and scoring of US stock market companies.
The goal is to build a scoring system (0-100 points) that filters stocks, fetches financial data from external APIs, and computes fundamental, valuation, and analyst metrics.

- **Language:** Java 25
- **Framework:** Quarkus 3.30.5
- **Build:** Gradle (Groovy DSL)
- **Database:** PostgreSQL + Hibernate ORM Panache + Flyway
- **External APIs:** YH Finance (yfapi.net), Alpha Vantage
- **Libraries:** Lombok, Jackson, REST Client (MicroProfile), Quarkus Scheduler

---

## Architecture — Hexagonal (Ports & Adapters)

Base package: `com.stock.screener`

```
com.stock.screener
├── analyzer/                   ← ANALYSIS BOUNDED CONTEXT (scoring facade, currently minimal)
│   └── application/
│       ├── port/in/AnalyzeStockUseCase.java
│       └── service/
│           ├── StockAnalysisService.java
│           └── AnalysisReport.java
├── collector/                  ← COLLECTION BOUNDED CONTEXT (hexagonal)
│   ├── adapter/
│   │   ├── in/
│   │   │   ├── web/            (manual triggers: MonthlyCollectorController, QuarterlyCollectorController)
│   │   │   └── scheduler/      (MonthlyCollectorScheduler, QuarterlyCollectorScheduler)
│   │   └── out/
│   │       ├── file/           (FileTickerReaderAdapter)
│   │       └── web/
│   │           ├── yhfinance/  (YhFinanceApiClient, YhFinanceGateway, mapper, logs, exception/, model/)
│   │           └── alphavantage/ (AlphaVantageApiClient, AlphaVantageGateway, mapper, logs, exception/, model/)
│   ├── application/
│   │   ├── port/
│   │   │   ├── in/             (CollectMonthlyDataUseCase, CollectQuarterlyDataUseCase)
│   │   │   └── out/            (alphavantage/, yhfinance/, file/)
│   │   └── service/            (MonthlyDataCollectorService, QuarterlyDataCollectorService, StockDataMapper)
│   └── domain/
│       ├── entity/             (MonthlyReport, QuarterlyReport)
│       ├── kernel/             (CalculationResult, CalculationGuard, MetricType, ReportError, ...)
│       ├── valueobject/        (PsRatio, ForwardPeg, QuickRatio, ...)
│       │   └── snapshot/       (FinancialDataSnapshot, MarketDataSnapshot)
│       └── service/            (AltmanScoreCalculator)
└── common/
    └── Sector.java             (shared enum used by collector + analyzer)
```

---

## Key Patterns & Conventions

### 1. Self-Calculating Value Objects
Value Objects (e.g. `PsRatio`, `QuickRatio`, `ForwardPeg`) expose a static `compute(snapshot)` method returning `CalculationResult<T>`.
Usage pattern:
```java
PsRatio.compute(snapshot)
    .onSuccess(ps -> this.psRatio = ps)
    .onFailure(failure -> {
        this.psRatio = null;
        this.calculationErrors.add(fromFailure(PS_RATIO, failure));
    });
```

### 2. CalculationResult — Monadic Result Type
Sealed interface with variants `Success`, `Failure`, `Skipped`.
Fluent API: `.onSuccess()`, `.onFailure()`, `.onSkipped()` (chainable).
Factory methods: `success()`, `failure()`, `missingData()`, `divisionByZero()`, `skip()`.

### 3. CalculationGuard — Input Validation
Fluent builder for checking required fields and division-by-zero before computation:
```java
CalculationGuard.check(snapshot)
    .require("totalAssets", FinancialDataSnapshot::totalAssets)
    .ensureNonZero("totalLiabilities", FinancialDataSnapshot::totalLiabilities)
    .validate(snapshot -> new AltmanZScore(computeScore(snapshot)));
```

### 4. ReportIntegrityStatus — Data Completeness Tracking
Each report (Monthly/Quarterly) automatically updates its `integrityStatus` after recalculating metrics:
- `READY_FOR_ANALYSIS` — all data complete
- `PRICING_DATA_COLLECTED` / `FUNDAMENTALS_COLLECTED` — partial data
- `MISSING_DATA` — missing data

### 5. Persistence — Active Record (Panache)
Entities extend `PanacheEntity`.
Operations: `entity.persist()`, `Entity.findById()`, `Entity.find("field", value)`.
Current domain entities (`MonthlyReport`, `QuarterlyReport`) use `PanacheEntity` with auto-generated IDs.

### 6. API Response Logging
Both adapters (YhFinance, AlphaVantage) persist raw JSON responses in dedicated log tables — useful for debugging and data replay.

---

## Database

**PostgreSQL** with **Flyway** migrations (`src/main/resources/db/migration/`):

| Migration | Description |
|---|---|
| V1 | Tables: `stock`, `monthly_report`, `quarterly_report` + indexes |
| V2 | Add `calculation_errors` (JSONB) to `quarterly_report` |
| V3 | Add `calculation_errors`, `integrity_status`, `updated_at` to `monthly_report` |
| V4 | Additional indexes on `stock.ticker` |
| V5 | Table `alpha_vantage_response_log` |
| V6 | Table `yh_finance_response_log` |
| V7 | Move `sector` to reports and remove `stock` table |
| V8 | Add `revenueTTM` column to `quarterly_report` |

**Naming strategy:** `CamelCaseToUnderscoresNamingStrategy` (Java camelCase → SQL snake_case).
**Schema management:** Hibernate set to `validate` (does not generate schema — Flyway only).

---

## External APIs

### YH Finance (yfapi.net)
- Modules: `earningsTrend`, `recommendationTrend`
- Data: EPS forecasts, revenue estimates, analyst recommendations, target price
- REST Client config key: `yhfinance-api`

### Alpha Vantage
- Functions: `OVERVIEW`, `BALANCE_SHEET`, `INCOME_STATEMENT`, `CASH_FLOW`
- Data: balance sheet, income statement, fundamental indicators
- API key via env var `AV_API_KEY` (bound to `alphavantage.api.key`)
- REST Client config key: `alphavantage-api`

---

## Tests

Framework: JUnit 5 + AssertJ + REST Assured + ArchUnit + WireMock (integration tests)

**Test coverage (unit + integration):**
- **Domain VO:** `PsRatioTest`, `QuickRatioTest`, `ForwardPegTest`, `InterestCoverageRatioTest`, `UpsidePotentialTest`
- **Domain Service:** `AltmanScoreCalculatorTest`
- **Domain Entity:** `MonthlyReportTest`, `QuarterlyReportTest`
- **Application mapping:** `StockDataMapperTest`
- **Adapter deserialization:** `YhFinanceDtoDeserializationTest`, `AlphaVantageDtoDeserializationTest`, `AlphaVantageResponseMapperTest`
- **Adapter file source:** `FileTickerReaderAdapterTest`
- **Adapter logging:** `YhFinanceResponseLogTest`, `AlphaVantageResponseLogTest`
- **Adapter mapping:** `YhFinanceClientMapperTest`
- **Integration:** `MonthlyReportCollectionIT` (+ WireMock helpers in `src/integrationTest/java/com/stock/screener/wiremock/`)
- **Fixtures:** `FinancialDataSnapshotFixture`, `MarketDataSnapshotFixture`, `RawOverviewFixture`

---

## Business Logic — Scoring (planned, from work_plan.md)

Stocks scored across 4 categories (max 95 pts, target 100):

| Category | Max pts | Metrics |
|---|---|---|
| **Safety** | 12 | quickRatio, debtRatio, ICR, Altman Z-Score |
| **Growth** | 28 | revenueGrowthMean 3Y, forwardRevenueGrowth, forwardEpsGrowth, OCF/NI |
| **Valuation** | 30 | P/S vs 4Y median, PEG ratio |
| **Analyst Opinion** | 25 | upside potential, priceTargetTrend |

Pre-filters (REJECT):
- Excluded sectors: Financials, Real Estate, Energy, Utilities, Basic Materials
- marketCap < $3B USD
- quickRatio < 0.8, Altman Z < 1.8 (or 1.1 for tech), negative revenueGrowth

Anomaly flags: `POSSIBLE_SPECULATION_BUBBLE`, `CASH_FLOW_LESS_THAN_NET_INCOME`, `MISSING_DATA`, etc.

---

## How to Run

```bash
# Dev mode (hot reload)
./gradlew quarkusDev

# Build
./gradlew build

# Unit tests
./gradlew test

# Integration tests (WireMock)
./gradlew integrationTest
```

Default local config has `quarkus.scheduler.enabled=false` (manual collection via REST endpoints in `collector/adapter/in/web`).

Requires: Java 25, PostgreSQL (configured in `application.yaml` or via Quarkus Dev Services).

---

## Key Configuration Files

| File | Description |
|---|---|
| `build.gradle` | Dependencies, Java 25 target |
| `gradle.properties` | Quarkus version (3.30.5) |
| `src/main/resources/application.yaml` | DB config, REST clients, API keys |
| `src/integrationTest/resources/application.yaml` | Integration-test config (WireMock + scheduler disabled) |
| `src/main/resources/config/currency-mapping.yaml` | Static exchange rates (USD, TWD, CNY, EUR, JPY, GBP) |
| `work_plan.md` | Detailed scoring plan |
| `data_collectedv2.md` | List of collected metrics + calculation formulas |

## Known TODOs / Open Issues

1. **Merge API calls** — how to combine YH Finance and Alpha Vantage data for MonthlyReport without wasting API requests (comment in `MonthlyReport.java`)
2. **Rate limiting** — add robust rate limiting for YH Finance and Alpha Vantage (`TODO` in `YhFinanceApiClient.java`)
3. **Scoring engine** — not yet implemented (described in `work_plan.md`)
4. **Quarterly scheduling strategy** — improve trigger condition to use report freshness (`TODO` in `QuarterlyCollectorScheduler.java`)
