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

## Known TODOs (current phase — collection layer)

1. **Rate limiting** — `TODO` in `YhFinanceApiClient`; also needed for Alpha Vantage.
2. **Quarterly scheduler freshness** — `TODO` in `QuarterlyCollectorScheduler`: should query stocks where `updatedAt < 3 months` instead of iterating all tickers.
3. **Monthly API merge** — comment in `MonthlyReport` about combining YH + AV calls without wasting quota.
4. **TTL-based collection cache** — collectors always hit APIs; the `work_plan.md` freshness rules (90d / 1d / 30d) are not yet implemented.

*(Scoring engine, sector/marketCap filtering, and `ScoreBreakdown` are future-phase work — tracked in `work_plan.md`.)*
