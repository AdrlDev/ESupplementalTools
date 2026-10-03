# Architecture

The project is a pragmatic hybrid rather than a strict Clean Architecture implementation.

```text
Compose screen / reusable composable
        ↓
ViewModel or direct screen-owned UI state
        ↓
Use case (where one exists)
        ↓
Repository interface + implementation
        ↓
Room DAO / bundled assets / Retrofit audio API / Media3 manager
        ↓
Flow or Result
        ↓
ViewModel state
        ↓
Compose recomposition
```

## Actual boundaries

- `presentation/ui`: Compose screens and reusable UI. Game screens often render directly from a feature-specific state data class.
- `presentation/viewmodel`: screen/business orchestration. Most screens expose immutable `StateFlow`; `AuthViewModel` still uses Compose `mutableStateOf`.
- `presentation/state`: UI state models, including game phase enums and answer state.
- `domain/usecases`: thin application operations such as auth, media lookup, quiz save, game catalogue sync, transcript processing, and audio prefetch orchestration.
- `domain/utils`: content banks, asset parsing, cache-key generation, scoring, preferences, error mapping, and other mixed domain helpers.
- `data/local/repository`: repository contracts and Room-backed implementations. The package name is local-data-specific even where the interface is used as a domain boundary.
- `data/local/dao` and `data/local/entity`: Room persistence.
- `data/remote`: Retrofit contract and API DTOs for generated audio.
- `domain/manager` + `data/local/manager`: playback abstraction and Media3 implementation.
- `domain/worker`: WorkManager worker that invokes the prefetch use case.

## Dependency injection

Koin is configured in `domain/di/module.kt`. Room, DAOs, repositories, preferences, API, and most use cases are singletons/factories. ViewModels are declared with Koin. Game ViewModels receive `(gameId, mediaId?)` parameters. `MediaPlaybackManager` is a factory, so each injected ViewModel receives its own manager/player instance.

## Architectural observations

- MVVM and repository/use-case patterns are real and dominant.
- State is mostly unidirectional (`ViewModel → immutable state → UI → callbacks`), but there is no common sealed action/event contract.
- Game ViewModels own substantial game rules and call content banks and repositories directly.
- `StoryExerciseViewModel` saves through `QuizRepository`; game ViewModels save aggregate catalogue progress through `GameRepository`.
- `FakeData` remains legacy/demo data. Song-activity models and bundled question/answer assets are active through `SongActivityFactory`, media seeding, and the song challenge route.
