# Tasks

## Completed

1. ~~Fix TemperatureEmitterSpec~~
2. ~~Fix capitalisation of measurement (should be "Temperature" and tags, should be "postcode").~~
3. ~~Set up an automated CI/CD process using GitHub actions~~
4. ~~CI/CD Setup https://github.com/marketplace/actions/git-semantic-version for versioning.~~
5. ~~CI/CD Push Docker image.~~
6. ~~Publish JUnit test results.~~
7. ~~Set up PR actions.~~
8. ~~Write a README for Dockerhub. Can this be pulled automatically from somewhere?~~
9. ~~Make sure the GitHub repo gets tagged each time we publish a new release number.~~
10. ~~Add a "source" property to measurements, for easy identification (do this in the same way that lan2rf-gateway-stats does it)~~
11. ~~Add a "provider" property, to identify which provider provided the temperature data.~~
12. ~~Rebase and merge stale Renovate PRs (#74, #93, #94, #95, #96, #103, #104, #105, #106, #107, #110, #111)~~

## Open — High Priority

- [ ] **H1: Fix unsafe Meteomatics response parsing** — `MeteomaticsWeatherClient` uses a chain of unchecked casts to navigate deeply nested JSON. A malformed response produces opaque `ClassCastException`/`NullPointerException`. Add safe extraction with meaningful error messages. _File: `MeteomaticsWeatherClient.kt`_

- [ ] **H2: Remove dead `LocationDAO` class** — Contains only `TODO()` which throws `NotImplementedError` at runtime. The class is never used anywhere. _File: `LocationDAO.kt`_

- [ ] **H3: Remove or re-enable `RequiresCheckConfiguration`** — The custom annotation exists but is commented out on all three classes that should use it (`CheckTemperatureTask`, `TemperatureEmitter`, `RegisterChecksAction`). Either apply it or delete it. _Files: `RequiresCheckConfiguration.kt`, `CheckTemperatureTask.kt`, `TemperatureEmitter.kt`, `RegisterChecksAction.kt`_

- [ ] **H4: Simplify coroutine usage in `TemperatureEmitter`** — `runBlocking { launch { ... }; job.join() }` is equivalent to calling the suspend function directly in `runBlocking`. Remove the unnecessary `launch`/`join`. _File: `TemperatureEmitter.kt`_

- [ ] **H5: Fix duplicate dependencies in `build.gradle.kts`** — `mn.micronaut.serde.jackson` is declared twice. `mn.micronaut.serde.processor` appears as both `ksp` and `annotationProcessor`. `mn.micronaut.http.client` appears in both `implementation` and `testImplementation`. _File: `build.gradle.kts`_

## Open — Medium Priority

- [ ] **M1: Fix native image config directory naming** — Currently `META-INF/native-image.com.eddgrant.../` (dot-separated). GraalVM expects `META-INF/native-image/<groupId>/<artifactId>/`. The current naming may not be picked up. _Directory: `src/main/resources/META-INF/`_

- [ ] **M2: Fix `@Requires` defaultValue mismatch on `MeteomaticsWeatherClient`** — The `defaultValue = "meteomatics"` conflicts with `application.yml` default of `weatherapi.com`. Remove the `defaultValue` to prevent both beans loading when the property is absent. Also applies to `MeteomaticsConfiguration`. _Files: `MeteomaticsWeatherClient.kt`, `MeteomaticsConfiguration.kt`_

- [ ] **M3: Close InfluxDB client on shutdown** — `InfluxDBClientKotlin` is created via `@Factory` but never closed. Add a `@PreDestroy` or use `@Bean(preDestroy = "close")` to avoid connection leaks. _File: `InfluxDBClientFactory.kt`_

- [ ] **M4: Evaluate removing Micronaut's HTTP server** — The app is a scheduled task runner, not an HTTP service. The embedded Netty server adds startup time and attack surface. Consider if it's worth keeping for health checks, or replace with a lighter approach. _(Original task #11)_

- [ ] **M5: Fix silent DNS resolution failure** — The app silently does nothing when unable to resolve DNS names in Docker internal networks. Investigate and add proper error handling/logging. _(Original task #12)_

## Open — Low Priority (batch into single PR)

- [ ] **L1: Fix `docs/feateures/` directory typo** — Rename to `docs/features/`.

- [ ] **L2: Fix broken markdown link in README.md** — Line 7: `[https://hub.docker.com/r/eddgrant/influxdb-weather-ingestor)` should use `(` not `[`.

- [ ] **L3: Remove redundant `kotlin-stdlib-jdk8` dependency** — Since Kotlin 1.8+, the stdlib includes JDK8 extensions. _File: `build.gradle.kts`_

- [ ] **L4: Fix logger visibility** — `CheckTemperatureTask.LOGGER` and `RegisterChecksAction.LOGGER` should be `private val`. _Files: `CheckTemperatureTask.kt`, `RegisterChecksAction.kt`_

- [ ] **L5: Tighten CI workflow permissions** — Remove `contents: write` and `actions: read` from `unit-tests.yaml` and `integration-tests.yaml` where not needed.

- [ ] **L6: Align test provider config with production default** — `application-test.yml` uses `weather.provider: meteomatics` but production defaults to `weatherapi.com`. Consider aligning or documenting the rationale.

- [ ] **L7: Get Docker native image working** — _(Original task #9, carried forward)_
