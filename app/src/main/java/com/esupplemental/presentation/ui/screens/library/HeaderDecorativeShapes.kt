package com.esupplemental.presentation.ui.screens.library

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HeaderDecorativeShapes(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.drawBehind {
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = 100.dp.toPx(),
                center = Offset(size.width * 0.9f, size.height * 0.2f)
            )
            drawCircle(
                color = Color.Black.copy(alpha = 0.05f),
                radius = 60.dp.toPx(),
                center = Offset(size.width * 0.1f, size.height * -0.1f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = 40.dp.toPx(),
                center = Offset(size.width * 0.5f, size.height * 0.5f)
            )
        }
    )
}
