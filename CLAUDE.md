# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Source of truth

**Code beats docs.** If `work_plan.md` or any other `.md` file contradicts what the code does, trust the code and update the doc. `work_plan.md` is a *target spec* — several sections describe planned behaviour that is **not yet implemented** (see [Plan vs. implementation](#plan-vs-implementation-gaps) below).

---

## Project overview

**screenerv2** — a Quarkus backend for automated collection and storage of US stock financial data. The long-term goal is a 0–100 fundamental scoring system, but **the current phase is data collection only**: reliable ingestion from external APIs, proper scheduling intervals, and a clean, well-populated database. Filtering and scoring are future work.

| Item              | Value                                                                            |
|-------------------|----------------------------------------------------------------------------------|
| Language          | Java 25                                                                          |
| Framework         | Quarkus 3.30.5                                                                   |
| Build             | Gradle (Groovy DSL)                                                              |
| Database          | PostgreSQL + Hibernate ORM Panache + Flyway                                      |
| External APIs     | Yahoo Finance (`yfapi.net`), Alpha Vantage                                       |
| Key libraries     | Lombok, Jackson, MicroProfile REST Client, Quarkus Scheduler, ArchUnit, WireMock |
| Required env vars | `YH_FINANCE_API_KEY`, `AV_API_KEY`                                               |

---

## Architecture — Hexagonal (Ports & Adapters)

Base package: `com.stock.screener`

```
com.stock.screener
├── analyzer/                         ← FUTURE: scoring facade (not the current focus)
│   └── application/
│       ├── port/in/AnalyzeStockUseCase.java
│       └── service/
│           ├── StockAnalysisService.java  ← placeholder only; will become the facade
│           │                                for the full filtering + scoring pipeline
│           └── AnalysisReport.java
├── collector/                        ← COLLECTION context (hexagonal, fully implemented)
│   ├── adapter/
│   │   ├── in/
│   │   │   ├── web/       (MonthlyCollectorController, QuarterlyCollectorController)
│   │   │   └── scheduler/ (MonthlyCollectorScheduler, QuarterlyCollectorScheduler)
│   │   └── out/
│   │       ├── file/      (FileTickerReaderAdapter — reads tickers.txt from classpath)
│   │       └── web/
│   │           ├── yhfinance/    (YhFinanceApiClient, YhFinanceGateway, mapper, logs, exception/, model/)
│   │           └── alphavantage/ (AlphaVantageApiClient, AlphaVantageGateway, mapper, logs, exception/, model/)
│   ├── application/
│   │   ├── port/
│   │   │   ├── in/  (CollectMonthlyDataUseCase, CollectQuarterlyDataUseCase)
│   │   │   └── out/ (alphavantage/, yhfinance/, file/ ports)
│   │   └── service/ (MonthlyDataCollectorService, QuarterlyDataCollectorService, StockDataMapper)
│   └── domain/
│       ├── entity/      (MonthlyReport, QuarterlyReport — extend PanacheEntity)
│       ├── kernel/      (CalculationResult, CalculationGuard, MetricType, ReportError, CalculationErrorType)
│       ├── valueobject/ (PsRatio, QuickRatio, ForwardPeg, InterestCoverageRatio, AltmanZScore, AnalystRatings,
│       │                 UpsidePotential, ReportIntegrityStatus, FinancialMetric)
│       │   └── snapshot/ (FinancialDataSnapshot, MarketDataSnapshot)
│       └── service/     (AltmanScoreCalculator)
└── common/
    └── Sector.java      (shared enum: TECHNOLOGY, HEALTHCARE, FINANCE, ENERGY, REAL_ESTATE,
                          UTILITIES, MINING, CONSUMER_CYCLICAL, CONSUMER_DISCRETIONARY,
                          COMMUNICATION_SERVICES, OTHER)
```

**`collector`** is the current active context — orchestrates API calls → maps to domain snapshots → upserts `MonthlyReport` / `QuarterlyReport`. All active development happens here.  
**`analyzer`** is a placeholder for the future scoring pipeline. `StockAnalysisService` will become the facade that coordinates sector/marketCap filtering and the scoring engine — but this is **not the current phase**.  
**`common`** holds `Sector`, shared by both contexts.

---

## Key patterns & conventions

### 1. Self-calculating Value Objects

Value Objects (`PsRatio`, `QuickRatio`, `ForwardPeg`, …) expose a `static compute(snapshot)` factory that returns `CalculationResult<T>`. Canonical examples: `QuickRatio.java`, `PsRatio.java`.

```java
PsRatio.compute(snapshot)
    .onSuccess(ps -> this.psRatio = ps)
    .onFailure(failure -> {
        this.psRatio = null;
        this.calculationErrors.add(fromFailure(PS_RATIO, failure));
    });
```

### 2. CalculationResult — sealed monadic result type

Variants: `Success`, `Failure`, `Skipped`.  
Fluent API: `.onSuccess()`, `.onFailure()`, `.onSkipped()` — all chainable.  
Factory methods: `success()`, `failure()`, `missingData()`, `divisionByZero()`, `skip()`.

### 3. CalculationGuard — input validation

Fluent precondition chain before computation:

```java
CalculationGuard.check(snapshot)
    .require("totalAssets", FinancialDataSnapshot::totalAssets)
    .ensureNonZero("totalLiabilities", FinancialDataSnapshot::totalLiabilities)
    .validate(s -> new AltmanZScore(computeScore(s)));
```

### 4. ReportIntegrityStatus

Lifecycle flag stamped on each entity after metric recalculation:  
`PRICING_DATA_COLLECTED` → `FUNDAMENTALS_COLLECTED` → `READY_FOR_ANALYSIS` / `MISSING_DATA`.

### 5. Snapshots as input contracts

`FinancialDataSnapshot` and `MarketDataSnapshot` are immutable Lombok-built records. They decouple raw API DTOs from domain calculations — all VO `compute()` methods accept a snapshot, not raw responses.

### 6. Persistence — Active Record (Panache)

Entities extend `PanacheEntity`. Operations: `entity.persist()`, `Entity.findById()`, `Entity.find("field", value)`.  
Raw API responses are persisted as JSONB in `alpha_vantage_response_log` / `yh_finance_response_log`.

### 7. StockDataMapper

Application-layer translator (`application/service/StockDataMapper`) that merges AlphaVantage + YH Finance DTOs into `FinancialDataSnapshot` / `MarketDataSnapshot`. Complex logic lives here (e.g. `revenueTTM` by summing last 4 quarters, EBIT derivation, debt fallback). Do not move this to adapters.

---

## Database

PostgreSQL + Flyway. Hibernate is set to `validate` — Flyway owns the schema, Hibernate never auto-generates it.  
**Naming strategy:** `CamelCaseToUnderscoresNamingStrategy` (Java camelCase → SQL snake_case).

| Migration | Description                                                                |
|-----------|----------------------------------------------------------------------------|
| V1        | `stock`, `monthly_report`, `quarterly_report` + indexes                    |
| V2        | `calculation_errors` JSONB on `quarterly_report`                           |
| V3        | `calculation_errors`, `integrity_status`, `updated_at` on `monthly_report` |
| V4        | Additional indexes on `stock.ticker`                                       |
| V5        | `alpha_vantage_response_log` table                                         |
| V6        | `yh_finance_response_log` table                                            |
| V7        | Drop `stock` table (sector moved into reports)                             |
| V8        | `revenue_ttm` column on `quarterly_report`                                 |
| V9        | `UNIQUE(ticker, fiscal_date_ending)` on `quarterly_report`                 |

Dev/test relies on **Quarkus Dev Services** (auto-started Docker Postgres). No datasource URL is configured in `application.yaml` — do not add one for dev; let Dev Services handle it.

---

## External APIs

|                   | Yahoo Finance                                         | Alpha Vantage                                                |
|-------------------|-------------------------------------------------------|--------------------------------------------------------------|
| Config key        | `yhfinance-api`                                       | `alphavantage-api`                                           |
| Base URL          | `https://yfapi.net` (env configurable)                | `https://alpha-vantage.p.rapidapi.com/`                      |
| API key env var   | `YH_FINANCE_API_KEY`                                  | `AV_API_KEY`                                                 |
| Modules/functions | `earningsTrend`, `recommendationTrend`, `price`       | `OVERVIEW`, `BALANCE_SHEET`, `INCOME_STATEMENT`, `CASH_FLOW` |
| Data provided     | EPS/revenue forecasts, analyst targets, current price | Balance sheet, income statement, fundamental indicators      |
| **Actual role**   | **Primary** for forecasts & analysts                  | **Primary** for fundamentals (all 4 functions always called) |

> **Note:** `work_plan.md` describes Alpha Vantage as a *fallback* when YH data is missing. In the current code the roles are inverted — AV is the primary source for fundamentals and the fallback path is not implemented.

---

## Build / run / test

```bash
# Dev mode (hot reload, Dev UI at http://localhost:8080/q/dev/)
./gradlew quarkusDev

# Unit tests only
./gradlew test

# Integration tests only (Docker required for Dev Services Postgres + WireMock)
./gradlew integrationTest

# Both (check.dependsOn integrationTest)
./gradlew check
./gradlew build

# Single test class
./gradlew test --tests com.stock.screener.collector.domain.valueobject.PsRatioTest
./gradlew integrationTest --tests com.stock.screener.collector.MonthlyReportCollectionIT

# Native image (requires GraalVM or Docker for container build)
./gradlew build -Dquarkus.native.enabled=true
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

Schedulers are **disabled by default** (`quarkus.scheduler.enabled: false`). Trigger collection manually:

```
POST /api/collector/monthly/{ticker}   # collect monthly data for one ticker
POST /api/collector/monthly/all        # collect for all tickers in tickers.txt
POST /api/collector/quarterly/{ticker}
POST /api/collector/quarterly/all
```

---

## Architectural rules

Persona: **senior Java + DDD architect** — critical, no "yes-man", concrete and concise. Push back on shortcuts that compromise architectural integrity.

1. **Domain isolation** — the domain layer must not depend on adapters or infrastructure. Never let external API DTOs leak into the domain. Map at the application layer (`StockDataMapper`).
2. **No entity leakage** — never expose Panache entities through REST endpoints. Always map to a Record/DTO first.
3. **Java 25 features** — use records, sealed types, switch expressions, pattern matching for instanceof. Avoid boilerplate where a modern language feature handles it.
4. **Panache discipline** — use the Repository pattern (`PanacheRepository<E>`) for complex queries. Active Record (`extends PanacheEntity`) is acceptable only for simple internal CRUD, never exposed outside the domain implementation.
5. **Constructor injection** — use `@RequiredArgsConstructor` (Lombok) or an explicit constructor. Do not use field-level `@Inject`.
6. **Blocking I/O** — use Mutiny `Uni`/`Multi` or `@RunOnVirtualThread` for outbound HTTP calls that block.
7. **New value objects** — follow the `CalculationGuard → CalculationResult` pattern exactly. See `QuickRatio.java` and `PsRatio.java` as canonical references. Never compute a metric inline in a service.

---

## Testing rules

1. **AssertJ only** — `assertThat(...)` everywhere. Never use JUnit's `assertEquals`, `assertTrue`, etc.
2. **Structure** — `@DisplayName` on every class and test method; `@Nested` to group scenarios; `// Given / When / Then` comments inside each test body.
3. **BigDecimal** — compare with `isCloseTo(..., within(new BigDecimal("0.0001")))`. 4-decimal scale is the project convention.
4. **Fixtures** — hand-rolled fluent builders in a sibling `fixtures/` package. Pattern:
   ```java
   public static FinancialDataSnapshotFixture aFinancialDataSnapshot() { return new FinancialDataSnapshotFixture(); }
   public FinancialDataSnapshotFixture withTotalRevenue(String v) { this.totalRevenue = new BigDecimal(v); return this; }
   public FinancialDataSnapshot build() { … }
   ```
   Default values should be realistic and complete; only override what a given scenario needs.
5. **DTO deserialization tests** — use `JsonFixtureLoader.load("name.json", Dto.class)`. JSON fixtures live in `src/test/resources/alphavantage/`.
6. **Integration tests** — `@QuarkusTest` + `@TestProfile(IntegrationTestProfile.class)` + `@ExtendWith(WireMockServerConfig.class)`. Register WireMock stubs in `@BeforeAll` via `AlphaVantageWireMock` / `YhFinanceWireMock` static helpers. Stub JSON bodies go in `src/integrationTest/resources/stubs/__files/`. Verify DB state via Panache counts or `assertResponseLogs(em)`.
7. **ArchUnit** — already on the classpath. Add `@ArchTest` rules when introducing new cross-package dependencies to prevent domain leakage.
8. **Source sets** — unit tests: `src/test/java`; integration tests: `src/integrationTest/java` (custom Gradle source set, runs after unit tests via `check`).

---

## Current phase: data collection

**Active work:** solid data ingestion, correct scheduling intervals, clean database state. Everything in the `analyzer` context and all filtering/scoring logic is explicitly **future work** — do not implement it until the collection layer is stable and well-tested.

### Future work (not to be touched now)

| Item | `work_plan.md` target | Status |
|---|---|---|
| Scoring engine | 4 categories, 0–100 pts | Future — `StockAnalysisService` is a placeholder |
| Sector pre-filter | Reject Financials, Real Estate, Energy, Utilities, Basic Materials | Future |
| MarketCap filter | Reject < $3B USD | Future |
| `ScoreBreakdown` on `AnalysisReport` | Required field | Future |

### Collection layer — known gaps vs. `work_plan.md`

These matter now because they affect data quality:

| Topic                  | `work_plan.md` says                      | Current code                                                       |
|------------------------|------------------------------------------|--------------------------------------------------------------------|
| AlphaVantage role      | Fallback when YH data missing            | Inverted — AV is primary for fundamentals; no fallback path exists |
| Repository + TTL cache | 90-day / 1-day / 30-day freshness checks | Not implemented — collectors always call APIs                      |
| `Sector` enum names    | "Basic Materials", "Industrials"         | Code has `MINING`; no `INDUSTRIALS`                                |
| Schedulers             | Driven by cron                           | Disabled by default; REST manual trigger is the dev workflow       |

---

## Confirmed financial decisions (verified against AlphaVantage + Yahoo APIs, AAPL 2026-Q2)

The following decisions have been agreed with the user and verified empirically. Implement accordingly:

### Domain semantics

| Topic                          | Decision                                                                                                                                                                                        | Verification                                                                                                                    |
|--------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| **`QuarterlyReport` storage**  | One row per `(ticker, fiscalDateEnding)` — accumulate full historical series. Needed for moving averages, P/S 4Y median, growth 3Y.                                                             | Fixed — upsert by `(ticker, fiscalDateEnding)` in `QuarterlyDataCollectorService`; `UNIQUE` constraint added in V9.             |
| **revenueTTM source of truth** | Sum of last 4 quarterly `totalRevenue` from `INCOME_STATEMENT`. AV's `OVERVIEW.RevenueTTM` is rejected as a source.                                                                             | AAPL: AV scalar = $451,442,016,000; sum-of-4-quarters = $451,442,000,000 (Δ=$16k, pure rounding).                               |
| **marketCap source**           | `AlphaVantage.OVERVIEW.MarketCapitalization` is live (matches Yahoo's `price.marketCap` to within $480 on $4.3T). Acceptable for the planned >$3B filter.                                       | AAPL: AV=$4,308,095,468,000; YH=$4,308,095,467,520.                                                                             |
| **Total debt formula**         | Keep current code (`shortLongTermDebtTotal` then `shortTermDebt + longTermDebt`). **Do NOT add `currentLongTermDebt`** — AV duplicates it inside `shortTermDebt`, so adding would double-count. | AAPL: `shortTermDebt`=`currentLongTermDebt`=$10.307B; `shortLongTermDebtTotal`=`shortTermDebt + longTermDebt`=$84.711B exactly. |
| **Forward growth unit**        | Yahoo returns `growth.raw` as **decimal** (e.g. 0.0959 = 9.59%). The existing `multiply(100)` in `ForwardPeg` is correct.                                                                       | AAPL `+1y` growth raw = 0.0959, fmt = "9.59%".                                                                                  |

### Altman Z-Score sector mapping (per Altman's published guidance)

Authoritative thresholds and applicability:
- **Original Z (1968)**: public manufacturing companies. Distress < 1.81, safe > 2.99.
- **Z'' (1995)**: non-manufacturing & emerging markets, drops T5 (asset turnover). Distress < 1.1, safe > 2.6.
- Altman explicitly **does not recommend** Z for: financials/banks, REITs, insurers (their leverage is structural, not risk).

Correct mapping for `AltmanScoreCalculator`:

| Sector                                                                                   | Formula                             | Why                                                                            |
|------------------------------------------------------------------------------------------|-------------------------------------|--------------------------------------------------------------------------------|
| `INDUSTRIALS` (to be added), `MINING`, `CONSUMER_CYCLICAL`†                              | Original Z                          | Classic manufacturing-like balance sheets, asset turnover meaningful.          |
| `TECHNOLOGY`, `HEALTHCARE`, `CONSUMER_DISCRETIONARY`, `COMMUNICATION_SERVICES`, `ENERGY` | Z''                                 | Non-manufacturing; T5 distorted by asset-light or commodity-driven structures. |
| `FINANCE`, `REAL_ESTATE`, `UTILITIES`                                                    | **Skip** (`CalculationResult.skip`) | Altman explicitly excludes these; regulated/depository capital structure.      |
| `OTHER`                                                                                  | Skip                                | Cannot pick a formula without sector certainty.                                |

† `CONSUMER_CYCLICAL` and `CONSUMER_DISCRETIONARY` are duplicates representing the same S&P sector (Yahoo vs. AlphaVantage naming). Merge into `CONSUMER_DISCRETIONARY` and map both API strings to it in `Sector.fromString`.

### Calculation refinements

| Metric                                  | Refinement                                                                                                                                                                                                 | Reason                                                                                                                                                                                                                   |
|-----------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **EBIT fallback**                       | 3-tier: `ebit` → **`operatingIncome`** → `netIncome + interestExpense + incomeTaxExpense`. Currently the middle tier is missing.                                                                           | `operatingIncome` is the cleanest EBIT — excludes non-operating items (investment gains, special items). Critical for companies with large investment portfolios.                                                        |
| **RetainedEarnings fallback**           | Full identity: `Equity − CommonStock − APIC + TreasuryStock − AOCI`. Current code subtracts only CS+APIC, which silently mis-attributes treasury & AOCI.                                                   | For AAPL (treasury ~$100B+) the error is in the tens of billions, distorting Altman T2. Verify AV returns `treasuryStock` and `accumulatedOtherComprehensiveIncome`; if not — drop the fallback and emit `MISSING_DATA`. |
| **Quick Ratio**                         | Subtract `prepaidExpenses` in addition to `inventory`: `(currentAssets − inventory − prepaidExpenses) / currentLiabilities`.                                                                               | Standard acid-test. Material for retail / SaaS with significant prepayments. Verify AV returns the field; otherwise keep current.                                                                                        |
| **Interest Coverage Ratio**             | Negative EBIT → emit `OPERATING_LOSS` flag, value = null. Very low / null `interestExpense` (debt-free) → emit positive `NO_DEBT` flag, value = null; **scoring must treat NO_DEBT as a positive signal**. | Negative ICR computed numerically is misleading; debt-free is a strength, not "missing data".                                                                                                                            |
| **`forwardEpsGrowth` period selection** | `YhFinanceClientMapper:22` uses `getLast()` on `earningsTrend.trend`. Currently works only because Yahoo returns 4 trends and the last is `+1y`. Fragile — replace with explicit filter `period == "+1y"`. | Yahoo can return additional periods (`+5y`, `-5y trailing`); `getLast()` would silently return wrong data.                                                                                                               |

### Open questions (to revisit when scoring engine is built)

- **PEG methodology**: our 1-year forward PEG vs. Yahoo's 5-year PEG diverge meaningfully (AAPL: ours = 3.20, Yahoo's = 2.57). The `work_plan.md` threshold "PEG < 1.5 = +10 pts" likely assumes 5-year. Decide which to use during scoring engine design.
- **AnalystRatings + numberOfAnalysts**: 1 analyst's bullish target ≠ 30 analysts' consensus. `MarketDataSnapshot` does not capture analyst count; consider adding before scoring.

---

## Confirmed bugs to fix (priority order)

These are all in the collection layer and must be fixed before relying on the database for analysis:

1. **`Skipped` polluting `calculationErrors`** (`QuarterlyReport:151-154`). `recalculateAltmanZScore.onSkipped` adds to `calculationErrors`, then `updateIntegrityStatus` checks `calculationErrors.isEmpty()` → tech companies with legitimate Altman skip are flagged `MISSING_DATA`. Skipped is a valid state, not an error.
2. **`AlphaVantageGateway` missing response validation** (vs. `YhFinanceGateway` which validates and throws `ClientException`). AV silently maps rate-limit responses (`{"Note": "..."}`) to all-null fields; collection "succeeds" with empty data.
3. **Altman sector mapping wrong** — see table above.
4. **EBIT fallback missing `operatingIncome` middle tier** — see table above.
5. **RetainedEarnings fallback ignores treasury & AOCI** — see table above.
6. **Quick Ratio missing `prepaidExpenses`** — see table above.
7. **ICR no `OPERATING_LOSS` / `NO_DEBT` flagging** — see table above.
8. **`Sector` enum: missing `INDUSTRIALS`, duplicate `CONSUMER_*`** — add `INDUSTRIALS`, merge cyclical→discretionary, fix `fromString`.
9. **`MonthlyReport.updateIntegrityStatus` edge case** — when pricing+fundamentals complete but ForwardPeg failed, status falls to `MISSING_DATA` (too harsh).
10. **`YhFinanceClientMapper` uses `getLast()` on trend** — replace with explicit `+1y` filter.
11. **Rate limiting absent** for both APIs — required before any large-scale `/all` collection.
12. **`calculateRevenueTTM` silently drops null quarters** — should null the result or flag if any of the 4 quarters is missing revenue.
13. **`MonthlyReport.forecastDate` misnamed** — it's `@CreationTimestamp`, semantically `createdAt`.

---

## Key configuration files

| File                                              | Purpose                                                                           |
|---------------------------------------------------|-----------------------------------------------------------------------------------|
| `src/main/resources/application.yaml`             | DB Dev Services, REST client config, API keys, scheduler crons, Flyway, Hibernate |
| `src/integrationTest/resources/application.yaml`  | Integration-test overrides (WireMock URL, scheduler off, DevServices config)      |
| `src/main/resources/config/currency-mapping.yaml` | Static exchange rates (USD, TWD, CNY, EUR, JPY, GBP)                              |
| `src/main/resources/db/migration/`                | Flyway migrations V1–V8                                                           |
| `build.gradle`                                    | Source sets, `integrationTest` task, all dependencies                             |
| `gradle.properties`                               | Quarkus version (3.30.5)                                                          |
| `work_plan.md`                                    | Target scoring spec                                                               |
| `data_collectedv2.md`                             | Collected metrics and calculation formulas                                        |

---

## Open infrastructure TODOs (not yet decided)

These are operational concerns separate from the bugs list above:

1. **Quarterly scheduler freshness** — `TODO` in `QuarterlyCollectorScheduler`: query stocks where the latest `QuarterlyReport.fiscalDateEnding < 3 months ago`, instead of iterating every ticker every quarter.
2. **Monthly API merge** — comment in `MonthlyReport` about combining YH + AV calls without wasting API quota across monthly + quarterly windows.
3. **TTL-based collection cache** — `work_plan.md` freshness rules (fundamentals 90d / pricing 1d / analyst 30d) are not implemented; collectors always hit APIs.

*(Scoring engine, sector/marketCap pre-filter, and `ScoreBreakdown` are future-phase work — tracked in `work_plan.md`.)*
