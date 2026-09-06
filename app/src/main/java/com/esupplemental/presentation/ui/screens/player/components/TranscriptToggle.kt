package com.esupplemental.presentation.ui.screens.player.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing

@Composable
fun TranscriptToggle(
    modifier: Modifier = Modifier,
    showTranscript: Boolean,
    onToggle: () -> Unit,
    color: Color
) {
    val spacing = MaterialTheme.spacing
    
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            onClick = onToggle,
            shape = CircleShape,
            color = color.copy(alpha = 0.15f),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.background.copy(alpha = 0.3f)),
            modifier = Modifier.padding(top = spacing.small)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = spacing.medium, vertical = spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (showTranscript) Icons.Rounded.Audiotrack else Icons.Rounded.Description,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(spacing.small))

                Text(
                    text = if (showTranscript) "SHOW CONTROLS" else "VIEW TRANSCRIPT",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = color
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Transcript Toggle")
@Composable
private fun TranscriptTogglePreview() {
    ESupplementalTheme {
        TranscriptToggle(
            showTranscript = false,
            onToggle = {},
            color = MaterialTheme.colorScheme.primary
        )
    }
}
