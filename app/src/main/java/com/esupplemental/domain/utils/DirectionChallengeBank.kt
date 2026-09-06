package com.esupplemental.domain.utils

import androidx.compose.ui.unit.IntOffset
import com.esupplemental.presentation.state.game_state.Direction
import com.esupplemental.presentation.state.game_state.DirectionChallenge
import com.esupplemental.presentation.state.game_state.DirectionStep

/**
 * Provides deterministic directions for the "Follow the Directions" game.
 * This allows pre-generating audio for instructions.
 */
object DirectionChallengeBank {

    fun challenges(title: String): List<DirectionChallenge> {
        return listOf(
            DirectionChallenge(
                id = AudioCacheKey.fromText("${title}_dir_1", "In the story \"$title\", follow these paths: 2 steps right, and finally 1 step down."),
                instructionText = "In the story \"$title\", follow these paths: 2 steps right, and finally 1 step down.",
                steps = listOf(
                    DirectionStep(Direction.RIGHT, 2),
                    DirectionStep(Direction.DOWN, 1)
                ),
                start = IntOffset(1, 1),
                target = IntOffset(3, 2)
            ),
            DirectionChallenge(
                id = AudioCacheKey.fromText("${title}_dir_2", "Help the hero! Go 3 steps left, then 2 steps up."),
                instructionText = "Help the hero! Go 3 steps left, then 2 steps up.",
                steps = listOf(
                    DirectionStep(Direction.LEFT, 3),
                    DirectionStep(Direction.UP, 2)
                ),
                start = IntOffset(4, 4),
                target = IntOffset(1, 2)
            ),
            DirectionChallenge(
                id = AudioCacheKey.fromText("${title}_dir_3", "Quickly! Move 1 step right, 2 steps down, and 1 step left."),
                instructionText = "Quickly! Move 1 step right, 2 steps down, and 1 step left.",
                steps = listOf(
                    DirectionStep(Direction.RIGHT, 1),
                    DirectionStep(Direction.DOWN, 2),
                    DirectionStep(Direction.LEFT, 1)
                ),
                start = IntOffset(2, 0),
                target = IntOffset(2, 2)
            )
        )
    }
}
