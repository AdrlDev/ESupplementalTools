package com.esupplemental.presentation.ui.screens.player.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.esupplemental.data.model.MediaItem
import com.esupplemental.presentation.ui.theme.elevations
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.ui.screens.player.MusicWaveAnimation

/**
 * Senior Engineer Note: Encapsulates the 3D-effect media stage (CD or Book cover)
 * with integrated audio wave animations.
 */
@Composable
fun MediaStage(
    item: MediaItem,
    isPlaying: Boolean,
    amplitudes: List<Float>,
    accentColor: Color,
    isSong: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val shapes = MaterialTheme.shapes

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(spacing.large),
        contentAlignment = Alignment.Center
    ) {
        // Shadow / Floor effect
        Surface(
            modifier = Modifier.size(200.dp).offset(y = 12.dp),
            shape = shapes.extraLarge,
            color = Color.Black.copy(alpha = 0.1f)
        ) {}

        Card(
            shape = shapes.extraLarge,
            modifier = Modifier.size(200.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevations.medium)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(accentColor, accentColor.copy(alpha = 0.6f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Background Thumbnail
                if (!item.thumbnailUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = item.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().blur(1.dp)
                    )
                } else if (item.thumbnailRes != null && item.thumbnailRes != 0) {
                    Image(
                        painter = painterResource(id = item.thumbnailRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().blur(3.dp)
                    )
                }

                // Wave animation or Icon
                if (isPlaying) {
                    MusicWaveAnimation(
                        accentColor = MaterialTheme.colorScheme.secondary,
                        amplitudes = amplitudes
                    )
                } else {
                    if (item.thumbnailRes != null && item.thumbnailRes != 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.25f))
                        )
                    }
                    Icon(
                        imageVector = if (isSong) Icons.Rounded.MusicNote else Icons.Rounded.AutoStories,
                        contentDescription = null,
                        tint = if (isSong) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
        }
    }
}
