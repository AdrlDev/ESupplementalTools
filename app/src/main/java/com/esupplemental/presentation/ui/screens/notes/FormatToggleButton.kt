package com.esupplemental.presentation.ui.screens.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun FormatToggleButton(
    label: String,
    active: Boolean,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    textDecoration: TextDecoration = TextDecoration.None,
    onClick: () -> Unit
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val activeTextColor = MaterialTheme.colorScheme.onPrimary

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (active) activeColor else Color.Transparent,
        border = if (!active) BorderStroke(1.dp, activeColor) else null,
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                style = MaterialTheme.typography.titleMedium.copy(textDecoration = textDecoration),
                color = if (active) activeTextColor else activeColor
            )
        }
    }
}