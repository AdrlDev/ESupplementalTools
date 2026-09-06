package com.esupplemental.presentation.ui.screens.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.data.local.entity.QuizHistoryEntity
import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.domain.model.game.GameItem
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.RewardGold
import com.esupplemental.presentation.ui.theme.spacing

/**
 * An item representing a past story quiz result with playful sticker design.
 */
@Composable
fun PlayfulQuizHistoryItem(
    item: QuizHistoryEntity,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val scorePercentage = if (item.total > 0) {
        (item.score.toFloat() / item.total.toFloat() * 100).toInt()
    } else 0

    val performanceColor = when {
        scorePercentage >= 80 -> ArcadeColors.Success
        scorePercentage >= 60 -> ArcadeColors.Warning
        else -> ArcadeColors.Rose
    }

    val feedbackText = when {
        scorePercentage >= 90 -> "Super Star! 🌟"
        scorePercentage >= 70 -> "Great Job! 🎯"
        scorePercentage >= 50 -> "Good Effort! 👍"
        else -> "Keep Going! 💪"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.5.dp, colorScheme.outline.copy(alpha = 0.15f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Surface(
                color = colorScheme.primary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Quiz,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Story Quiz Quest",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        ),
                        color = colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${item.score} / ${item.total} Right",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = performanceColor
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = feedbackText,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            // Score Pill Badge
            Surface(
                color = performanceColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, performanceColor.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (scorePercentage >= 70) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                        contentDescription = null,
                        tint = performanceColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$scorePercentage%",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        ),
                        color = performanceColor
                    )
                }
            }
        }
    }
}

/**
 * An item representing a game played, its earned stars, and XP gained.
 */
@Composable
fun PlayfulGameHistoryItem(
    game: GameItem,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val accentColor = when (game.difficulty) {
        GameDifficulty.EASY -> ArcadeColors.Teal
        GameDifficulty.MODERATE -> ArcadeColors.Warning
        GameDifficulty.HARD -> ArcadeColors.Purple
    }

    val difficultyLabel = when (game.difficulty) {
        GameDifficulty.EASY -> "Easy"
        GameDifficulty.MODERATE -> "Moderate"
        GameDifficulty.HARD -> "Hard"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Game Icon
            Surface(
                color = accentColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = game.icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Game Name, Difficulty & Stars
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        ),
                        color = colorScheme.onSurface
                    )
                    // Difficulty Chip
                    Surface(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = difficultyLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Stars Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(3) { starIndex ->
                        val isEarned = starIndex < game.stars
                        Icon(
                            imageVector = if (isEarned) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                            contentDescription = null,
                            tint = if (isEarned) RewardGold else colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (game.stars == 3) "Mastered! 🏆" else if (game.stars > 0) "${game.stars} Stars" else "Ready to Play 🚀",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (game.stars > 0) RewardGold else colorScheme.onSurfaceVariant
                    )
                }
            }

            // XP Earned Pill Badge
            if (game.xpReward > 0 || game.stars > 0) {
                Surface(
                    color = RewardGold.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, RewardGold.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = "XP",
                            tint = RewardGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "+${game.xpReward} XP",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            ),
                            color = RewardGold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayfulQuizHistoryPreview() {
    ESupplementalTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PlayfulQuizHistoryItem(
                item = QuizHistoryEntity(
                    id = 1,
                    userId = "1",
                    score = 9,
                    total = 10,
                    userAnswers = "",
                    correctAnswers = "",
                    timestamp = 0
                )
            )
        }
    }
}
