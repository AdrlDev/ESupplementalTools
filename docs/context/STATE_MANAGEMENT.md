# State management

## Dominant convention

Most screens use a private `MutableStateFlow` and expose `asStateFlow()`:

```kotlin
private val _uiState = MutableStateFlow(ScreenUiState())
val uiState: StateFlow<ScreenUiState> = _uiState.asStateFlow()
```

ViewModels collect DataStore/Room/repository flows in `viewModelScope`, use `update { ... }`, and Compose screens collect with `collectAsStateWithLifecycle()`.

## Variations

- `AuthViewModel` uses Compose `mutableStateOf` for auth/form state rather than `StateFlow`.
- `HomeScreen` and `MediaListScreen` use plain `collectAsState()` instead of lifecycle-aware collection.
- Transient UI state such as search visibility, drag state, dialog visibility, slider dragging, and animation flags uses `remember { mutableStateOf(...) }`.
- No shared event channel or sealed action system is used. Callbacks invoke ViewModel functions directly.
- Game state is a single immutable data class plus a feature-specific phase enum. Timers/audio transitions use `Job`, `delay`, and playback-manager flow collectors.

## Side effects

- `LaunchedEffect` loads media/notes, drives splash/session checks, and triggers delayed UI animation.
- ViewModels launch database, audio, timer, and save work in `viewModelScope`.
- `MediaPlaybackManagerImpl` owns a `SupervisorJob` and releases ExoPlayer/Visualizer in lifecycle cleanup.

## Review notes

- The architecture generally has one ViewModel source of truth per screen.
- Process-global content-bank caches are outside ViewModel state and are not invalidated when assets/configuration change.
- Several state models carry nullable/legacy collections (`List<QuizHistoryEntity?>?`) because DAO signatures are overly nullable.
- Long-lived game feedback jobs are usually cancelled on clear; not every delayed transition has a dedicated session guard.

