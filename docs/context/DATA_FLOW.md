# Major data flows

## Startup and session

```text
MainActivity.onCreate
 → ESupplementalTheme
 → LaunchedEffect(Unit)
 → SyncInitialMediaUseCase()
      → MediaRepository.seedInitialData()
           → insert FinalData media
           → generate StoryActivityFactory activities from story assets
           → generate SongActivityFactory challenges from song question/answer assets
 → enqueue unique AudioPrefetchWorker (network connected, KEEP)
 → splash progress animation
 → read DataStore session
 → AuthRepository.getCurrentUser(userId)
 → Home or Login start destination
```

The stored token is a locally generated UUID. Session validation checks the local user ID; the token is not sent to a backend.

## Home

`HomeViewModel` observes `UserPreferences.userId`, then `GetHomeDataUseCase`, which combines `HomeRepository.getUserStats`, songs, and stories. Room flows update `HomeUiState`.

## Media list/player

`MediaListScreen`/`LibraryScreen` → `LibraryViewModel` or `PlayerViewModel` → `GetMediaItemsUseCase` → `MediaRepository` → Room `media_items`. `PlayerViewModel.loadMedia` resolves a story Markdown asset, processes transcript chunks/pages, generates or retrieves story audio, then prepares `MediaPlaybackManager`. Playback flows update `PlayerUiState`.

## Story exercise

`StoryPlayer` → `StoryExerciseScreen` → `StoryExerciseViewModel.loadActivity` → Room `story_activities`. User reorder and multiple-choice selections remain in UI state. `submit()` scores event positions and MC choices, builds `QuizResult`, then calls `QuizRepository.saveQuizResult` → Room transaction inserts `quiz_history` and recalculates `user_stats`. Result navigation passes score/total/result ID/media ID to `QuizResultScreen`.

## Song challenge

`SongPlayer` → completed playback → `SongExerciseScreen` → `SongExerciseViewModel.loadActivity` → Room `song_activities`. The ViewModel scores fill-in answers and the message choice, then saves a `MediaType.SONG` `QuizResult` through the same quiz-history transaction.

## Games

`GameScreen` → `GameScreenViewModel` → `GetGamesUseCase`/`GameRepository` → Room `games`. A selected game navigates to the single `game_player/{gameId}` route, which dispatches to a game screen and parameterized ViewModel. The ViewModel owns the round state, uses a bank and/or `MediaRepository.generateAudioForText`, and saves final aggregate stars/XP through `SaveGameResultUseCase` → `GameRepository` → Room `games`.

## Notes

`NoteViewModel` observes the current DataStore user ID → `NoteRepository.getNotes` → Room `notes`. Editor callbacks mutate `NoteUiState`; save/delete calls are user-scoped in the DAO.

## Progress

`ProgressViewModel` independently observes user-scoped stats, quiz history, latest quiz, and game catalogue flows, then merges them into one `ProgressUiState`.
