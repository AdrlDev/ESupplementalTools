package com.esupplemental.presentation.ui.screens.player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.components.AppBackground

@Composable
fun PlayerLoadingState(
    isPreparingStory: Boolean
) {
    AppBackground {
        Box(
            modifier =
                Modifier.fillMaxSize(),
            contentAlignment =
                Alignment.Center
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {

                CircularProgressIndicator(
                    modifier =
                        Modifier.size(48.dp),
                    strokeWidth = 4.dp,
                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )

                Spacer(
                    Modifier.height(20.dp)
                )

                Text(
                    text =
                        if (isPreparingStory) {
                            "Preparing your story..."
                        } else {
                            "Loading..."
                        },
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                if (isPreparingStory) {

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Preparing narration and word timing.",
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        }
    }
}