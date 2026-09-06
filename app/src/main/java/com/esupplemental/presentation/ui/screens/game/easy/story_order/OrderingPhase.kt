package com.esupplemental.presentation.ui.screens.game.easy.story_order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.esupplemental.data.model.game.StoryEvent
import com.esupplemental.data.model.game.StoryQuestion
import com.esupplemental.presentation.state.game_state.StoryOrderUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.GameFeedbackBanner
import com.esupplemental.presentation.ui.screens.game.GameFeedbackType
import com.esupplemental.presentation.ui.screens.game.GamePrimaryButton
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.StoryOrderPhase
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing

@Composable
fun OrderingPhase(
    state: StoryOrderUiState,
    onReOrderEvents: (Int, Int) -> Unit,
    onCheckOrder: () -> Unit
) {
    val isChecking = state.phase == StoryOrderPhase.CHECKING
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        // --- Header Story Quest Card ---
        Surface(
            color = colorScheme.surfaceVariant.copy(alpha = 0.45f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AsyncImage(
                    model = state.currentStory?.thumbnailUrl ?: state.currentStory?.thumbnailRes,
                    contentDescription = state.currentStory?.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.currentStory?.title.orEmpty(),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                        fontWeight = FontWeight.Black,
                        color = colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isChecking) Icons.Rounded.HourglassTop else Icons.Rounded.TouchApp,
                            contentDescription = null,
                            tint = ArcadeColors.Teal,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isChecking) "Checking your story path…"
                            else "Long-press & drag cards to reorder!",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = ArcadeColors.Teal
                        )
                    }
                }
            }
        }

        // --- Drag-and-Drop Event List ---
        DraggableEventList(
            events = state.userOrderedEvents,
            checkStates = state.cardCheckStates,
            dragEnabled = !isChecking,
            onReorder = onReOrderEvents,
            modifier = Modifier.weight(1f)
        )

        // --- Action Button / Result Banner ---
        AnimatedVisibility(visible = !isChecking) {
            GamePrimaryButton(
                text = "CHECK MY STORY ORDER! 🚀",
                onClick = onCheckOrder,
                icon = Icons.Rounded.CheckCircle,
                accent = ArcadeColors.Teal
            )
        }

        AnimatedVisibility(
            visible = isChecking,
            enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn()
        ) {
            val total = state.currentStory?.events?.size ?: 1
            val correct = state.correctCountThisRound
            val perfect = correct == total

            GameFeedbackBanner(
                title = if (perfect) "Perfect Story Order! 🌟" else "Good Effort! 📖",
                message = if (perfect) {
                    "Awesome! You arranged all $total events in perfect sequence. +XP earned!"
                } else {
                    "You got $correct out of $total events in the right order. Keep practicing!"
                },
                type = if (perfect) GameFeedbackType.SUCCESS else GameFeedbackType.ENCOURAGEMENT
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Ordering: Active Sorting")
@Composable
fun OrderingPhaseActivePreview() {
    val events = (0..4).map { i ->
        StoryEvent(
            id = "s_$i",
            description = "Wilbur was born as the runt of the litter. (Step ${i + 1})",
            correctOrder = i
        )
    }.toMutableList()

    val mockState = StoryOrderUiState(
        phase = StoryOrderPhase.ORDERING,
        currentStory = StoryQuestion(
            id = "",
            title = "Charlotte's Web",
            storyText = "",
            events = emptyList()
        ),
        userOrderedEvents = events,
        cardCheckStates = mapOf()
    )

    ESupplementalTheme {
        AppBackground {
            Box(modifier = Modifier.padding(16.dp)) {
                OrderingPhase(state = mockState, onReOrderEvents = { _, _ -> }, onCheckOrder = {})
            }
        }
    }
}

@Preview(name = "Ordering: Result Summary")
@Composable
fun OrderingPhaseCheckingPreview() {
    val events = (0..4).map { i ->
        StoryEvent(
            id = "s_$i",
            description = "Wilbur was born as the runt of the litter. (Step ${i + 1})",
            correctOrder = i
        )
    }.toMutableList()

    val mockState = StoryOrderUiState(
        phase = StoryOrderPhase.CHECKING,
        currentStory = StoryQuestion(
            id = "",
            title = "Charlotte's Web",
            storyText = "",
            events = emptyList()
        ),
        userOrderedEvents = events,
        correctCountThisRound = 4,
        cardCheckStates = mapOf()
    )

    ESupplementalTheme {
        AppBackground {
            Box(modifier = Modifier.padding(16.dp)) {
                OrderingPhase(state = mockState, onReOrderEvents = { _, _ -> }, onCheckOrder = {})
            }
        }
    }
}
