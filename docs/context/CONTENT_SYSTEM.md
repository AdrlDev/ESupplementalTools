# Content system

## Primary catalogue

`domain/utils/FinalData.kt` is the active hardcoded media catalogue:

- Songs `s-1`…`s-10`: titles, singers, durations, YouTube thumbnails, and Google Drive sharing URLs.
- Stories `t-1`…`t-10`: titles, story categories, resource thumbnails, Markdown asset filenames, morals, and some source audio URLs.

`MediaRepositoryImpl.seedInitialData()` writes both lists to Room with `REPLACE`, then generates story activities and song challenges from the current Markdown files.

## Story activity generation

`StoryActivityFactory.create(mediaId, title, markdown)`:

1. Splits Markdown into paragraphs and cleans headings, stage directions, broken words, and whitespace.
2. Extracts meaningful sentences and requires at least five candidates.
3. Selects five evenly distributed event descriptions when more exist.
4. Gives events stable IDs using `AudioCacheKey` and one-based `correctOrder`.
5. Creates three multiple-choice questions about event order.
6. Creates no open-ended questions (`openEnded = emptyList()`).

The generated `StoryActivity` is serialized into one Room row per story.

## Game banks

- `WordMasterBank`: dynamic unique words from all stories; removes a small stop-word set and keeps words length 4–12.
- `DisappearingTextBank`: one deterministic 6–12-word sentence per story, with fallback length filtering.
- `TwoTruthsLieBank`: deterministic five rounds from cleaned 6–18-word sentences across at least two stories; two spoken truths and one distractor.
- `MinimalPairsBank`: curated phonetic pairs retained only when both words occur in bundled stories.
- `CharacterQuestBank`: nine static character profiles; quote selection uses story keyword matching and falls back to authored quotes.
- `SpeedTyperBank`: up to five 4–8-word sentences per story, generated with `BreakIterator`.
- `FollowDirectionsBank`: four deterministic grid challenges per story; `DirectionChallengeBank` is a separate older helper and is not used by the active ViewModel.
- `StoryRecallBank`: five questions per story based on first/quarter/half/three-quarter/final sentences plus the story moral.

Bank objects use process-global caches (`ConcurrentHashMap`, `CopyOnWriteArrayList`, or `@Volatile` lists). They initialize once and do not expose an invalidation API.

## Song challenges

Song challenge content is bundled under `app/src/main/assets/songs/questions/` and `answers/`. `SongActivityFactory` parses the numbered fill-in questions and answer keys, then creates one message multiple-choice question from the first message prompt/answer. `MediaRepositoryImpl.seedInitialData()` persists the resulting `SongActivityEntity` records alongside media and story activities.

The active flow is `song_player/{mediaId}` → `song_exercise/{mediaId}`. `PlayerConsole` exposes the challenge button only after playback reports completion. `SongExerciseViewModel` scores the five fill-in answers plus the message answer and saves a `MediaType.SONG` quiz result.

## ID rules

`AudioCacheKey.fromText(prefix, text)` uses a trimmed UTF-8 SHA-256 digest truncated to six bytes. Changing spoken text changes the generated audio ID. Stable prefixes include media/game context, and `MinimalPairsBank.audioId`/`WordMasterBank` intentionally share `dynamic_<word>` IDs for longer words.
