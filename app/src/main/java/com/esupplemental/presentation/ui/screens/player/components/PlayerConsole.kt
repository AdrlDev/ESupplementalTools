package com.esupplemental.presentation.ui.screens.player.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.presentation.state.PlayerUiState
import com.esupplemental.presentation.ui.screens.player.PlaybackControls
import com.esupplemental.presentation.ui.screens.player.StartChallengeButton
import com.esupplemental.presentation.ui.theme.elevations
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.domain.model.media.StoryChunk
import com.esupplemental.domain.model.media.StoryPage

@Composable
fun PlayerConsole(
    state: PlayerUiState,
    item: MediaItem,
    showTranscript: Boolean,
    accentColor: Color,
    surfaceColor: Color,
    onPlayPause: () -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekMs: (Long) -> Unit,
    onStartExercise: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing

    Surface(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp),
        color = surfaceColor,
        shadowElevation = MaterialTheme.elevations.large
    ) {
        AnimatedContent(
            targetState = showTranscript,
            transitionSpec = {
                (slideInVertically { it } + fadeIn()) togetherWith
                        (slideOutVertically { it } + fadeOut())
            },
            label = "console_transition"
        ) { isTranscriptVisible ->
            if (isTranscriptVisible) {
                Column(modifier = Modifier.padding(spacing.large)) {
                    StoryBookView(
                        state = state,
                        item = item,
                        accentColor = accentColor,
                        onSeek = onSeek,
                        onSeekMs = onSeekMs,
                        modifier = Modifier.weight(1f)
                    )
                    if (state.isPlaybackFinished && onStartExercise != null) {
                        StartChallengeButton(accentColor) { onStartExercise(item.id) }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(spacing.large),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlaybackControls(
                        state = state,
                        accentColor = accentColor,
                        onPlayPause = onPlayPause,
                        onSkipForward = onSkipForward,
                        onSkipBackward = onSkipBackward,
                        onSeek = onSeek
                    )
                    Spacer(Modifier.weight(1f))
                    if (state.isPlaybackFinished && onStartExercise != null) {
                        StartChallengeButton(accentColor) { onStartExercise(item.id) }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Player Console - Playback",
    group = "Player Console",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun PlayerConsolePlaybackPreview() {
    MaterialTheme {
        PlayerConsole(
            state = PlayerUiState(
                currentPositionSeconds = 45,
                isPlaying = true,
                isPlaybackFinished = false
            ),
            item = MediaItem(
                id = "t-1",
                title = "The Notes of Note-Re Dame",
                type = MediaType.STORY,
                durationSeconds = 280,
                thumbnailUrl = null
            ),
            showTranscript = false,
            accentColor = MaterialTheme.colorScheme.primary,
            surfaceColor = MaterialTheme.colorScheme.surface,
            onPlayPause = {},
            onSkipForward = {},
            onSkipBackward = {},
            onSeek = {},
            onSeekMs = {},
            onStartExercise = {}
        )
    }
}

@Preview(
    name = "Player Console - Story Book",
    group = "Player Console",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun PlayerConsoleStoryBookPreview() {
    MaterialTheme {
        PlayerConsole(
            state = PlayerUiState(
                currentPositionSeconds = 30,
                isPlaying = true,
                currentSentenceIndex = 2,
                isPlaybackFinished = false,
                transcriptPages = listOf(
                    StoryPage(0, listOf(
                        StoryChunk(0, "Solo was a lonely little note who wished for a more exciting life.", 0, 11),
                        StoryChunk(1, "Every evening, he listened to the grand cathedral bells.", 12, 19),
                        StoryChunk(2, "He wondered where music might take him.", 20, 26),
                        StoryChunk(3, "Then one bright morning, a gentle melody floated through the window.", 27, 37)
                    )),
                    StoryPage(1, listOf(
                        StoryChunk(4, "Solo followed the melody through the ancient halls.", 38, 45),
                        StoryChunk(5, "Tiny golden notes danced above him.", 46, 51),
                        StoryChunk(6, "The quiet corridor became a river of sound.", 52, 59),
                        StoryChunk(7, "For the first time, Solo realized he did not have to sing alone.", 60, 72)
                    ))
                ),
                transcriptChunks = listOf(
                    StoryChunk(0, "Solo was a lonely little note who wished for a more exciting life.", 0, 11),
                    StoryChunk(1, "Every evening, he listened to the grand cathedral bells.", 12, 19),
                    StoryChunk(2, "He wondered where music might take him.", 20, 26),
                    StoryChunk(3, "Then one bright morning, a gentle melody floated through the window.", 27, 37),
                    StoryChunk(4, "Solo followed the melody through the ancient halls.", 38, 45),
                    StoryChunk(5, "Tiny golden notes danced above him.", 46, 51),
                    StoryChunk(6, "The quiet corridor became a river of sound.", 52, 59),
                    StoryChunk(7, "For the first time, Solo realized he did not have to sing alone.", 60, 72)
                )
            ),
            item = MediaItem(
                id = "t-1",
                title = "The Notes of Note-Re Dame",
                type = MediaType.STORY,
                durationSeconds = 280,
                thumbnailUrl = null
            ),
            showTranscript = true,
            accentColor = MaterialTheme.colorScheme.primary,
            surfaceColor = MaterialTheme.colorScheme.surface,
            onPlayPause = {},
            onSkipForward = {},
            onSkipBackward = {},
            onSeek = {},
            onSeekMs = {},
            onStartExercise = {}
        )
    }
}

@Preview(
    name = "Player Console - Story Finished",
    group = "Player Console",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun PlayerConsoleFinishedPreview() {
    MaterialTheme {
        PlayerConsole(
            state = PlayerUiState(
                currentPositionSeconds = 280,
                isPlaying = false,
                currentSentenceIndex = 3,
                isPlaybackFinished = true,
                transcriptPages = listOf(
                    StoryPage(0, listOf(
                        StoryChunk(0, "Solo discovered that music was everywhere.", 0, 5),
                        StoryChunk(1, "Every note had its own special voice.", 6, 12),
                        StoryChunk(2, "Together, the notes created something beautiful.", 13, 18),
                        StoryChunk(3, "And Solo never felt alone again.", 19, 24)
                    ))
                ),
                transcriptChunks = listOf(
                    StoryChunk(0, "Solo discovered that music was everywhere.", 0, 5),
                    StoryChunk(1, "Every note had its own special voice.", 6, 12),
                    StoryChunk(2, "Together, the notes created something beautiful.", 13, 18),
                    StoryChunk(3, "And Solo never felt alone again.", 19, 24)
                )
            ),
            item = MediaItem(
                id = "t-1",
                title = "The Notes of Note-Re Dame",
                type = MediaType.STORY,
                durationSeconds = 280,
                thumbnailUrl = null
            ),
            showTranscript = true,
            accentColor = MaterialTheme.colorScheme.primary,
            surfaceColor = MaterialTheme.colorScheme.surface,
            onPlayPause = {},
            onSkipForward = {},
            onSkipBackward = {},
            onSeek = {},
            onSeekMs = {},
            onStartExercise = {}
        )
    }
}
