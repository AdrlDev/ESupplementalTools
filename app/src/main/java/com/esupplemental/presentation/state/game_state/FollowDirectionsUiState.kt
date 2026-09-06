package com.esupplemental.presentation.state.game_state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.IntOffset
import com.esupplemental.domain.model.game.StoryGameContext

@Immutable
data class FollowDirectionsUiState(
    val phase: FollowDirectionsPhase = FollowDirectionsPhase.IDLE,
    val storyContext: StoryGameContext? = null,
    val currentChallenge: DirectionChallenge? = null,
    val characterPosition: IntOffset = IntOffset(2, 2),
    val targetPosition: IntOffset = IntOffset(0, 0),
    val gridSize: Int = 5,
    val movesLeft: Int = 0,
    val maxMoves: Int = 0,
    val round: Int = 0,
    val totalRounds: Int = 4,
    val score: Int = 0,
    val xpEarned: Int = 0,
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

enum class FollowDirectionsPhase {
    IDLE,
    LOADING,
    LISTENING,
    PLAYING,
    CORRECT,
    WRONG,
    GAME_OVER
}

data class DirectionChallenge(
    val id: String,
    val instructionText: String,
    val steps: List<DirectionStep>,
    val start: IntOffset,
    val target: IntOffset
)

data class DirectionStep(
    val direction: Direction,
    val steps: Int
)

enum class Direction { UP, DOWN, LEFT, RIGHT }
