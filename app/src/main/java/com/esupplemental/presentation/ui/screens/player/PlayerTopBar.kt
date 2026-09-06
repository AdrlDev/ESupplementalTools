package com.esupplemental.presentation.ui.screens.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.theme.BrandNavy
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun PlayerTopBar(
    title: String,
    onBack: () -> Unit,
    accentColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Reserves space for status bar icons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = accentColor
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = accentColor,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Player Top Bar - Light Mode")
@Composable
fun PlayerTopBarLightPreview() {
    ESupplementalTheme(darkTheme = false) {
        AppBackground {
            PlayerTopBar(
                title = "Laro ng Lahi Story",
                onBack = {},
                accentColor = BrandNavy
            )
        }
    }
}

@Preview(showBackground = true, name = "Player Top Bar - Dark Mode")
@Composable
fun PlayerTopBarDarkPreview() {
    ESupplementalTheme(darkTheme = true) {
        AppBackground {
            PlayerTopBar(
                title = "Patintero Guide",
                onBack = {},
                accentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}