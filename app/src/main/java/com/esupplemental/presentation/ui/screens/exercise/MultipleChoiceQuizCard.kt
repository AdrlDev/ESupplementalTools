package com.esupplemental.presentation.ui.screens.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esupplemental.data.model.MultipleChoiceQuestion
import com.esupplemental.presentation.ui.screens.game.rememberGameSemanticColors

@Composable
fun MultipleChoiceQuizCard(
    index: Int,
    question: MultipleChoiceQuestion,
    selectedIndex: Int?,
    isSubmitted: Boolean,
    isInteractionEnabled: Boolean = true,
    isLoadingAudio: Boolean,
    onPlayAudio: () -> Unit,
    onChoiceSelected: (Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val semanticColors = rememberGameSemanticColors()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$index. ${question.question}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            
            if (isLoadingAudio) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = colorScheme.onSecondaryContainer
                )
            } else {
                IconButton(onClick = onPlayAudio) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "Play Question Audio",
                        tint = colorScheme.onSecondaryContainer
                    )
                }
            }
        }
        
        Spacer(Modifier.height(10.dp))

        question.choices.forEachIndexed { choiceIdx, choiceText ->
            val isSelected = selectedIndex == choiceIdx
            val isCorrectChoice = choiceIdx == question.correctAnswerIndex

            val (icon, tint, bgAlpha) = when {
                !isSubmitted && isSelected -> Triple(Icons.Rounded.RadioButtonUnchecked, colorScheme.onSecondaryContainer, 0.12f)
                !isSubmitted -> Triple(Icons.Rounded.RadioButtonUnchecked, colorScheme.outline, 0f)
                isSubmitted && isCorrectChoice -> Triple(Icons.Rounded.CheckCircle, semanticColors.success, 0.14f)
                isSubmitted && isSelected -> Triple(Icons.Rounded.Cancel, colorScheme.error, 0.10f)
                else -> Triple(Icons.Rounded.RadioButtonUnchecked, colorScheme.outline, 0f)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tint.copy(alpha = bgAlpha))
                    .clickable(enabled = !isSubmitted && isInteractionEnabled) {
                        onChoiceSelected(choiceIdx)
                    }
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    text = choiceText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isSelected || (isSubmitted && isCorrectChoice)) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isSubmitted && (isCorrectChoice || isSelected)) tint else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
