package com.esupplemental.presentation.ui.screens.player

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MusicWaveAnimation(accentColor: Color, amplitudes: List<Float>) {
    Row(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom
    ) {
        amplitudes.forEach { amp ->
            val animatedHeight by animateFloatAsState(
                targetValue = (amp * 80f).coerceAtLeast(10f),
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "wave_height"
            )
            WaveBar(animatedHeight.dp, accentColor)
        }
    }
}

@Composable
private fun WaveBar(height: androidx.compose.ui.unit.Dp, color: Color) {
    Box(
        modifier = Modifier
            .width(8.dp)
            .height(height)
            .background(color, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
    )
}