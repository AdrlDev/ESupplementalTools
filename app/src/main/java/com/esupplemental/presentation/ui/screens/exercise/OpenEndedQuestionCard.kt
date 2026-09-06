package com.esupplemental.presentation.ui.screens.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.BrandTealDark
import com.esupplemental.presentation.ui.theme.BrandNavy
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.rounded.Assignment

/**
 * Open-ended question wrapped in a styled card with teal accent strip.
 */
@Composable
fun OpenEndedQuestionCard(
    index: Int,
    question: String,
    answer: String,
    isSubmitted: Boolean,
    sampleAnswer: String?,
    onAnswerChange: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colorScheme.surface)
    ) {
        Column {
            // Teal accent strip with question number
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(
                        Brush.horizontalGradient(listOf(BrandNavy, BrandTealDark))
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Question $index",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color.White
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = answer,
                    onValueChange = onAnswerChange,
                    placeholder = {
                        Text(
                            "Write your answer here...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colorScheme.onSurface.copy(alpha = 0.40f)
                            )
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null,
                            tint = colorScheme.secondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    enabled = !isSubmitted,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.secondary,
                        unfocusedBorderColor = colorScheme.outlineVariant
                    )
                )
                if (sampleAnswer != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorScheme.secondaryContainer)
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Assignment,
                            contentDescription = null,
                            tint = colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "Sample Answer:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = sampleAnswer,
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
