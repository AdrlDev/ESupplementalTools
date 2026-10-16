package com.esupplemental.presentation.ui.screens.player.components

import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem as Media3Item
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.esupplemental.domain.utils.DriveAudioManager

@Composable
fun PodcastVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    onPlaybackFinished: () -> Unit
) {
    val context = LocalContext.current
    var isFullscreen by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(true) }
    val player = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(Media3Item.fromUri(DriveAudioManager.getDirectStreamUrl(videoUrl) ?: videoUrl))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(player) {
        isBuffering = player.playbackState != Player.STATE_READY && player.playbackState != Player.STATE_ENDED
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING || playbackState == Player.STATE_IDLE
                if (playbackState == Player.STATE_ENDED && !isFinished) {
                    isFinished = true
                    onPlaybackFinished()
                }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                isBuffering = isLoading
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                isBuffering = false
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    VideoPlayerSurface(
        player = player,
        isBuffering = isBuffering,
        fullscreen = isFullscreen,
        onEnterFullscreen = { isFullscreen = true },
        onExitFullscreen = { isFullscreen = false },
        modifier = modifier
    )
}

@Composable
private fun VideoPlayerSurface(
    player: ExoPlayer,
    isBuffering: Boolean,
    fullscreen: Boolean,
    onEnterFullscreen: () -> Unit,
    onExitFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playerView: @Composable (Modifier) -> Unit = { viewModifier ->
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { it.player = player },
            modifier = viewModifier
        )
    }

    if (fullscreen) {
        Dialog(
            onDismissRequest = onExitFullscreen,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            BackHandler(onBack = onExitFullscreen)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                playerView(Modifier.fillMaxSize())
                BufferingOverlay(visible = isBuffering)
                IconButton(
                    onClick = onExitFullscreen,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Exit fullscreen", tint = Color.White)
                }
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
        ) {
            playerView(Modifier.fillMaxSize())
            BufferingOverlay(visible = isBuffering)
            IconButton(
                onClick = onEnterFullscreen,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Rounded.Fullscreen,
                    contentDescription = "View fullscreen",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun BufferingOverlay(visible: Boolean) {
    if (!visible) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.32f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 3.dp
        )
    }
}
