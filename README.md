# E-Supplemental Tools
### Grade 6 Listening Comprehension App — Jetpack Compose + MVVM

---

## 📁 Project Structure

```
com.esupplemental/
├── MainActivity.kt                  ← App entry; Scaffold + BottomNav
│
├── navigation/
│   ├── Screen.kt                    ← Sealed class route definitions
│   └── NavGraph.kt                  ← NavHost with all composable destinations
│
├── data/model/
│   └── Models.kt                    ← All data classes + FakeData repository
│
├── viewmodel/
│   ├── HomeViewModel.kt             ← User stats, activity list state
│   ├── PlayerViewModel.kt           ← Playback state (play/pause/seek/skip)
│   ├── QuizViewModel.kt             ← SongExerciseViewModel + StoryExerciseViewModel
│   ├── NoteViewModel.kt             ← Rich-text note state + keyword chips
│   └── ProgressViewModel.kt        ← Dashboard stats state
│
├── ui/
│   ├── theme/
│   │   ├── Color.kt                 ← DeepPurple / Teal / Orange palettes
│   │   ├── Type.kt                  ← Typography scale
│   │   └── Theme.kt                 ← MaterialTheme (light + dark)
│   │
│   ├── components/
│   │   └── CommonComponents.kt      ← CircularProgressRing, ActivityCard,
│   │                                   StatCard, MediaListItem, KeywordChip
│   │
│   └── screens/
│       ├── HomeScreen.kt            ← Greeting + activity cards + progress
│       ├── PlayerScreen.kt          ← Media list (searchable) + full player UI
│       ├── ExerciseScreen.kt        ← SongExerciseScreen + StoryExerciseScreen
│       ├── NoteScreen.kt            ← NoteListScreen + NoteEditorScreen
│       ├── ProgressScreen.kt        ← Dashboard + QuizResultScreen + ProfileScreen
│       └── LibraryScreen.kt        ← Tabbed library (Songs / Stories / Notes)
```

---

## 🏗️ Architecture

```
UI (Compose) ──► ViewModel (StateFlow) ──► Repository (FakeData)
     ▲                   │
     └── collectAsState()┘
```

- **MVVM** with `StateFlow` / `collectAsState()`
- Each screen has a dedicated `UiState` data class
- Navigation uses `NavHost` + `NavController`; bottom nav tabs save/restore state
- `ViewModel` instances are scoped to the nav graph via `viewModel()`

---

## 🎨 Theme Palette

| Context       | Colors                          |
|---------------|---------------------------------|
| Home / Songs  | DeepPurple 700 → 500            |
| Stories       | Teal 700 → Green 600            |
| Note-Taking   | VibrantOrange 700 → 500         |
| All cards     | 24 dp+ corner radius            |

---

## 📱 Screens

| Screen                | Route                              |
|-----------------------|------------------------------------|
| Home                  | `home`                             |
| Progress Dashboard    | `progress`                         |
| Library               | `library`                          |
| Profile               | `profile`                          |
| Song List             | `media_list/song`                  |
| Story List            | `media_list/story`                 |
| Song Player           | `song_player/{mediaId}`            |
| Story Player          | `story_player/{mediaId}`           |
| Song Exercise         | `song_exercise/{mediaId}`          |
| Story Exercise        | `story_exercise/{mediaId}`         |
| Note Editor           | `note_taking/{noteId}`             |
| Quiz Result           | `quiz_result/{score}/{total}`      |

---

## 🔧 Setup

1. Open in **Android Studio Meerkat** (2024.3+)
2. Sync Gradle
3. Run on emulator / device with **API 26+**

### Key Dependencies
```
androidx.navigation:navigation-compose:2.8.9
androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0
androidx.compose.material:material-icons-extended
kotlinx-coroutines-android:1.10.2
Compose BOM: 2025.05.00
```

---

## ✅ Features Checklist

- [x] **Home** — greeting, avatar, progress card, activity cards
- [x] **Media Player** — searchable list, album art, play/pause, skip ±10s, seek slider, transcript tab
- [x] **Song Exercise** — fill-in-the-blanks + multiple-choice with live scoring
- [x] **Story Exercise** — reorderable events (↑/↓) + open-ended questions
- [x] **Note-Taking** — B/I/U toggles, Main Idea / Key Details / Summary sections, keyword chips
- [x] **Progress Dashboard** — ring chart (85%), stat cards, level progress bar
- [x] **Quiz Result** — celebration animation, trophy icon, score breakdown, review
- [x] **Library** — tabbed (Songs / Stories / Notes) with navigation
- [x] **Profile** — user info + settings menu
- [x] **Bottom Nav** — Home / Progress / Library / Profile with state restoration
- [x] **Smooth transitions** — slide + fade between screens
- [x] **Dark theme** — full M3 dark color scheme support
