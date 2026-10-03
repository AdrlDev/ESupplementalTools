# AGENTS.md

## Project Overview

This is a native Android application written primarily in Kotlin.

## Technology Stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM
- ViewModel
- StateFlow
- Kotlin Coroutines
- Koin
- Room
- Retrofit
- OkHttp
- Gradle Kotlin DSL

## Architecture

Follow MVVM and existing project architecture.

Typical dependency direction:

UI / Compose
↓
ViewModel
↓
Use Case / Repository
↓
Data Source
↓
Room / Retrofit

Do not introduce a new architecture unless explicitly requested.

## Jetpack Compose Rules

- Prefer stateless composables where practical.
- Hoist state to the appropriate owner.
- Use ViewModel for screen/business state.
- Use StateFlow for observable ViewModel state.
- Collect lifecycle-aware state with `collectAsStateWithLifecycle()`.
- Avoid performing side effects directly during composition.
- Use `LaunchedEffect`, `DisposableEffect`, or other appropriate APIs for side effects.
- Avoid unnecessary recompositions.
- Do not pass ViewModel instances deep into reusable UI components.
- Keep composables focused and reasonably small.

## Kotlin Rules

- Prefer idiomatic Kotlin.
- Prefer immutable data structures/state where practical.
- Avoid `!!` unless absolutely necessary.
- Handle nullable values explicitly.
- Prefer sealed interfaces/classes for UI state where appropriate.
- Use coroutines for asynchronous work.
- Do not use `GlobalScope`.
- Use structured concurrency.

## ViewModel Rules

- ViewModels must not hold references to Activity, Fragment, View, or Compose UI objects.
- Use `viewModelScope` for ViewModel coroutines.
- Expose immutable StateFlow to the UI.
- Keep mutable state private.

Example:

```kotlin
private val _uiState = MutableStateFlow(ScreenUiState())
val uiState: StateFlow<ScreenUiState> = _uiState.asStateFlow()
```
# UI/UX Design Guidelines

## Design Role

When working on UI, act as both a senior Android UI engineer and a
product UI/UX designer.

Do not treat UI tasks as merely placing components on a screen.

Consider:
- visual hierarchy
- spacing
- typography
- color
- accessibility
- interaction design
- usability
- responsive layouts
- loading states
- empty states
- error states
- animations
- user feedback
- consistency with the rest of the application

Before implementing a new screen, inspect existing screens and reusable
components so the result feels like part of the same product.

---

## Design Philosophy

Prefer interfaces that are:

- modern
- clean
- minimal
- polished
- intuitive
- accessible
- content-focused
- visually balanced

Avoid unnecessarily complicated interfaces.

Use whitespace intentionally.

Do not fill every available area with cards, borders, text, or controls.

Every visible element should have a clear purpose.

---

## Visual Hierarchy

Every screen should clearly communicate:

1. What screen the user is viewing.
2. What information is most important.
3. What action the user should take next.

Use hierarchy through:

- typography
- spacing
- alignment
- size
- weight
- color
- grouping

Do not rely on color alone to communicate hierarchy or state.

Primary actions must be visually distinguishable from secondary actions.

---

## Material Design

Follow Material 3 principles while preserving the application's own
visual identity.

Use the project's MaterialTheme whenever possible.

Prefer:

```kotlin
MaterialTheme.colorScheme
MaterialTheme.typography
MaterialTheme.shapes

## Repository context

Before making feature changes, read `docs/context/PROJECT_OVERVIEW.md`, `docs/context/ARCHITECTURE.md`, and the relevant feature document. The context documents describe the current implementation, including known legacy paths and risks; source code remains the final authority.

- Preserve current games, scoring, timers, progress, media IDs, audio behavior, navigation, and Room data unless the task explicitly changes them.
- Trace UI → ViewModel → use case/repository → DAO/API/assets before editing.
- Do not bypass repositories/use cases from Compose UI or create duplicate game/content logic.
- Treat `audio_stories` as remote metadata/URL cache, not local offline audio.
- Database changes require a migration review and must preserve existing user data.
- Run targeted validation after modifications; the baseline JVM command is `./gradlew testDebugUnitTest`.
