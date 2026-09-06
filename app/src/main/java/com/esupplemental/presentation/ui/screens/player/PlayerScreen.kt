package com.esupplemental.presentation.ui.screens.player

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.domain.utils.NetworkUtils
import com.esupplemental.presentation.state.PlayerUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.player.components.*
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.viewmodel.PlayerViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.Alignment
import com.esupplemental.data.remote.model.StoryAudioStatus
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline

@Composable
fun PlayerScreen(
    mediaId: String,
    viewModel: PlayerViewModel = koinViewModel(),
    onStartExercise: ((String) -> Unit)?,
    onBack: () -> Unit
) {
    val context =
        LocalContext.current.applicationContext

    val state by
    viewModel.uiState
        .collectAsStateWithLifecycle()

    var showNoInternetDialog by
    remember {
        mutableStateOf(false)
    }

    LaunchedEffect(mediaId) {
        viewModel.loadMedia(
            context,
            mediaId
        )
    }

    /*
     * ==========================================================
     * ERROR STATE
     * ==========================================================
     */
    if (
        state.error != null ||
        state.storyAudioStatus ==
        StoryAudioStatus.FAILED
    ) {
        PlayerErrorState(
            message =
                state.error
                    ?: "Unable to prepare this story.",
            onBack = onBack
        )

        return
    }

    /*
     * ==========================================================
     * LOADING / PROCESSING
     * ==========================================================
     */
    if (
        state.isLoadingMedia ||
        state.storyAudioStatus ==
        StoryAudioStatus.PROCESSING ||
        state.mediaItem == null
    ) {
        PlayerLoadingState(
            isPreparingStory =
                state.storyAudioStatus ==
                        StoryAudioStatus.PROCESSING
        )

        return
    }

    val item =
        state.mediaItem
            ?: return

    LaunchedEffect(item.id) {

        if (
            item.type == MediaType.SONG &&
            !NetworkUtils.isInternetAvailable(
                context
            )
        ) {
            showNoInternetDialog = true
        }
    }

    if (showNoInternetDialog) {
        AlertDialog(
            onDismissRequest = {
                showNoInternetDialog = false
                onBack()
            },
            title = {
                Text(
                    "No Internet Connection"
                )
            },
            text = {
                Text(
                    "An internet connection is required to play this song."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNoInternetDialog =
                            false

                        onBack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    val isSong =
        item.type == MediaType.SONG

    val colors =
        MaterialTheme.colorScheme

    val accentColor =
        if (isSong) {
            colors.primary
        } else {
            colors.secondary
        }

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        if (
            (
                    item.thumbnailRes != null &&
                            item.thumbnailRes != 0
                    ) ||
            !item.thumbnailUrl.isNullOrBlank()
        ) {

            Box(
                Modifier.fillMaxSize()
            ) {

                if (
                    !item.thumbnailUrl
                        .isNullOrBlank()
                ) {

                    AsyncImage(
                        model =
                            item.thumbnailUrl,
                        contentDescription =
                            null,
                        contentScale =
                            ContentScale.Crop,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .blur(3.dp)
                    )

                } else if (
                    item.thumbnailRes != null
                ) {

                    Image(
                        painter =
                            painterResource(
                                item.thumbnailRes
                            ),
                        contentDescription =
                            null,
                        contentScale =
                            ContentScale.Crop,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .blur(8.dp)
                    )
                }

                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(
                                alpha = 0.45f
                            )
                        )
                )
            }
        }

        AppBackground(
            topColor =
                if (
                    item.thumbnailRes != null &&
                    item.thumbnailRes != 0
                ) {
                    Color.Transparent
                } else {
                    accentColor.copy(
                        alpha = 0.8f
                    )
                }
        ) {

            PlayerScreenContent(
                state = state,
                item = item,
                accentColor =
                    accentColor,
                surfaceColor =
                    colors.surface,
                isSong = isSong,
                onBack = onBack,
                onPlayPause = {
                    viewModel.togglePlayPause()
                },
                onSkipForward = {
                    viewModel.skipForward()
                },
                onSkipBackward = {
                    viewModel.skipBackward()
                },
                onSeek = {
                    viewModel.seekTo(it)
                },
                onSeekMs = {
                    viewModel.seekToMs(it)
                },
                onStartExercise =
                    onStartExercise
            )
        }
    }
}

@Composable
fun PlayerScreenContent(
    state: PlayerUiState,
    item: MediaItem,
    accentColor: Color,
    surfaceColor: Color,
    isSong: Boolean,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekMs: (Long) -> Unit,
    onStartExercise: ((String) -> Unit)?
) {
    var showTranscript by remember { mutableStateOf(false) }
    val spacing = MaterialTheme.spacing

    Column(modifier = Modifier.fillMaxSize()) {
        val title = item.singer ?: item.category?.name ?: item.title
        PlayerTopBar(
            title = title,
            onBack = onBack,
            accentColor = MaterialTheme.colorScheme.background
        )

        AnimatedVisibility(
            visible = !showTranscript,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                MediaStage(
                    item = item,
                    isPlaying = state.isPlaying,
                    amplitudes = state.amplitudes,
                    accentColor = accentColor,
                    isSong = isSong
                )
                MediaHeader(item = item)
            }
        }

        if (!isSong) {
            TranscriptToggle(
                showTranscript = showTranscript,
                onToggle = { showTranscript = !showTranscript },
                color = accentColor
            )
        }

        Spacer(Modifier.height(spacing.extraLarge))

        PlayerConsole(
            state = state,
            item = item,
            showTranscript = showTranscript,
            accentColor = accentColor,
            surfaceColor = surfaceColor,
            onPlayPause = onPlayPause,
            onSkipForward = onSkipForward,
            onSkipBackward = onSkipBackward,
            onSeek = onSeek,
            onSeekMs = onSeekMs,
            onStartExercise = onStartExercise
        )
    }
}

@Composable
fun PlayerLoadingState(
    isPreparingStory: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AppBackground {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = if (isPreparingStory) "Preparing your story..." else "Loading media...",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (isPreparingStory) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Preparing narration and word timing",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerErrorState(
    message: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AppBackground {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Unable to Load",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Go Back")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Player – Song")
@Composable
private fun PlayerSongPreview() {
    ESupplementalTheme {
        AppBackground {
            val state = PlayerUiState(
                mediaItem = MediaItem(
                    id = "song_1", title = "The Honest Boy",
                    category = com.esupplemental.data.model.StoryCategory.KINDNESS, type = MediaType.SONG,
                    durationSeconds = 180, transcript = "Sample lyrics…"
                ),
                isPlaying = true, currentPositionSeconds = 45
            )

            PlayerScreenContent(
                state = state,
                item = state.mediaItem!!,
                accentColor = MaterialTheme.colorScheme.primary,
                surfaceColor = MaterialTheme.colorScheme.surface,
                isSong = false,
                onBack = {}, onPlayPause = {}, onSkipForward = {},
                onSkipBackward = {}, onSeek = {}, onSeekMs = {}, onStartExercise = {}
            )
        }
    }
}
