package com.esupplemental.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.BrandAqua

/**
 * Story-themed ✦ divider between sections.
 */
@Composable
fun StoryDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HorizontalDivider(Modifier.weight(1f), color = BrandAqua.copy(alpha = 0.35f))
        Icon(
            imageVector = Icons.Rounded.AutoStories,
            contentDescription = null,
            tint = BrandAqua.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
        HorizontalDivider(Modifier.weight(1f), color = BrandAqua.copy(alpha = 0.35f))
    }
}