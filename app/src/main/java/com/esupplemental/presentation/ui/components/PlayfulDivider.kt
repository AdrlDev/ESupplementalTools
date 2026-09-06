package com.esupplemental.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.spacing

/**
 * Playful star-dotted divider between exercise sections.
 * Uses semantic theme colors and standardized spacing.
 */
@Composable
fun PlayfulDivider(color: Color = MaterialTheme.colorScheme.outlineVariant) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = color.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Text(
            text = "✦",
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.5f)
        )
        Text(
            text = "✦",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.8),
            color = color.copy(alpha = 0.4f)
        )
        Text(
            text = "✦",
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.5f)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = color.copy(alpha = 0.3f),
            thickness = 1.dp
        )
    }
}
