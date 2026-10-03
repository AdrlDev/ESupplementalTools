# Poem Feature

## Overview

Poems are a third `MediaType` and follow the existing Story player pipeline. The initial content is imported from `Downloads/Poem.docx`; the source contains 16 usable entries, including two distinct versions of “The Little Seed”.

## Architecture

Poems are stored as `MediaItem` rows and use the existing `MediaRepository`, `PlayerViewModel`, `PlayerScreen`, `MediaPlaybackManager`, Room audio metadata cache, and `StoryExerciseScreen`. Poem exercise rows reuse the existing `story_activities` table, so this feature does not require a database schema change.

## Poem Content Flow

`Home` or `Library` → `media_list/poem` → `poem_player/{mediaId}` → `poem_exercise/{mediaId}` → shared quiz result screen.

## Audio Generation Flow

`PlayerViewModel` loads only the poem Markdown transcript, then calls `MediaRepository.generateAudioForText(mediaId, title, transcript)`. Quiz headings, options, and answer keys are not sent to the audio API. Generated audio metadata is cached in `audio_stories` using the existing media ID cache key.

## Transcript Flow

The poem asset is loaded through `StoryLoader`, cleaned by `ProcessTranscriptUseCase`, and rendered by the existing story-book transcript UI. Blank lines in the source asset remain stanza separators in the asset and are not included in the exercise section sent to playback.

## Exercise Flow

`PoemActivityFactory` parses the supplied five multiple-choice and five True/False items. True/False items are represented as a two-choice `MultipleChoiceQuestion` (`True`, `False`) so the existing selection, submission, scoring, result, and quiz-history behavior is reused. Poem activities have no story-reordering section.

## Markdown Format

Each asset uses one combined file:

```markdown
# A New Poem

Author: Example Author

## Poem
First line,
second line.

New stanza.

## Multiple Choice
### 1. What is the poem about?
- A. One answer
- B. The correct answer
- C. Another answer
- D. A final answer
Answer: B

## True or False
### 1. The poem has a message.
Answer: True
```

## Adding a New Poem

1. Add a Markdown file under `app/src/main/assets/poems/` using the format above.
2. Add a `MediaItem` to `FinalData.poems` with a unique `p-` ID and the asset path.
3. Keep the transcript under `## Poem`; keep all exercise material under the exercise headings.
4. Add five supplied multiple-choice questions and five supplied True/False questions, preserving the source answer key.
5. Run `./gradlew testDebugUnitTest`.

## API Integration

The existing Retrofit audio-story endpoint and repository polling/cache logic are used unchanged. A poem’s title and transcript body are the only content passed to audio generation.

## File Locations

- Catalogue: `app/src/main/java/com/esupplemental/domain/utils/FinalData.kt`
- Parser: `app/src/main/java/com/esupplemental/domain/utils/PoemActivityFactory.kt`
- Assets: `app/src/main/assets/poems/`
- Shared player: `presentation/viewmodel/PlayerViewModel.kt` and `presentation/ui/screens/player/`
- Shared exercise: `presentation/viewmodel/StoryExerciseViewModel.kt` and `presentation/ui/screens/exercise/StoryExerciseScreen.kt`
- Routes: `app/src/main/java/com/esupplemental/navigation/Screen.kt` and `NavGraph.kt`

## Data Models

Poems use `MediaType.POEM` and the existing `MediaItem`, `StoryActivity`, `StoryActivityEntity`, `MultipleChoiceQuestion`, and `QuizResult` models. Poem quiz results are saved as `POEM` history rows and count toward the existing story-style completion statistic.

## Navigation

Poem list, player, and exercise routes are separate names for clear navigation while their screen implementations remain shared with Stories.

## Error Handling

Audio generation, unavailable assets, malformed exercise content, and playback failures surface through the same loading/error states and retry behavior used for Stories. A malformed poem is rejected during activity seeding rather than crashing the player flow silently.

## Testing

`PoemActivityFactoryTest` verifies that multiple-choice and True/False Markdown are parsed into the shared activity model. The baseline project suite is `./gradlew testDebugUnitTest`.
