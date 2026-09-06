# Start Challenge — Playful Story Quiz Design

## Goal
Replace the current post-story `Start Challenge` experience with a playful, kid-friendly multiple-choice quiz.

After the learner finishes listening to a story:
1. Tap `START CHALLENGE`.
2. Show one comprehension question at a time.
3. Show exactly four selectable answers: A, B, C, D.
4. Give immediate animated feedback.
5. Move through all questions.
6. Show the final score and encouragement.
7. Persist progress and results locally.

## Visual Style
Match the existing ESupplemental Jetpack Compose + Material 3 theme and `ArcadeColors`.

The question should look like a floating/pop bubble:
- Large rounded card
- Soft elevation/shadow
- Playful typography
- Small decorative bubbles
- Theme accent colors
- Clean enough to remain readable

Example:
```text
        ○

   ┌─────────────────────┐
   │     QUESTION 1      │
   │                     │
   │ What did the mouse  │
   │ do to help the lion?│
   └─────────────────────┘

   A  Answer one
   B  Answer two
   C  Answer three
   D  Answer four

          1 / 5
```

## Answer States
Normal:
- Neutral Material surface
- Clear letter badge
- Large touch target

Pressed:
- Small scale-down animation

Correct:
- Green success treatment
- Check icon
- Small scale-up/bubble celebration

Incorrect:
- Red treatment
- X icon
- Short horizontal shake
- Correct answer becomes highlighted

Do not allow multiple selections for one question.

## Question Animation
Use Compose animations such as `AnimatedContent`, `AnimatedVisibility`, `fadeIn`, `fadeOut`, `slideInVertically`, `slideOutVertically`, and `spring()`.

Enter:
- Scale about 0.85f -> 1f
- Fade in
- Slight vertical movement

Exit:
- Slight scale/fade
- Slide to the next question

Keep animations lightweight.

## Progress
Show both text and a visual indicator:
`Question 2 of 5`

Animate the progress indicator when changing questions.

## Scoring
- Correct = +1
- Incorrect = +0
- No negative scoring

Track:
- Current question
- Score
- Answered questions
- Total questions
- Completion state

## Recommended Model
```kotlin
data class MultipleChoiceQuestion(
    val id: String,
    val question: String,
    val choices: List<String>,
    val correctAnswerIndex: Int
)
```

Validate that there are exactly four choices and the correct index is 0..3.

## State
The ViewModel should own challenge state, not only the Composable:
```kotlin
data class ChallengeState(
    val questions: List<MultipleChoiceQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val score: Int = 0,
    val answeredQuestions: Int = 0,
    val isAnswerRevealed: Boolean = false,
    val isFinished: Boolean = false
)
```

## Persistence
Use the project's existing persistence solution. Prefer existing Room if already appropriate; otherwise use DataStore for lightweight progress.

Persist at least:
- mediaId
- current question index
- score
- answered question count
- completion state
- final score
- completion timestamp

If resume requires it, persist selected answers too.

Do not keep challenge progress only in `remember`.

## Resume
If the learner exits before finishing:
`Continue Challenge?`
`Question 3 of 5`
`Score: 2`
`[CONTINUE] [START OVER]`

If already completed:
`Challenge Complete`
`Score: 4 / 5`
`[TRY AGAIN] [BACK TO STORY]`

## Final Screen
Make the result rewarding:
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

Suggested feedback:
- 100%: `Fantastic! You remembered the story perfectly!`
- 80–99%: `Great job! You remembered almost everything!`
- 60–79%: `Nice work! You remembered many important details!`
- Below 60%: `Good try! Listen to the story again and give it another shot!`

Keep feedback encouraging.

## Architecture
UI:
- Rendering
- Animations
- Answer buttons
- Progress
- Result

ViewModel:
- Load challenge
- Select answer
- Calculate score
- Advance
- Save/restore progress
- Complete/retry

Repository:
- Load StoryActivity
- Persist challenge progress

Do not duplicate business logic in Composables.

## Accessibility
- Large touch targets
- Good contrast in light/dark mode
- Do not communicate correctness through color alone
- Use icons/text as well
- Meaningful content descriptions
- Animations must not be essential to understanding the result

## Acceptance Criteria
- Start Challenge opens the quiz
- Four answers per question
- One correct answer
- Animated question transitions
- Animated answer feedback
- Correct/incorrect feedback
- No multiple selection
- Progress indicator
- Correct scoring
- Final result
- Persistent progress
- Resume incomplete challenge
- Retry completed challenge
- Works in light and dark mode
- Does not break existing player/story functionality
