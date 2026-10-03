# Known risks

Findings are evidence-based and intentionally not fixed in this context-building pass.

### [HIGH] Missing migration path for database version 19

Location: `data/local/AppDatabase.kt`, `domain/di/module.kt`.

Reason: `@Database(version = 19)` exposes migrations only through 17→18. Koin additionally enables `fallbackToDestructiveMigration(true)`.

Impact: an existing installation upgrading from schema 18 can be destructively recreated, losing local users, notes, progress, games, and generated-audio metadata.

Recommendation: add and test the required 18→19 migration, enable schema export, and remove destructive fallback for production once migration coverage is complete.

### [HIGH] Staging/demo Retrofit base URL lacks a trailing slash

Location: `data/remote/AudioStoryApi.kt`.

Reason: the bypass branch returns `https://aeservertesting.aesprt.com` while Retrofit requires a base URL ending in `/`.

Impact: staging/demo can fail during Retrofit construction at app startup, before audio features are usable.

Recommendation: make both branches valid trailing-slash base URLs and add a variant startup/configuration check.

### [HIGH] “Cache” is not an offline audio cache

Location: `data/local/entity/AudioStoryEntity.kt`, `MediaRepositoryImpl.generateAudioForText`, `MediaPlaybackManagerImpl`.

Reason: Room stores remote URL/metadata only; no audio bytes or local file destination are persisted.

Impact: generated audio avoids API regeneration but playback still fails without network access or when the remote URL expires/becomes unavailable.

Recommendation: if offline playback is a requirement, add a controlled file-download/cache layer and lifecycle-safe local URI resolution.

### [MEDIUM] Game order/progression depends on an unordered DAO query

Location: `data/local/dao/GameDao.kt`, `GameScreenViewModel`.

Reason: `getAllGames` has no `ORDER BY`, while the ViewModel locks each game based on the previous list item.

Impact: SQLite row order is not a durable catalogue-order contract; unlock sequencing can be inconsistent after migrations or database changes.

Recommendation: persist/display an explicit order or query in `GameCatalogue` order deterministically.

### [MEDIUM] Existing game catalogue metadata is not refreshed

Location: `GameRepositoryImpl.refreshCatalogue`, `GameDao.insertGames`.

Reason: catalogue sync uses `OnConflictStrategy.IGNORE`.

Impact: title, description, difficulty, icon, or XP changes in code do not reach existing users, while progress is preserved.

Recommendation: separate immutable progress from catalogue metadata or use a transaction that updates metadata while preserving progress.

### [MEDIUM] Note lookup is not user-scoped

Location: `NoteDao.getNoteById`, `NoteRepositoryImpl`, `NoteViewModel.loadNote`.

Reason: lookup filters only by note ID; list/delete include user ID.

Impact: a known note ID could load another user’s note into the editor.

Recommendation: require `(noteId, userId)` for reads and preserve the user boundary end-to-end.

### [LOW] Song challenge parser assumes numbered Markdown sections

Location: `domain/utils/SongActivityFactory.kt`, `assets/songs/questions`, `assets/songs/answers`.

Reason: bundled song question and answer files are parsed by their numbered fill-in and message sections.

Impact: future content files with a different structure will not seed a usable challenge.

Recommendation: preserve the section headings and numbering when adding song content, or add parser tests before changing the asset format.

### [MEDIUM] Speed Typer result total does not match saved score semantics

Location: `presentation/ui/screens/game/hard/speed_typer/SpeedTyperScreen.kt`, `SpeedTyperViewModel`.

Reason: the ViewModel scores one point per correct round across four rounds, but the result screen is passed `state.totalRounds * 25` as the displayed total.

Impact: a perfect four-round game can display `4 / 100` even though the persisted game result uses four as its total.

Recommendation: use one score contract for the ViewModel, save path, and result UI; add a ViewModel/UI test for a perfect run.

### [LOW] Mixed lifecycle collection and state conventions

Location: `HomeScreen.kt`, `MediaListScreen.kt`, `AuthViewModel.kt`, several newer screens.

Reason: both `collectAsState()` and lifecycle-aware collection exist; auth uses Compose mutable state while most ViewModels use StateFlow.

Impact: maintenance and lifecycle behavior vary by screen.

Recommendation: standardize incrementally when a screen is already being changed.

### [CLEANUP] Legacy/dead content helpers and stale comments

Location: `data/model/FakeData.kt`, `domain/utils/DirectionChallengeBank.kt`, song activity assets/models, comments mentioning TTS.

Reason: active code uses `FinalData`, `FollowDirectionsBank`, and Media3/API audio instead.

Impact: future agents may follow obsolete paths or duplicate functionality.

Recommendation: confirm product intent, then remove/archive or explicitly mark legacy code in a separate cleanup change.
