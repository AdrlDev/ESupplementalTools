# E-Supplemental Tools — Project Overview

Audit basis: current source in `app/src/main`, tests in `app/src/test`, and Gradle configuration as of 2026-09-13. The source code is authoritative; the older root README describes an earlier architecture.

## Purpose

E-Supplemental Tools is a native Android listening-comprehension and educational practice app. It provides locally stored learner accounts, story/song playback, structured story exercises, notes, progress, and nine listening-oriented games.

## Runtime shape

- One Android application module: `app` (`com.esupplemental`).
- Kotlin/JVM 17, min SDK 26, target SDK 36, compile SDK 37.1.
- Kotlin 2.4.10, Jetpack Compose with Compose BOM 2026.08.00, Material 3.
- MVVM-like presentation layer with `StateFlow`/Compose state, repositories, use cases, Room, and Koin.
- Room database `esupplemental.db`, schema version 19.
- Retrofit 3 + Gson + OkHttp for the remote audio-story generation/status API.
- Media3 ExoPlayer for playback and Android `Visualizer` for waveform data.
- DataStore Preferences for session, theme, dark-mode, and sound-effect settings.
- WorkManager for one-time background audio prefetch.

## Main user areas

1. Local registration/login and DataStore session restoration.
2. Home dashboard with user stats and song/story shortcuts.
3. Songs and stories library/player.
4. Story exercise: reorder events plus multiple choice, saved as quiz history.
5. Notes with title, main idea, key details, summary, and keywords.
6. Progress dashboard with stats, quiz history, and game catalogue progress.
7. Games grouped into Easy, Moderate, and Hard tiers.
8. Profile/settings for local profile and appearance preferences.

## Content at a glance

- `FinalData` defines 10 songs (`s-1`…`s-10`) and 10 stories (`t-1`…`t-10`).
- Ten story Markdown assets are bundled at `app/src/main/assets/`.
- Ten song lyric assets plus question/answer Markdown files are bundled under `app/src/main/assets/songs/`, but no active song-exercise route or seed path currently consumes them.
- Story activities are generated from story Markdown at seed time by `StoryActivityFactory`.
- Game banks derive content from story assets, curated pairs, or static character profiles; see `GAME_SYSTEM.md` and `CONTENT_SYSTEM.md`.

## Important entry points

- `app/src/main/java/com/esupplemental/MyApp.kt`: starts Koin and registers WorkManager integration.
- `app/src/main/java/com/esupplemental/presentation/MainActivity.kt`: theme, splash, initial media seed, prefetch enqueue, session gate, and app scaffold.
- `app/src/main/java/com/esupplemental/navigation/NavGraph.kt`: all active routes and destination wiring.
- `app/src/main/java/com/esupplemental/domain/di/module.kt`: Room, Retrofit, repositories, use cases, ViewModels, and worker bindings.

