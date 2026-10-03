# Coding conventions observed

- Kotlin packages follow `com.esupplemental.{data|domain|presentation|navigation}`.
- Feature packages are grouped by UI difficulty and game name, generally snake_case for game folders.
- Compose functions are PascalCase; ViewModels/UseCases/Repositories use PascalCase classes; constants are uppercase in companions.
- Stateless `*Content` composables are common and receive state plus callbacks. Screen wrappers commonly obtain Koin ViewModels.
- ViewModels keep mutable state private when using `StateFlow`; public state is read-only.
- `collectAsStateWithLifecycle()` is the preferred newer UI collection style, but legacy `collectAsState()` remains.
- Repository implementations map Room entities through `DataMapper`; JSON/list/enums use Room `TypeConverter` classes.
- Coroutines use `viewModelScope`, `Dispatchers.IO` for asset/duration work, `Flow`, `flatMapLatest`, `combine`, and `runCatching`/`Result`.
- Koin supplies application dependencies, parameterized game ViewModels, and the WorkManager worker.
- Errors are commonly converted to strings with `ErrorMapper` or stored in UI state; some asset-bank failures are logged/ignored so fallback content can continue.
- Media IDs and generated-audio IDs are stable strings. Story IDs are `t-#`, song IDs are `s-#`, and game IDs are lower snake_case.

These are extraction notes, not a mandate to spread the current inconsistencies into new code. New work should match the nearest active feature and preserve its documented behavior.

