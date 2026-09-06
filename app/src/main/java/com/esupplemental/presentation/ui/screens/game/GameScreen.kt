package com.esupplemental.presentation.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.data.model.game.GameCatalogue
import com.esupplemental.domain.model.game.GameItem
import com.esupplemental.presentation.state.game_state.GameScreenUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.viewmodel.GameScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameScreen(
    viewModel: GameScreenViewModel = koinViewModel(),
    onPlayGame: (gameId: String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AppBackground {
        GameScreenContent(
            state = state,
            onSelectDifficulty = viewModel::selectDifficulty,
            onPlayGame = onPlayGame
        )
    }
}

// ── Stateless content ─────────────────────────────────────────────────────

@Composable
fun GameScreenContent(
    state: GameScreenUiState,
    onSelectDifficulty: (GameDifficulty) -> Unit,
    onPlayGame: (String) -> Unit
) {
    val currentGameId = remember(state.games) {
        state.games.firstOrNull { !it.isLocked && it.stars == 0 }?.id
    }
    LazyColumn(
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        // ── Hero header ────────────────────────────────────────────────
        item {
            GameHeroHeader(
                totalXp = state.totalXp,
                decoSpin = 0f,
                decoFloat = 0f
            )
        }

        // ── Difficulty selector ────────────────────────────────────────
        item {
            DifficultySelector(
                selected = state.selectedDifficulty,
                onSelect = onSelectDifficulty,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        // ── Level banner ───────────────────────────────────────────────
        item {
            LevelBanner(
                difficulty = state.selectedDifficulty,
                gameCount = state.games.size,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(14.dp))
        }

        if (state.games.isEmpty()) {
            item {
                GameLockedState(
                    message = "No quests are available in this challenge yet.",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        // ── Game cards ─────────────────────────────────────────────────
        itemsIndexed(state.games, key = { _, game -> game.id }) { index, game ->
            AdventureLevelItem(
                game = game,
                isCompleted = game.id in state.completedGameIds,
                levelNumber = index + 1,
                isCurrent = game.id == currentGameId,
                isFirst = index == 0,
                isLast = index == state.games.lastIndex,
                onPlay = { onPlayGame(game.id) }
            )
        }
    }
}

@Composable
private fun AdventureLevelItem(
    game: GameItem,
    isCompleted: Boolean,
    levelNumber: Int,
    isCurrent: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onPlay: () -> Unit
) {
    val difficultyColors = rememberGameDifficultyColors(game.difficulty)
    val pathColor = difficultyColors.accent.copy(alpha = 0.28f)
    Box(modifier = Modifier.fillMaxWidth()) {
        Canvas(Modifier.matchParentSize()) {
            val x = 30.dp.toPx()
            val nodeCenter = 64.dp.toPx()
            drawLine(
                color = pathColor,
                start = Offset(x, if (isFirst) nodeCenter else 0f),
                end = Offset(x, if (isLast) nodeCenter else size.height),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        Surface(
            modifier = Modifier
                .padding(start = 8.dp, top = 42.dp)
                .size(44.dp)
                .align(Alignment.TopStart),
            shape = CircleShape,
            color = when {
                game.isLocked -> MaterialTheme.colorScheme.surfaceVariant
                isCompleted || isCurrent -> difficultyColors.accent
                else -> MaterialTheme.colorScheme.surface
            },
            border = BorderStroke(
                if (isCurrent) 3.dp else 2.dp,
                if (game.isLocked) MaterialTheme.colorScheme.outlineVariant else difficultyColors.accent
            ),
            shadowElevation = if (isCurrent) 7.dp else 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                when {
                    game.isLocked -> Icon(
                        Icons.Rounded.Lock,
                        contentDescription = "Level $levelNumber locked",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    isCompleted -> Icon(
                        Icons.Rounded.Check,
                        contentDescription = "Level $levelNumber completed",
                        tint = difficultyColors.onAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    else -> Text(
                        text = levelNumber.toString(),
                        color = if (isCurrent) difficultyColors.onAccent else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }

        GameCard(
            game = game,
            isCompleted = isCompleted,
            isCurrent = isCurrent,
            onPlay = onPlay,
            modifier = Modifier
                .padding(start = 66.dp, end = 20.dp)
                .padding(bottom = 14.dp)
        )
    }
}


// ── Previews ──────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Game Screen – Easy Light")
@Composable
private fun GameScreenEasyPreview() {
    ESupplementalTheme(darkTheme = false) {
        GameScreenContent(
            state = GameScreenUiState(
                selectedDifficulty = GameDifficulty.EASY,
                games = GameCatalogue.byDifficulty(GameDifficulty.EASY),
                totalXp = 120,
                completedGameIds = setOf("listen_slap")
            ),
            onSelectDifficulty = {},
            onPlayGame = {}
        )
    }
}

@Preview(showBackground = true, name = "Game Screen – Moderate")
@Composable
private fun GameScreenModeratePreview() {
    ESupplementalTheme(darkTheme = false) {
        GameScreenContent(
            state = GameScreenUiState(
                selectedDifficulty = GameDifficulty.MODERATE,
                games = GameCatalogue.byDifficulty(GameDifficulty.MODERATE),
                totalXp = 240,
                completedGameIds = setOf("listen_slap", "story_order", "word_bingo")
            ),
            onSelectDifficulty = {},
            onPlayGame = {}
        )
    }
}

@Preview(showBackground = true, name = "Game Screen – Hard Dark")
@Composable
private fun GameScreenHardDarkPreview() {
    ESupplementalTheme(darkTheme = true) {
        GameScreenContent(
            state = GameScreenUiState(
                selectedDifficulty = GameDifficulty.HARD,
                games = GameCatalogue.byDifficulty(GameDifficulty.HARD),
                totalXp = 400,
                completedGameIds = setOf(
                    "listen_slap",
                    "story_order",
                    "disappearing_text",
                    "speed_typer"
                )
            ),
            onSelectDifficulty = {},
            onPlayGame = {}
        )
    }
}
