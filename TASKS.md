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
13. ~~**H1**: Fix unsafe Meteomatics response parsing (PR #114)~~
14. ~~**H2**: Remove dead `LocationDAO` class (PR #115)~~
15. ~~**H3**: Remove unused `RequiresCheckConfiguration` annotation (PR #115)~~
16. ~~**H4**: Simplify coroutine usage in `TemperatureEmitter` (PR #116)~~
17. ~~**H5**: Fix duplicate dependencies in `build.gradle.kts` (PR #113)~~
18. ~~**M2**: Fix `@Requires` defaultValue mismatch on Meteomatics beans (PR #117)~~
19. ~~**M3**: Close InfluxDB client on shutdown (PR #118)~~
20. ~~**M5**: Fix silent DNS resolution failure — log errors in scheduled task (PR #119)~~
21. ~~**L1**: Fix `docs/feateures/` directory typo (PR #120)~~
22. ~~**L3**: Remove redundant `kotlin-stdlib-jdk8` dependency (PR #120)~~
23. ~~**L4**: Fix logger visibility in `RegisterChecksAction` (PR #120)~~
24. ~~**L5**: Tighten CI workflow permissions (PR #120)~~
25. ~~**L6**: Align test provider config with production default (PR #120)~~

## Open

- [ ] **M1: Fix native image config directory naming** — Currently `META-INF/native-image.com.eddgrant.../` (dot-separated). GraalVM expects `META-INF/native-image/<groupId>/<artifactId>/`. Address as part of L7.

- [ ] **L7: Get Docker native image working** — _(Original task #9, carried forward)_

## Won't Fix

- **M4: Remove Micronaut's HTTP server** — Keeping for future /health endpoint.
- **L2: Fix broken markdown link in README.md** — Link was already removed in a previous PR.
