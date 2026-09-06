package com.esupplemental.presentation.ui.screens.player.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.components.AppBackground

@Composable
fun PlayerErrorState(
    message: String,
    onBack: () -> Unit
) {
    AppBackground {

        Box(
            modifier =
                Modifier.fillMaxSize(),
            contentAlignment =
                Alignment.Center
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector =
                        Icons.Rounded.ErrorOutline,
                    contentDescription =
                        null,
                    modifier =
                        Modifier.size(64.dp),
                    tint =
                        MaterialTheme
                            .colorScheme
                            .error
                )

                Spacer(
                    Modifier.height(20.dp)
                )

                Text(
                    text =
                        "Unable to Load",
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall
                )

                Spacer(
                    Modifier.height(8.dp)
                )

                Text(
                    text = message,
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    Modifier.height(24.dp)
                )

                Button(
                    onClick = onBack
                ) {
                    Text("Go Back")
                }
            }
        }
    }
}