# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`car-manager` is a Spring Boot 3.3 / Java 17 REST service that manages cars and their repair history. It is a
companion service to a separate `brand-manager` service: car-manager validates a car's `brandId` by calling out to
brand-manager over HTTP (via `WebClient`), and on startup registers itself with brand-manager so brand-manager can
notify it of changes. The two services are wired together in `docker-compose.yml` along with one Postgres instance
per service and a shared pgAdmin.

This appears to be a teaching/exercise codebase (Inatel): `Issues.md` documents known functional bugs to be found and
fixed, and at least one existing test (`CarServiceTest`) asserts intentionally wrong values. Don't assume failing
tests or odd logic are accidental regressions to "clean up" — check `Issues.md` and the test intent before changing
behavior that looks buggy.

## Commands

Build tooling: `mvn` and `java` are on `PATH` directly (no need to invoke `./mvnw`; note `mvnw` in this checkout is
not marked executable).

```bash
# Compile
mvn compile

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=CarServiceTest

# Run a single test method
mvn test -Dtest=CarServiceTest#givenCarDTO_whenSaveCarAndBrandIdIsValid_shouldReturnCarDTO

# Package the jar (used by the Dockerfile as target/car-manager-1.0.0.jar)
mvn package

# Run the app locally (defaults to the `dev` profile settings unless SPRING_PROFILES_ACTIVE is set)
mvn spring-boot:run
```

Tests run against an in-memory H2 database (`src/test/resources/application-test.properties`), so `mvn test` needs
no external services. Running the app for real (`dev`/`prod` profiles) requires a reachable Postgres and a reachable
brand-manager instance — see `docker-compose.yml` to spin up the full stack (`bm-postgres`, `cm-postgres`,
`brand-manager`, `car-manager`, `pgadmin`).

Cucumber feature files exist under `src/test/resources/features/` (`CreateGame.feature`, `ReadGame.feature`) and the
`cucumber-jvm`/`cucumber-spring`/`cucumber-java` dependencies are on the classpath, but there is currently no
Cucumber runner or step-definitions class in the repo — these features are not presently wired into `mvn test`.

## Architecture

Standard layered Spring Boot structure under `br.inatel.carmanager`:

- **`controller`** – `CarController` (`/car`, GET/POST) and `BrandController` (`/brandcache`, DELETE to evict the
  brand cache). Controllers depend only on services, never the repository or adapter directly.
- **`service`** – `CarService` holds the car CRUD/validation logic and depends on `BrandManagerService` +
  `CarRepository`. `BrandManagerService` wraps all outbound calls to brand-manager: `getAllBrand()` (cached under the
  `brand` cache name via `@Cacheable`/`@CacheEvict`, cleared through `BrandController`'s DELETE endpoint) and
  `registerOnBrandManager()` (called once at startup).
- **`adapter`** – `BrandManagerAdapter` is a `@Configuration` class exposing the brand-manager base URL and a
  `WebClient` bean, built from `brand.manager.host`/`brand.manager.port` properties. This is the seam to mock/replace
  when testing brand-manager interactions (see `MockWebServer` test dependency).
- **`listener`** – `NotificationListener` implements `ApplicationListener<ApplicationReadyEvent>` and calls
  `BrandManagerService.registerOnBrandManager()` once the app is up. Disabled under the `test` profile
  (`@Profile("!test")`) so tests don't require a live brand-manager.
- **`model.entity`** – JPA entities: `Car` (one-to-many to `Repair`, cascade PERSIST/MERGE, DB-level cascade delete
  via `@OnDelete`) and `Repair` (many-to-one back to `Car`).
- **`model.dto`** – `CarDTO` is the wire representation; note it flattens the `Repair` list into a
  `Map<LocalDate, BigDecimal>` (date → hours), which loses the repair `id` on the way out.
- **`model.rest`** – DTOs for brand-manager's REST contract (`Brand`, `Notification`) plus the generic `Error` shape.
- **`mapper.CarMapper`** – static, stateless conversion between `Car`/`Repair` entities and `CarDTO`
  (entity list ⇄ DTO list, and the `Map<LocalDate, BigDecimal>` ⇄ `List<Repair>` conversion).
- **`repository.CarRepository`** – Spring Data JPA repository; note its generic type is `JpaRepository<Car, String>`
  even though `Car.id` is a `Long`.
- **`filter.RequestFilter`** – a `@WebFilter`/`OncePerRequestFilter` at `@Order(-999)` that wraps every request in
  content-caching wrappers and logs request/response bodies (response logging gated by
  `car-manager.filter.include-response`); excludes `/actuator/**`.
- **`handler.ControllerExceptionHandler`** – single `@ControllerAdvice` mapping each custom/expected exception type
  (`BrandNotFoundException` → 404, `BrandManagerConnectionException` → 503, `JDBCConnectionException` → 503,
  `InvalidFormatException`/`JsonMappingException`/`MethodArgumentNotValidException` → 400, `NullPointerException` →
  500) to a Spring `ProblemDetail` response, optionally including the stack trace based on
  `car-manager.error.show-stacktrace`.
- **`exception`** – `BrandNotFoundException` and `BrandManagerConnectionException`, both plain `RuntimeException`s
  constructed with a formatted message (no custom fields).

### Configuration profiles

- `application.properties` – shared defaults (Jackson `NON_ABSENT`, `ddl-auto=update`, springdoc paths at
  `/api-docs` and `/swagger-ui.html`).
- `application-dev.properties` – localhost Postgres on 5432, brand-manager on localhost:8080, stack traces and
  response logging enabled.
- `application-prod.properties` – everything sourced from env vars (`SERVER_HOST`, `SERVER_PORT`, `DB_HOST`,
  `DB_PORT`, `DB_NAME`, `PUBLISHER_MANAGER_HOST`, `PUBLISHER_MANAGER_PORT`, `LOG_LEVEL`), matching the env blocks in
  `docker-compose.yml`; stack traces disabled.
- `application-test.properties` – H2 in-memory DB, `car-manager.filter.include-response`/`show-stacktrace` both
  enabled for easier debugging; brand-manager port is expected to be supplied at runtime via
  `@DynamicPropertySource` (paired with MockWebServer) since it isn't hardcoded here.

### Cross-service contract

car-manager and brand-manager only talk over HTTP through `BrandManagerAdapter`'s `WebClient`:
- `GET {brand.manager.host}:{brand.manager.port}/brand` → `Brand[]`, used to validate a car's `brandId` before save.
- `POST {brand.manager.host}:{brand.manager.port}/notification` (body: this service's own host/port) → `Notification[]`,
  called once on startup so brand-manager knows where to reach car-manager back.

Any change to `Brand`/`Notification` shapes must stay compatible with whatever `brand-manager` actually serves —
that service's source isn't in this repo.
