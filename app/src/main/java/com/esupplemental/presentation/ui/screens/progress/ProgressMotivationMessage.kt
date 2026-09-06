package com.esupplemental.presentation.ui.screens.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tram
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProgressMotivationMessage(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    val (title, subtext, emoji) = when {
        percentage >= 90 -> Triple("Excellent!", "You're a master of this level!", Icons.Rounded.EmojiEvents)
        percentage >= 75 -> Triple("Good job!", "Keep it up and you'll do even better!", Icons.Rounded.Celebration)
        percentage >= 50 -> Triple("On the right track!", "A little more practice and you'll be there!", Icons.Rounded.FitnessCenter)
        else -> Triple("Keep going!", "Every exercise brings you closer to your goal!", Icons.Rounded.Star)
    }

    Column(
        modifier = modifier.padding(start = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = emoji,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtext,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified // Optional: fine-tune spacing
        )
    }
}