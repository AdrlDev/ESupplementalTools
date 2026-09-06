# Prompt — Implement the Playful Story Start Challenge

You are modifying the existing ESupplemental Android application.

## Objective
Upgrade the current `Start Challenge` flow after a learner finishes listening to a story.

Turn it into a playful story-comprehension game using four-choice multiple-choice questions.

The experience should feel like a fun educational game, not a traditional exam.

## First: Inspect the Existing Project
Before coding, inspect:
- `StoryActivity`
- `StartChallengeButton`
- `PlayerScreen`
- `PlayerViewModel`
- `PlayerUiState`
- Story/question models
- Navigation
- Existing Room/DataStore/repository code
- Existing score, XP, stars, or progress systems
- `StoryLoader`
- `MediaItem`

Search all usages before changing models such as `openEnded`.

Do not create duplicate architecture or unnecessary dependencies.

## Desired Flow
```text
Player
  -> Story finished
  -> START CHALLENGE
  -> Question 1
  -> A/B/C/D
  -> Animated feedback
  -> NEXT
  -> Question 2
  -> ...
  -> Final Score
  -> Save result
```

## Question Model
Create or adapt:
```kotlin
data class MultipleChoiceQuestion(
    val id: String,
    val question: String,
    val choices: List<String>,
    val correctAnswerIndex: Int
)
```

Requirements:
- Exactly 4 choices
- Correct index 0..3
- Validate question data
- Questions come from `StoryActivity`, never from UI hardcoding

Prefer:
```kotlin
StoryActivity(
    mediaId = "t-2",
    reorderEvents = ...,
    multipleChoiceQuestions = listOf(
        MultipleChoiceQuestion(
            id = "s2_q1",
            question = "...",
            choices = listOf("...", "...", "...", "..."),
            correctAnswerIndex = 1
        )
    )
)
```

If `openEnded` is used elsewhere, preserve compatibility where practical.

## UI
Use Jetpack Compose + Material 3 and the existing ESupplemental theme.

Create a playful floating question bubble:
- Rounded corners
- Soft elevation
- Playful typography
- Decorative small bubbles
- Existing `MaterialTheme.colorScheme`
- Existing `ArcadeColors`
- Good light/dark mode support

Answer buttons:
```text
A  Answer
B  Answer
C  Answer
D  Answer
```

Use large touch targets and make the entire answer clickable.

## Animations
Use:
- `AnimatedContent`
- `AnimatedVisibility`
- `fadeIn`
- `fadeOut`
- `slideInVertically`
- `slideOutVertically`
- `spring`
- `animateFloatAsState` where appropriate

Question enter:
- scale 0.85f -> 1f
- fade in
- slight vertical movement

Answer pressed:
- subtle scale-down

Correct:
- green success state
- check icon
- subtle scale-up/bubble celebration

Wrong:
- red state
- X icon
- short horizontal shake
- correct answer highlighted

Do not use excessive animations or cause frame drops.

## Answer Rules
After selection:
- Lock the question against additional answers.
- Reveal whether it is correct.
- If wrong, show the correct answer.
- Show `NEXT` after feedback.
- Do not subtract points.

Scoring:
```text
correct = +1
wrong = +0
```

## Progress
Show:
`Question 2 of 5`

Also show an animated progress indicator.

## ViewModel
Keep challenge state in the ViewModel:
```kotlin
data class ChallengeUiState(
    val questions: List<MultipleChoiceQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val score: Int = 0,
    val answeredQuestions: Int = 0,
    val isAnswerRevealed: Boolean = false,
    val isFinished: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

Implement appropriate actions such as:
```kotlin
loadChallenge(mediaId)
selectAnswer(index)
nextQuestion()
restartChallenge()
completeChallenge()
```

Use immutable state updates.

## Persistence
Inspect the existing persistence architecture first.

If Room already exists and is appropriate, use it.
If lightweight storage is more appropriate, use DataStore.

Persist:
- mediaId
- current question index
- score
- answered questions
- completion state
- final score
- completion timestamp
- selected answers if required for resume

Do not store challenge progress only in Compose `remember`.

## Resume
When leaving an incomplete challenge, save state.

When reopening:
```text
Continue Challenge?

Question 3 of 5
Score: 2

[CONTINUE]
[START OVER]
```

For completed challenges:
```text
Challenge Complete
Score: 4 / 5
80%

[TRY AGAIN]
[BACK TO STORY]
```

A new attempt should reset the current attempt without deleting historical data unless the existing architecture does not track history.

If the app already has XP/stars/progression, integrate with it rather than creating a second system.

## Final Result
Create a rewarding result screen:
```text
       🎉
   STORY MASTER!
       4 / 5
        80%
    ★ ★ ★ ★ ☆
You remembered most of the story!

     [TRY AGAIN]
        [DONE]
```

Feedback:
- 100%: `Fantastic! You remembered the story perfectly!`
- 80–99%: `Great job! You remembered almost everything!`
- 60–79%: `Nice work! You remembered many important details!`
- <60%: `Good try! Listen to the story again and give it another shot!`

## Markdown Integration
Stories are stored as Markdown assets such as:
```text
stories/black_beauty.md
stories/in_memory_of_frankie.md
stories/motorboat_miracle.md
```

Keep Markdown loading in `StoryLoader`/repository. Do not load story files repeatedly from the Composable.

The challenge should use structured `StoryActivity` questions.

## Navigation
Use the existing navigation system:
```text
PlayerScreen
 -> Start Challenge
 -> Story Challenge
 -> Challenge Result
```

Back navigation must work.

Save incomplete progress when leaving.

## Performance
Avoid:
- Heavy infinite animations
- Expensive work in recomposition
- Database calls directly in Composables
- Repeated Markdown loading
- Unnecessary dependencies

Use `remember`, `LaunchedEffect`, and `derivedStateOf` appropriately.

## Accessibility
- Large touch targets
- Good contrast
- Correctness not communicated by color alone
- Icons/text for feedback
- Content descriptions for meaningful icons
- Readable in light/dark mode
- Animations are supplementary, not required for comprehension

## Implementation Order
1. Inspect architecture.
2. Update question model.
3. Add multiple-choice questions to story data.
4. Implement ViewModel state/actions.
5. Implement persistence.
6. Build challenge UI.
7. Add question transitions.
8. Add answer feedback animations.
9. Add progress.
10. Add result screen.
11. Add resume/retry.
12. Connect Start Challenge.
13. Test light/dark mode.
14. Test navigation and process recreation.
15. Test scoring/persistence.

## Acceptance Criteria
- [ ] Start Challenge opens quiz
- [ ] Questions load for selected story
- [ ] Exactly 4 choices
- [ ] Exactly 1 correct choice
- [ ] Answer selection is locked after selection
- [ ] Correct answer feedback is animated
- [ ] Wrong answer feedback is animated
- [ ] Correct answer is revealed after a wrong choice
- [ ] Question transitions are animated
- [ ] Progress is visible
- [ ] Score is correct
- [ ] Final result is displayed
- [ ] Incomplete progress is saved
- [ ] Challenge can be resumed
- [ ] Completed challenge can be retried
- [ ] Light mode works
- [ ] Dark mode works
- [ ] Existing player/story functionality still works
- [ ] No unnecessary duplicate architecture
- [ ] No challenge state is hardcoded in Composables

## Final Response
After implementation, summarize:
1. Files changed
2. Model changes
3. ViewModel changes
4. Persistence implementation
5. UI/animation implementation
6. Navigation changes
7. Migration required
8. Remaining TODOs

Do not merely describe code you could write. Inspect the existing implementation and make the changes directly while preserving existing architecture.
