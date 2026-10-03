package com.esupplemental.presentation.ui.screens.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Gamepad
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.data.model.UserStats
import com.esupplemental.data.model.MediaType
import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.domain.model.game.GameItem
import com.esupplemental.presentation.state.ProgressUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.components.CircularProgressRing
import com.esupplemental.presentation.ui.components.SectionHeader
import com.esupplemental.presentation.ui.components.StatCard
import com.esupplemental.presentation.ui.screens.home.HeaderSection
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.RewardGold
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.viewmodel.ProgressViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AppBackground {
        ProgressScreenContent(state = state)
    }
}

@Composable
fun ProgressScreenContent(state: ProgressUiState) {
    val stats = state.stats
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme

    val quizList = state.quizHistory.filterNotNull()
    val storyQuizCount = quizList.count { it.type.equals(MediaType.STORY.name, ignoreCase = true) }
    val songQuizCount = quizList.count { it.type.equals(MediaType.SONG.name, ignoreCase = true) }
    val poemQuizCount = quizList.count { it.type.equals(MediaType.POEM.name, ignoreCase = true) }
    val totalPossiblePoints = state.quizHistory.filterNotNull().sumOf { it.total }
    val progressRatio = (if (totalPossiblePoints > 0) {
        stats.overallScore / totalPossiblePoints.toFloat()
    } else {
        0f
    }).coerceIn(0f, 1f)
    val percentage = (progressRatio * 100).toInt()
    val averagePercentage = (stats.quizzesAverage.coerceIn(0f, 1f) * 100).toInt()
    val levelProgress = stats.levelProgress.coerceIn(0f, 1f)

    // Game stats calculations
    val playedGames = state.games.filter { it.stars > 0 || it.xpReward > 0 }
    val totalGameXp = state.games.sumOf { it.xpReward }
    val totalStars = state.games.sumOf { it.stars }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Games, 2: Quizzes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.screenPadding)
    ) {
        Spacer(Modifier.height(spacing.screenPadding))

        HeaderSection(
            title = "My Progress",
            subTitle = "You're crushing your learning goals!"
        )

        Spacer(Modifier.height(spacing.large))

        val cardGradient = Brush.linearGradient(
            colors = listOf(
                colorScheme.primaryContainer,
                colorScheme.secondaryContainer
            )
        )

        // --- THE "HERO" SCORE CARD ---
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .background(cardGradient)
                    .padding(spacing.large)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressRing(
                        progress = progressRatio,
                        size = 110.dp,
                        strokeWidth = 14.dp,
                        progressColor = colorScheme.primary,
                        trackColor = colorScheme.surface.copy(alpha = 0.3f),
                        label = "$percentage%"
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        ProgressMotivationMessage(
                            percentage = percentage,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        // Total XP pill badge
                        Surface(
                            color = colorScheme.surface.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Bolt,
                                    contentDescription = null,
                                    tint = RewardGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$totalGameXp Total XP Earned",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                    color = colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(spacing.extraLarge))

        // --- DETAILED STATISTICS GRID ---
        SectionHeader(title = "HALL OF FAME")
        Spacer(Modifier.height(spacing.medium))

        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                StatCard(
                    value = "$storyQuizCount",
                    label = "Story Quizzes",
                    icon = Icons.Rounded.AutoStories,
                    backgroundColor = colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "$songQuizCount",
                    label = "Song Quizzes",
                    icon = Icons.Rounded.MusicNote,
                    backgroundColor = colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "$poemQuizCount",
                    label = "Poem Quizzes",
                    icon = Icons.Rounded.AutoStories,
                    backgroundColor = colorScheme.tertiary,
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                StatCard(
                    value = "${playedGames.size}/${state.games.size.coerceAtLeast(1)}",
                    label = "Games",
                    icon = Icons.Rounded.Gamepad,
                    backgroundColor = ArcadeColors.Purple,
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "$totalStars",
                    label = "Stars",
                    icon = Icons.Rounded.Star,
                    backgroundColor = RewardGold,
                    contentColor = Color(0xFF5A3E00),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "$averagePercentage%",
                    label = "Quiz Avg",
                    icon = Icons.Rounded.Quiz,
                    backgroundColor = colorScheme.tertiary,
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(spacing.extraLarge))

        // --- THE "NEXT LEVEL" BAR ---
        SectionHeader(title = "EXP PROGRESS")
        Spacer(Modifier.height(spacing.small))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(spacing.medium)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "LEVEL ${stats.level}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Next Level: ${(levelProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = colorScheme.primary
                    )
                }

                Spacer(Modifier.height(spacing.small))

                LinearProgressIndicator(
                    progress = { levelProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(CircleShape),
                    color = colorScheme.secondary,
                    trackColor = colorScheme.secondary.copy(alpha = 0.15f),
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(Modifier.height(spacing.extraLarge))

        // --- RECENT ACTIVITY & GAMES HISTORY ---
        SectionHeader(title = "RECENT ACTIVITY & GAMES")
        Spacer(Modifier.height(spacing.small))

        // Filter Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActivityFilterChip(
                label = "All Activity",
                icon = Icons.Rounded.History,
                isSelected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f)
            )
            ActivityFilterChip(
                label = "Games (${playedGames.size})",
                icon = Icons.Rounded.Gamepad,
                isSelected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f)
            )
            ActivityFilterChip(
                label = "Quizzes (${state.quizHistory.filterNotNull().size})",
                icon = Icons.Rounded.Quiz,
                isSelected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(spacing.medium))

        val showGames = (selectedTab == 0 || selectedTab == 1) && state.games.isNotEmpty()
        val showQuizzes = (selectedTab == 0 || selectedTab == 2) && quizList.isNotEmpty()

        val isEmpty = when (selectedTab) {
            1 -> playedGames.isEmpty()
            2 -> quizList.isEmpty()
            else -> playedGames.isEmpty() && quizList.isEmpty()
        }

        if (isEmpty) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = colorScheme.surfaceVariant.copy(alpha = 0.25f),
                border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.medium)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = ArcadeColors.Teal.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.RocketLaunch,
                                contentDescription = null,
                                tint = ArcadeColors.Teal,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "No Activities Recorded Yet!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Complete stories and play learning games to see your stars and XP here!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Render games if active tab
                if (showGames) {
                    val gamesToDisplay = if (selectedTab == 1) state.games else playedGames
                    gamesToDisplay.forEach { game ->
                        PlayfulGameHistoryItem(game = game)
                    }
                }

                // Render quizzes if active tab
                if (showQuizzes) {
                    quizList.forEach { history ->
                        PlayfulQuizHistoryItem(item = history)
                    }
                }
            }
        }

        Spacer(Modifier.height(spacing.extraLarge))
    }
}

@Composable
private fun ActivityFilterChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) colorScheme.primary else colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = if (isSelected) null else BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = if (isSelected) Color.White else colorScheme.onSurfaceVariant
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = if (isSelected) Color.White else colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Progress Screen - Light")
@Composable
fun ProgressScreenLightPreview() {
    val mockStats = UserStats(
        userName = "Adriel",
        overallScore = 0.85f,
        songsCompleted = 0,
        storiesCompleted = 8,
        quizzesAverage = 0.90f,
        level = 5,
        levelProgress = 0.75f
    )

    val mockGames = listOf(
        GameItem(
            id = "speed_typer",
            icon = Icons.Rounded.Gamepad,
            title = "Speed Typer",
            description = "Type fast!",
            skillTags = emptyList(),
            difficulty = GameDifficulty.HARD,
            stars = 3,
            xpReward = 60
        ),
        GameItem(
            id = "story_order",
            icon = Icons.Rounded.AutoStories,
            title = "Story Order",
            description = "Order sequence",
            skillTags = emptyList(),
            difficulty = GameDifficulty.EASY,
            stars = 2,
            xpReward = 30
        )
    )

    ESupplementalTheme(darkTheme = false) {
        ProgressScreenContent(
            state = ProgressUiState(
                stats = mockStats,
                games = mockGames
            )
        )
    }
}
