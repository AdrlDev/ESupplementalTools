package com.esupplemental.presentation.ui.screens.exercise

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.data.model.StoryActivity
import com.esupplemental.domain.utils.FinalData
import com.esupplemental.domain.utils.StoryLoader
import com.esupplemental.presentation.state.StoryExerciseUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.components.StoryDivider
import com.esupplemental.presentation.ui.components.StorySubmitButton
import com.esupplemental.presentation.ui.theme.*
import com.esupplemental.presentation.viewmodel.StoryExerciseViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

// ── Story Exercise Screen ──────────────────────────────────────────────────

@Composable
fun StoryExerciseScreen(
    mediaId: String,
    viewModel: StoryExerciseViewModel = koinViewModel(),
    onViewResults: (Int, Int, Long) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mediaId) {
        viewModel.loadActivity(mediaId)
    }

    LaunchedEffect(state.audioError) {
        state.audioError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    LaunchedEffect(state.submissionError) {
        state.submissionError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSubmissionError()
        }
    }

    StoryExerciseScreenContent(
        state = state,
        activity = state.activity,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onMoveUp = { from, to -> viewModel.moveEvent(from, to) },
        onMoveDown = { from, to -> viewModel.moveEvent(from, to) },
        onMultipleChoiceChange = { id, choiceIndex -> viewModel.onMultipleChoiceChange(id, choiceIndex) },
        onPlayAudio = { id, text -> viewModel.playItemAudio(id, text) },
        onSubmit = { viewModel.submit() },
        onViewResults = onViewResults
    )
}

// ── Stateless content composable ──────────────────────────────────────────

@Composable
fun StoryExerciseScreenContent(
    state: StoryExerciseUiState,
    activity: StoryActivity?,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onMoveUp: (Int, Int) -> Unit,
    onMoveDown: (Int, Int) -> Unit,
    onMultipleChoiceChange: (String, Int) -> Unit,
    onPlayAudio: (String, String) -> Unit,
    onSubmit: () -> Unit,
    onViewResults: (Int, Int, Long) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val topBarHeight = 64.dp
    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density).toFloat()
    val topBarHeightPx = with(density) { topBarHeight.toPx() } + statusBarHeightPx
    val topBarOffsetPx = remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val newOffset = topBarOffsetPx.floatValue + available.y
                topBarOffsetPx.floatValue = newOffset.coerceIn(-topBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    // Book icon gentle rock animation
    val infiniteTransition = rememberInfiniteTransition(label = "story_anim")
    val bookRock by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "book_rock"
    )

    AppBackground {
        Scaffold(
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .imePadding(),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                // ── Teal gradient top bar ──────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(0, topBarOffsetPx.floatValue.roundToInt()) }
                        .background(
                            Brush.horizontalGradient(listOf(BrandNavy, BrandTealDark))
                        )
                        .statusBarsPadding()
                        .height(topBarHeight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.AutoStories,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier
                                .size(22.dp)
                                .rotate(bookRock)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Story Exercise",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "Read, think, then answer!",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.80f)
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        if (state.loadingAudioIds.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Generating...",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (activity != null) {

                    // ── Section 1: Multiple Choice ─────────────────────────────
                    item {
                        ExerciseSectionHeader(
                            title = "Multiple Choice Quiz",
                            icon = Icons.Rounded.QuestionAnswer,
                            color = colorScheme.onSecondaryContainer
                        )
                    }

                    itemsIndexed(
                        items = activity.multipleChoiceQuestions,
                        key = { _, q -> q.id }
                    ) { idx, q ->
                        MultipleChoiceQuizCard(
                            index = idx + 1,
                            question = q,
                            selectedIndex = state.mcAnswers[q.id],
                            isSubmitted = state.isSubmitted,
                            isInteractionEnabled = !state.isSubmitting,
                            isLoadingAudio = state.loadingAudioIds.contains(q.id),
                            onPlayAudio = { onPlayAudio(q.id, q.question) },
                            onChoiceSelected = { onMultipleChoiceChange(q.id, it) }
                        )
                    }

                    item { StoryDivider() }

                    // ── Section 2: Reordering ──────────────────────────────
                    item {
                        ExerciseSectionHeader(
                            title = "Put Events in Order",
                            icon = Icons.Rounded.FormatListNumbered,
                            color = colorScheme.onSecondaryContainer
                        )
                        Spacer(Modifier.height(8.dp))
                        // Instruction hint card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colorScheme.secondaryContainer)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lightbulb,
                                contentDescription = null,
                                tint = colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Tap ↑ / ↓ to rearrange events in the correct story order.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 18.sp
                                ),
                                color = colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    itemsIndexed(
                        items = state.orderedEvents,
                        key = { _, e -> e.id }
                    ) { idx, event ->
                        ReorderableEventItem(
                            orderNumber = idx + 1,
                            description = event.description,
                            canMoveUp = idx > 0,
                            canMoveDown = idx < state.orderedEvents.lastIndex,
                            isSubmitted = state.isSubmitted,
                            isInteractionEnabled = !state.isSubmitting,
                            isCorrect = state.isSubmitted && event.correctOrder == idx + 1,
                            isLoadingAudio = state.loadingAudioIds.contains(event.id),
                            onPlayAudio = { onPlayAudio(event.id, event.description) },
                            onMoveUp = { onMoveUp(idx, idx - 1) },
                            onMoveDown = { onMoveDown(idx, idx + 1) }
                        )
                    }

                    // ── Submit / Results ───────────────────────────────────
                    item {
                        if (!state.isSubmitted) {
                            StorySubmitButton(
                                onClick = onSubmit,
                                enabled = !state.isSubmitting,
                                isLoading = state.isSubmitting
                            )
                        } else {
                            ScoreBanner(
                                score = state.combinedScore,
                                total = state.combinedTotal,
                                accentColor = BrandTealDark
                            )
                            Spacer(Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = {
                                    state.resultId?.let { id ->
                                        onViewResults(state.combinedScore, state.combinedTotal, id)
                                    }
                                },
                                enabled = state.resultId != null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(18.dp),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = state.resultId != null),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandTealDark)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Assignment,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "View Full Results",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// ── Previews ──────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Story Exercise – Light")
@Composable
private fun StoryExerciseLightPreview() {
    ESupplementalTheme(darkTheme = false) {
        val context = LocalContext.current
        val activity = remember {
            FinalData.storyActivities { StoryLoader.load(context, it) }["t-1"]
        }
        StoryExerciseScreenContent(
            state = StoryExerciseUiState(isSubmitted = false),
            activity = activity,
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onMoveUp = { _, _ -> },
            onMoveDown = { _, _ -> },
            onMultipleChoiceChange = { _, _ -> },
            onPlayAudio = { _, _ -> },
            onSubmit = {},
            onViewResults = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Story Exercise – Dark")
@Composable
private fun StoryExerciseDarkPreview() {
    ESupplementalTheme(darkTheme = true) {
        val context = LocalContext.current
        val activity = remember {
            FinalData.storyActivities { StoryLoader.load(context, it) }["t-1"]
        }
        StoryExerciseScreenContent(
            state = StoryExerciseUiState(isSubmitted = false),
            activity = activity,
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onMoveUp = { _, _ -> },
            onMoveDown = { _, _ -> },
            onMultipleChoiceChange = { _, _ -> },
            onPlayAudio = { _, _ -> },
            onSubmit = {},
            onViewResults = { _, _, _ -> }
        )
    }
}
