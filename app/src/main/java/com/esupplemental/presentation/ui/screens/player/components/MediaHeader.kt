package com.esupplemental.presentation.ui.screens.player.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.esupplemental.data.model.MediaItem

@Composable
fun MediaHeader(
    item: MediaItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.title.uppercase(),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
