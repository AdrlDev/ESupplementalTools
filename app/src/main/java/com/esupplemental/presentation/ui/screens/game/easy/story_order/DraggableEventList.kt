package com.esupplemental.presentation.ui.screens.game.easy.story_order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.esupplemental.data.model.game.StoryEvent
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.CardCheckState
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing
import kotlin.math.roundToInt

@Composable
fun DraggableEventList(
    events: List<StoryEvent>,
    checkStates: Map<String, CardCheckState>,
    dragEnabled: Boolean,
    onReorder: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val itemHeights = remember { mutableStateMapOf<Int, Int>() }

    var draggingId by remember { mutableStateOf<String?>(null) }
    var draggingOffsetY by remember { mutableFloatStateOf(0f) }
    var dragStartIndex by remember { mutableIntStateOf(-1) }

    fun targetIndex(): Int {
        if (dragStartIndex < 0) return -1
        val avgH = if (itemHeights.isNotEmpty()) itemHeights.values.average().toFloat() else 1f
        val moved = (draggingOffsetY / avgH).roundToInt()
        return (dragStartIndex + moved).coerceIn(0, events.size - 1)
    }

    // Glass Container Styling
    val baseContentColor = colorScheme.onSurface
    val glassBgTop = baseContentColor.copy(alpha = 0.05f)
    val glassBgBottom = baseContentColor.copy(alpha = 0.01f)
    val glassBorderTop = baseContentColor.copy(alpha = 0.15f)
    val glassBorderBottom = baseContentColor.copy(alpha = 0.03f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(glassBgTop, glassBgBottom)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(glassBorderTop, glassBorderBottom)
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(spacing.medium)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            events.forEachIndexed { index, event ->
                val isDragging = draggingId == event.id
                val isTarget = !isDragging && index == targetIndex() && dragStartIndex >= 0
                val checkState = checkStates[event.id] ?: CardCheckState.IDLE

                EventCard(
                    event = event,
                    position = index + 1,
                    isDragging = isDragging,
                    isDropTarget = isTarget,
                    dragOffsetY = if (isDragging) draggingOffsetY else 0f,
                    checkState = checkState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { itemHeights[index] = it.height }
                        .zIndex(if (isDragging) 10f else 0f)
                        .then(
                            if (dragEnabled) {
                                Modifier.pointerInput(event.id) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            dragStartIndex = events.indexOfFirst { it.id == event.id }
                                            draggingId = event.id
                                            draggingOffsetY = 0f
                                        },
                                        onDrag = { change, amount ->
                                            change.consume()
                                            draggingOffsetY += amount.y
                                        },
                                        onDragEnd = {
                                            val from = dragStartIndex
                                            val to = targetIndex()
                                            if (from >= 0 && from != to) onReorder(from, to)
                                            draggingId = null
                                            draggingOffsetY = 0f
                                            dragStartIndex = -1
                                        },
                                        onDragCancel = {
                                            draggingId = null
                                            draggingOffsetY = 0f
                                            dragStartIndex = -1
                                        }
                                    )
                                }
                            } else Modifier
                        )
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Draggable List - Playing State")
@Composable
fun DraggableEventListPreview() {
    val mockEvents = listOf(
        StoryEvent("s1", "Wilbur was born as the runt of the litter.", 1),
        StoryEvent("s2", "Fern saved Wilbur from her father's axe.", 2),
        StoryEvent("s3", "Wilbur moved to Zuckerman's barn.", 3),
        StoryEvent("s4", "Charlotte spun the first word in her web.", 4),
        StoryEvent("s5", "Wilbur became a famous pig at the fair.", 5)
    )
    ESupplementalTheme {
        AppBackground {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                DraggableEventList(
                    events = mockEvents,
                    checkStates = mockEvents.associate { it.id to CardCheckState.IDLE },
                    dragEnabled = true,
                    onReorder = { _, _ -> }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Draggable List - Checked State")
@Composable
fun DraggableEventListCheckedPreview() {
    val mockEvents = listOf(
        StoryEvent("s1", "Correct Event One", 1),
        StoryEvent("s2", "Wrong Event Two", 2),
        StoryEvent("s3", "Idle Event Three", 3)
    )
    ESupplementalTheme {
        AppBackground {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                DraggableEventList(
                    events = mockEvents,
                    checkStates = mapOf(
                        "s1" to CardCheckState.CORRECT,
                        "s2" to CardCheckState.WRONG,
                        "s3" to CardCheckState.IDLE
                    ),
                    dragEnabled = false,
                    onReorder = { _, _ -> }
                )
            }
        }
    }
}