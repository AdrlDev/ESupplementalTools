# AI agent guide

## Before changing code

1. Read `PROJECT_OVERVIEW.md` and `ARCHITECTURE.md`.
2. Read the relevant feature document (`GAME_SYSTEM.md`, `AUDIO_MEDIA_PIPELINE.md`, `DATABASE.md`, or `NAVIGATION.md`).
3. Trace the existing screen → ViewModel → use case/repository → DAO/API/asset path.
4. Identify whether the feature writes quiz history, game aggregate progress, notes, or only transient state.
5. Preserve the invariants in `INVARIANTS.md` and check `KNOWN_RISKS.md` before changing persistence/media code.
6. Make the smallest safe change and avoid architecture migration unless explicitly requested.

## When modifying a game

Check the game ID, catalogue order/locking, screen, parameterized ViewModel, bank/content selection, phase transitions, scoring, timers/move limits, feedback delay, audio generation/cache ID, playback cleanup, result persistence, and replay behavior. Confirm the saved total matches the score semantics and the result screen’s displayed total.

## When modifying media/audio

Check the local media row, transcript asset loading, `AudioCacheKey`, `audio_stories` lookup, API base URL/API key, 409/status polling, duration retrieval, Media3 URI resolution, playback-manager release, WorkManager prefetch ordering, network constraints, and visible error/fallback behavior. Remember that current Room audio records are remote URL metadata, not downloaded files.

## When changing database structures

Update the entity, DAO, mapper/converter, repository, Koin wiring, database version, migration chain, and migration tests. Check existing installations and preserve user-scoped records. Do not rely on destructive fallback for production data.

## Validation

Prefer targeted tests for banks, scoring, transcript processing, repository behavior, and ViewModel transitions. A baseline JVM validation command is `./gradlew testDebugUnitTest`. Full device/UI validation is separate and should be run only when the change affects Android runtime behavior.

