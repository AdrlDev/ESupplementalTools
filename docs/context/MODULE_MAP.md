# Module and package map

There is one Gradle module and one production source set. No `debug`, `staging`, or `demo` Kotlin source directories are present; build variants are configured in Gradle only.

```text
app/
├── src/main/assets/                 Bundled story/song Markdown
├── src/main/res/                    Images, fonts, themes, strings
├── src/main/java/com/esupplemental/
│   ├── MyApp.kt                     Application/Koin/WorkManager startup
│   ├── data/
│   │   ├── converter/               Room Gson/enum converters
│   │   ├── local/
│   │   │   ├── AppDatabase.kt       Room schema and migrations
│   │   │   ├── dao/                 Room queries/transactions
│   │   │   ├── entity/              Persisted records
│   │   │   ├── manager/             Media3 playback implementation
│   │   │   └── repository/           Contracts and implementations
│   │   ├── mapper/                  Entity/domain/JSON mapping
│   │   ├── model/                   Media, quiz, note, user, game models
│   │   └── remote/                  Audio generation API and DTOs
│   ├── domain/
│   │   ├── di/                     Koin module
│   │   ├── manager/                Playback interface
│   │   ├── model/                  Game/story playback models
│   │   ├── usecases/               Application operations
│   │   ├── utils/                  Banks, asset parsing, preferences, helpers
│   │   └── worker/                 AudioPrefetchWorker
│   ├── navigation/                Screen routes and NavHost
│   └── presentation/
│       ├── state/                  Screen and game UI state
│       ├── ui/components/          Shared app components
│       ├── ui/screens/              App, player, exercise, note, profile, game UI
│       └── viewmodel/              Screen/game ViewModels
└── src/test/                       Five JVM test classes
```

## Build configuration

`app/build.gradle.kts` defines `debug`, `staging`, `demo`, and `release`. Staging/demo add an application suffix and set `ALLOW_GAME_PROGRESSION_BYPASS=true`; release minifies/shrinks resources. The API key is read from root `local.properties` property `appApiKey` and copied into `BuildConfig.APP_API_KEY`.

