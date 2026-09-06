package com.esupplemental.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.spacing

/**
 * Standard M3 section header.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        action?.invoke()
    }
}

/**
 * A specialized card for displaying numerical statistics.
 * Uses Quicksand (via titleLarge/labelSmall) to ensure legibility.
 */
@Composable
fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val resolvedContentColor = if (contentColor == Color.Unspecified) {
        contentColorFor(backgroundColor)
    } else {
        contentColor
    }
    
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = resolvedContentColor
        ),
        modifier = modifier.height(140.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = resolvedContentColor.copy(alpha = 0.72f), 
                modifier = Modifier.size(spacing.large)
            )
            Spacer(Modifier.height(spacing.small))
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, // Force Quicksand for numbers
                    fontWeight = FontWeight.Black
                )
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = resolvedContentColor.copy(alpha = 0.85f)
            )
        }
    }
}

/**
 * Time formatting utility.
 */
fun Int.toTimeLabel(): String {
    val m = this / 60
    val s = this % 60
    return "%d:%02d".format(m, s)
}
