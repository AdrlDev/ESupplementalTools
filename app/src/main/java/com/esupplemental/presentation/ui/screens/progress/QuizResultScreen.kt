package com.esupplemental.presentation.ui.screens.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.presentation.ui.components.CircularProgressRing
import com.esupplemental.presentation.ui.theme.*
import com.esupplemental.presentation.viewmodel.QuizResultViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuizResultScreen(
    score: Int,
    total: Int,
    resultId: Long,
    onRetry: () -> Unit,
    onHome: () -> Unit,
    viewModel: QuizResultViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(resultId) {
        if (resultId != -1L) {
            viewModel.loadResult(resultId)
        }
    }

    val displayScore = if (state.resultId != null) state.score else score
    val displayTotal = if (state.resultId != null) state.total else total

    QuizResultContent(
        score = displayScore,
        total = displayTotal,
        onRetry = onRetry,
        onHome = onHome
    )
}

@Composable
fun QuizResultContent(
    score: Int,
    total: Int,
    onRetry: () -> Unit,
    onHome: () -> Unit
) {
    val displayTotal = total.coerceAtLeast(0)
    val displayScore = if (displayTotal > 0) score.coerceIn(0, displayTotal) else 0
    val scoreRatio = if (displayTotal > 0) {
        displayScore.toFloat() / displayTotal.toFloat()
    } else {
        0f
    }.coerceIn(0f, 1f)
    val percentage = (scoreRatio * 100).toInt()
    val isPassing = percentage >= 70
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(spacing.huge))

        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(animationSpec = tween(500)) + fadeIn()
        ) {
            Icon(
                imageVector = if (isPassing) Icons.Default.EmojiEvents else Icons.Default.SentimentNeutral,
                contentDescription = null,
                tint = if (isPassing) StarGold else colorScheme.outline,
                modifier = Modifier.size(96.dp)
            )
        }

        Spacer(Modifier.height(spacing.medium))

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(500)) + fadeIn()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isPassing) "Congratulations! 🎉" else "Nice Try!",
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    color = if (isPassing) colorScheme.primary else colorScheme.onBackground
                )
                Spacer(Modifier.height(spacing.small))
                Text(
                    text = if (isPassing) "You did an amazing job!" else "Keep practicing to improve!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(spacing.extraLarge))

        CircularProgressRing(
            progress = scoreRatio,
            size = 180.dp,
            strokeWidth = 18.dp,
            progressColor = if (isPassing) colorScheme.secondary else colorScheme.error,
            trackColor = colorScheme.surfaceVariant.copy(alpha = 0.5f),
            label = "$percentage%",
            sublabel = "$displayScore / $displayTotal"
        )

        Spacer(Modifier.height(spacing.extraLarge))

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = if (isPassing) colorScheme.primaryContainer else colorScheme.errorContainer,
                contentColor = if (isPassing) colorScheme.onPrimaryContainer else colorScheme.onErrorContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(spacing.medium)) {
                Text(
                    text = "Score Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(spacing.small))
                ScoreRow(label = "Correct answers", value = "$displayScore")
                ScoreRow(label = "Total questions", value = "$displayTotal")
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = spacing.small),
                    color = if (isPassing) colorScheme.onPrimaryContainer.copy(alpha = 0.2f) else colorScheme.onErrorContainer.copy(alpha = 0.2f)
                )
                ScoreRow(
                    label = "Final score",
                    value = "$percentage%",
                    isBold = true
                )
            }
        }

        Spacer(Modifier.height(spacing.extraLarge))

        Button(
            onClick = onHome,
            shape = PlayfulShapes.ActionButton,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPassing) colorScheme.primary else colorScheme.secondary
            ),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Rounded.Home, contentDescription = null)
            Spacer(Modifier.width(spacing.small))
            Text("Back to Home", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(spacing.small))

        OutlinedButton(
            onClick = onRetry,
            shape = PlayfulShapes.ActionButton,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Rounded.Refresh, contentDescription = null)
            Spacer(Modifier.width(spacing.small))
            Text("Try Again", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(spacing.huge))
    }
}

@Preview(showBackground = true, name = "Quiz Result - Passing State")
@Composable
fun QuizResultPassingPreview() {
    ESupplementalTheme(darkTheme = false) {
        QuizResultContent(score = 8, total = 10, onRetry = {}, onHome = {})
    }
}
