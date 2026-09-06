package com.esupplemental.presentation.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun LevelBanner(
    difficulty: GameDifficulty,
    gameCount: Int,
    modifier: Modifier = Modifier
) {
    val difficultyColors = rememberGameDifficultyColors(difficulty)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(difficultyColors.container)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        // Decorative dots
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 10.dp)
                .clip(CircleShape)
                .background(difficultyColors.onContainer.copy(alpha = 0.08f))
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Challenge icon badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(difficultyColors.accent)
            ) {
                Icon(
                    imageVector = difficulty.emoji,
                    contentDescription = difficulty.label,
                    tint = difficultyColors.onAccent,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column {
                Text(
                    text = "${difficulty.label} Level",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
                    ),
                    color = difficultyColors.onContainer
                )
                Text(
                    text = "$gameCount games available",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = difficultyColors.onContainer.copy(alpha = 0.80f)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Level Banners - All Difficulties")
@Composable
fun LevelBannerPreview() {
    ESupplementalTheme {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Easy Level
            LevelBanner(
                difficulty = GameDifficulty.EASY,
                gameCount = 12
            )

            // Medium Level
            LevelBanner(
                difficulty = GameDifficulty.MODERATE,
                gameCount = 8
            )

            // Hard Level
            LevelBanner(
                difficulty = GameDifficulty.HARD,
                gameCount = 5
            )
        }
    }
}

@Preview(showBackground = true, name = "Banner Detail")
@Composable
fun LevelBannerInteractivePreview() {
    ESupplementalTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LevelBanner(
                difficulty = GameDifficulty.EASY,
                gameCount = 10
            )
        }
    }
}
