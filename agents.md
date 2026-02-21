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
├── adapter/                    ← INFRASTRUCTURE LAYER (outgoing adapters)
│   └── web/out/
│       ├── yhfinance/          ← YH Finance API Adapter
│       │   ├── YhFinanceApiClient.java        (MicroProfile REST Client interface)
│       │   ├── YhFinanceGateway.java          (implements YahooFinanceClient port)
│       │   ├── YhFinanceClientMapper.java     (DTO → domain command mapping)
│       │   ├── YhFinanceResponseLog.java      (entity logging raw JSON responses)
│       │   ├── exception/                     (ClientException, YhFinanceApiException, ExceptionMapper)
│       │   └── model/                         (DTOs: QuoteSummaryResponse, EarningsTrend, RecommendationTrend, etc.)
│       └── alphavantage/       ← Alpha Vantage API Adapter
│           ├── AlphaVantageApiClient.java      (MicroProfile REST Client interface)
│           ├── AlphaVantageGateway.java        (implements AlphaVantageClient port)
│           ├── AlphaVantageApiKeyFilter.java   (filter injecting API key into requests)
│           └── AlphaVantageResponseLog.java    (entity logging raw JSON responses)
│
├── application/                ← APPLICATION LAYER (ports)
│   └── port/out/
│       ├── yhfinance/
│       │   ├── YahooFinanceClient.java         (PORT — interface for fetching YH Finance data)
│       │   └── response/
│       │       └── YhFinanceResponse.java      (command/DTO returned by port)
│       └── alphavantage/
│           ├── AlphaVantageClient.java          (PORT — interface for Alpha Vantage)
│           ├── OverviewResponse.java            (DTO: company overview)
│           ├── BalanceSheetResponse/Report.java (DTO: balance sheet)
│           └── IncomeStatementResponse/Report.java (DTO: income statement)
│
└── domain/                     ← DOMAIN LAYER (pure business logic)
    ├── entity/
    │   ├── Stock.java              (Aggregate — ticker, sector, marketData; PanacheEntityBase)
    │   ├── MonthlyReport.java      (entity — monthly data: P/S, PEG, upside, analystRatings)
    │   └── QuarterlyReport.java    (entity — quarterly data: quickRatio, ICR, Altman Z-Score)
    ├── kernel/
    │   ├── CalculationResult.java  (sealed interface: Success | Failure | Skipped — monadic computation result)
    │   ├── CalculationGuard.java   (fluent validator: require(), ensureNonZero(), validate())
    │   ├── CalculationErrorType.java (enum: MISSING_DATA, DIVISION_BY_ZERO, NOT_APPLICABLE)
    │   ├── MetricType.java         (enum: PS_RATIO, FORWARD_PEG, UPSIDE_POTENTIAL, QUICK_RATIO, INTEREST_COVERAGE_RATIO, ALTMAN_Z_SCORE)
    │   └── ReportError.java        (record: metricType + errorType + reason)
    ├── valueobject/
    │   ├── AltmanZScore.java       (value object)
    │   ├── AnalystRatings.java     (@Embeddable: strongBuy, buy, hold, sell, strongSell)
    │   ├── FinancialMetric.java    (base interface/class for metrics)
    │   ├── ForwardPeg.java         (VO with compute() → CalculationResult)
    │   ├── InterestCoverageRatio.java (VO with compute())
    │   ├── MarketData.java         (@Embeddable: marketCap, currentPrice)
    │   ├── PsRatio.java            (VO with compute())
    │   ├── QuickRatio.java         (VO with compute())
    │   ├── ReportIntegrityStatus.java (enum: READY_FOR_ANALYSIS, PRICING_DATA_COLLECTED, FUNDAMENTALS_COLLECTED, MISSING_DATA)
    │   ├── Sector.java             (enum: TECHNOLOGY, HEALTHCARE, ENERGY, MINING, etc.)
    │   ├── UpsidePotential.java    (VO with compute())
    │   └── snapshoot/
    │       ├── FinancialDataSnapshot.java  (record/builder — snapshot for quarterly calculations)
    │       └── MarketDataSnapshot.java     (record/builder — snapshot for monthly calculations)
    └── service/
        └── AltmanScoreCalculator.java  (domain service — computes Z-Score/Z''-Score based on sector)
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
Entities extend `PanacheEntity` or `PanacheEntityBase`.
Operations: `entity.persist()`, `Entity.findById()`, `Entity.find("field", value)`.
`Stock` uses `PanacheEntityBase` with custom `@Id` (ticker); reports use `PanacheEntity` with auto-generated IDs.

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

**Naming strategy:** `CamelCaseToUnderscoresNamingStrategy` (Java camelCase → SQL snake_case).
**Schema management:** Hibernate set to `validate` (does not generate schema — Flyway only).

---

## External APIs

### YH Finance (yfapi.net)
- Modules: `earningsTrend`, `recommendationTrend`
- Data: EPS forecasts, revenue estimates, analyst recommendations, target price
- REST Client config key: `yhfinance-api`

### Alpha Vantage
- Functions: `OVERVIEW`, `BALANCE_SHEET`, `INCOME_STATEMENT`
- Data: balance sheet, income statement, fundamental indicators
- API key via env var `ALPHA_VANTAGE_API_KEY` (default: `demo`)
- REST Client config key: `alphavantage-api`

---

## Tests

Framework: JUnit 5 + AssertJ + REST Assured + ArchUnit

**Test coverage (15 test classes):**
- **Domain VO:** `PsRatioTest`, `QuickRatioTest`, `ForwardPegTest`, `InterestCoverageRatioTest`, `UpsidePotentialTest`
- **Domain Service:** `AltmanScoreCalculatorTest`
- **Domain Entity:** `MonthlyReportTest`, `QuarterlyReportTest`
- **Adapter deserialization:** `YhFinanceDtoDeserializationTest`, `AlphaVantageDtoDeserializationTest`
- **Adapter logging:** `YhFinanceResponseLogTest`, `AlphaVantageResponseLogTest`
- **Adapter mapping:** `YhFinanceClientMapperTest`
- **Fixtures:** `FinancialDataSnapshotFixture`, `MarketDataSnapshotFixture`

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

# Tests
./gradlew test
```

Requires: Java 25, PostgreSQL (configured in `application.yaml` or via Quarkus Dev Services).

---

## Key Configuration Files

| File | Description |
|---|---|
| `build.gradle` | Dependencies, Java 25 target |
| `gradle.properties` | Quarkus version (3.30.5) |
| `src/main/resources/application.yaml` | DB config, REST clients, API keys |
| `src/main/resources/config/currency-mapping.yaml` | Static exchange rates (USD, TWD, CNY, EUR, JPY, GBP) |
| `work_plan.md` | Detailed scoring plan |
| `data_collectedv2.md` | List of collected metrics + calculation formulas |

## Known TODOs / Open Issues

1. **Merge API calls** — how to combine YH Finance and Alpha Vantage data for MonthlyReport without wasting API requests (comment in `Stock.java`)
2. **totalRevenue in QuarterlyReport** — currently revenue from a single quarter, should be cumulative from 4 quarters (FIXME in `QuarterlyReport.java`)
3. **Scoring engine** — not yet implemented (described in `work_plan.md`)
4. **No `adapter/web/in` layer** — no REST controllers (inbound endpoints) exposing data externally yet
