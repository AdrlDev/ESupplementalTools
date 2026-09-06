package com.esupplemental.presentation.ui.screens.notes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun FormattingToolbar(
    isBold: Boolean,
    isItalic: Boolean,
    isUnderline: Boolean,
    onBold: () -> Unit,
    onItalic: () -> Unit,
    onUnderline: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Format:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(12.dp))
            FormatToggleButton(
                label = "B",
                active = isBold,
                fontWeight = FontWeight.ExtraBold,
                onClick = onBold
            )
            Spacer(Modifier.width(8.dp))
            FormatToggleButton(
                label = "I",
                active = isItalic,
                fontStyle = FontStyle.Italic,
                onClick = onItalic
            )
            Spacer(Modifier.width(8.dp))
            FormatToggleButton(
                label = "U",
                active = isUnderline,
                textDecoration = TextDecoration.Underline,
                onClick = onUnderline
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormattingToolbarPreview() {
    ESupplementalTheme {
        var isBold by remember { mutableStateOf(true) }
        var isItalic by remember { mutableStateOf(false) }
        var isUnderline by remember { mutableStateOf(false) }

        FormattingToolbar(
            isBold = isBold,
            isItalic = isItalic,
            isUnderline = isUnderline,
            onBold = { isBold = !isBold },
            onItalic = { isItalic = !isItalic },
            onUnderline = { isUnderline = !isUnderline }
        )
    }
}