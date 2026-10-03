# Invariants for future changes

- The source code and current runtime behavior outrank README/comments when they disagree.
- `FinalData` story IDs, song IDs, asset filenames, and game IDs are stable integration keys; changing them affects Room rows, navigation, and audio IDs.
- `StoryActivityFactory` requires at least five meaningful narrative candidates per story asset.
- Story event `correctOrder` is one-based; game and exercise scoring compare it to `index + 1`.
- A game score must remain within its saved total; `QuizRepository` explicitly requires `0 <= score <= total` and `total > 0`.
- Quiz history and recalculated user stats must be saved through `QuizDao.saveQuizResultAndUpdateStats` so the insert and stats update remain transactional.
- Game progress is user-scoped by composite key `(userId, gameId)` and updates preserve the maximum stars/XP already earned.
- Game tier progression is part of the current behavior: Easy → Moderate → Hard, with sequential completion within each tier, unless the build flag explicitly bypasses it.
- Generated audio IDs must be reused consistently between gameplay and prefetch. If spoken text changes, its hash-based cache ID should change too.
- `audio_stories` currently caches remote metadata/URLs, not local bytes; do not claim offline audio unless a real file cache is added.
- Audio playback must be released with the owning ViewModel; ExoPlayer and Visualizer are lifecycle resources.
- Story transcript processing must preserve word ordering/ranges used by playback highlighting and Story Order captions.
- Initial media seeding is intended to be repeatable: media/activity inserts use `REPLACE`; user data/progress must not be cleared by content refresh.
- Existing Room installations must not lose local accounts, notes, quiz history, game progress, or cached audio metadata during schema changes.
- Note reads and writes must remain user-isolated; do not bypass repository/DAO ownership from Compose UI.

