# Navigation

`MainActivity` chooses `home` or `login` after local session validation. `NavGraph.kt` owns the active `NavHost`; bottom navigation is shown only for Home, Progress, Library, and Profile.

```text
Login
 ├── Register
 └── Home
     ├── media_list/song → song_player/{mediaId}
     │                         └── song_exercise/{mediaId}
     ├── media_list/story → story_player/{mediaId}
     │                         └── story_exercise/{mediaId}
     │                              └── quiz_result/{score}/{total}/{resultId}/{mediaId}
     ├── note_list → note_taking/{noteId}
     └── game → game_player/{gameId}?mediaId={mediaId}
                         ├── listen_slap → ListenSlapScreen
                         ├── story_order → StoryOrderScreen
                         ├── character_quest → CharacterQuestScreen
                         ├── disappearing_text → DisappearingTextScreen
                         ├── two_truths_lie → TwoTruthsLieScreen
                         ├── minimal_pairs → MinimalPairsScreen
                         ├── speed_typer → SpeedTyperScreen
                         ├── follow_directions → FollowDirectionsScreen
                         └── story_recall → StoryRecallScreen

Home / Progress / Library / Profile
 ├── general_settings
 └── profile_settings
```

## Route ownership

- `home`: `HomeScreen` + `HomeViewModel`.
- `progress`: `ProgressScreen` + `ProgressViewModel`.
- `library`: `LibraryScreen` + `LibraryViewModel` and shared `NoteViewModel`.
- `profile`: profile UI; settings screens use settings/auth ViewModels.
- `song_player`/`story_player`: `PlayerScreen` + `PlayerViewModel`; both expose `onStartExercise` after playback finishes.
- `song_exercise`: `SongExerciseScreen` + `SongExerciseViewModel`.
- `story_exercise`: `StoryExerciseScreen` + `StoryExerciseViewModel`.
- `quiz_result`: `QuizResultScreen` reloads persisted result by ID through `QuizResultViewModel`.
- `note_list`/`note_taking`: `NoteViewModel` is obtained once in `AppNavGraph` and passed to both destinations.
- `game_player`: route dispatches by `GameId.fromIdOrDefault`; parameterized Koin ViewModels receive game ID and optional media ID.

There are no declared deep links.
