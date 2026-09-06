package com.esupplemental.domain.utils

import android.content.Context
import androidx.compose.ui.unit.IntOffset
import com.esupplemental.data.model.MediaItem
import com.esupplemental.presentation.state.game_state.Direction
import com.esupplemental.presentation.state.game_state.DirectionChallenge
import com.esupplemental.presentation.state.game_state.DirectionStep
import java.util.concurrent.ConcurrentHashMap

/**
 * Sources deterministic story-themed instructions for the Follow the Directions game.
 * Deterministic challenges ensure that background audio prefetching matches runtime cache keys.
 */
object FollowDirectionsBank {
    private val challenges = ConcurrentHashMap<String, List<DirectionChallenge>>()

    fun init(context: Context, stories: List<MediaItem>) {
        if (challenges.isNotEmpty()) return

        stories.forEach { story ->
            val storyChallenges = listOf(
                createChallenge(
                    mediaId = story.id,
                    title = story.title,
                    index = 1,
                    action = "help the hero",
                    start = IntOffset(1, 1),
                    steps = listOf(
                        DirectionStep(Direction.RIGHT, 2),
                        DirectionStep(Direction.DOWN, 2)
                    ),
                    target = IntOffset(3, 3)
                ),
                createChallenge(
                    mediaId = story.id,
                    title = story.title,
                    index = 2,
                    action = "guide the character",
                    start = IntOffset(4, 0),
                    steps = listOf(
                        DirectionStep(Direction.LEFT, 3),
                        DirectionStep(Direction.DOWN, 2)
                    ),
                    target = IntOffset(1, 2)
                ),
                createChallenge(
                    mediaId = story.id,
                    title = story.title,
                    index = 3,
                    action = "search the scene",
                    start = IntOffset(0, 4),
                    steps = listOf(
                        DirectionStep(Direction.UP, 3),
                        DirectionStep(Direction.RIGHT, 2),
                        DirectionStep(Direction.DOWN, 1)
                    ),
                    target = IntOffset(2, 2)
                ),
                createChallenge(
                    mediaId = story.id,
                    title = story.title,
                    index = 4,
                    action = "reach the destination",
                    start = IntOffset(3, 4),
                    steps = listOf(
                        DirectionStep(Direction.UP, 2),
                        DirectionStep(Direction.LEFT, 2),
                        DirectionStep(Direction.DOWN, 1)
                    ),
                    target = IntOffset(1, 3)
                )
            )
            challenges[story.id] = storyChallenges
        }
    }

    fun getChallenges(mediaId: String): List<DirectionChallenge> = 
        challenges[mediaId] ?: emptyList()

    private fun createChallenge(
        mediaId: String,
        title: String,
        index: Int,
        action: String,
        start: IntOffset,
        steps: List<DirectionStep>,
        target: IntOffset
    ): DirectionChallenge {
        val instruction = buildString {
            append("In the story \"$title\", $action! ")
            steps.forEachIndexed { i, step ->
                append("Move ${step.steps} ${if (step.steps == 1) "step" else "steps"} ${step.direction.name.lowercase()}")
                if (i < steps.lastIndex - 1) append(", ")
                else if (i == steps.lastIndex - 1) append(", and finally ")
            }
            append(".")
        }

        return DirectionChallenge(
            id = AudioCacheKey.fromText("${mediaId}_dir_v2_$index", instruction),
            instructionText = instruction,
            steps = steps,
            start = start,
            target = target
        )
    }
}

