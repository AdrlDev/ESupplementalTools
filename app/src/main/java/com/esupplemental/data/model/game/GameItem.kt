package com.esupplemental.data.model.game

import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.domain.model.game.GameSkillTag

/**
 * Single game entry shown on the Games screen.
 *
 * @param id           Unique stable identifier used as nav argument.
 * @param emoji        Large decorative emoji shown on the card.
 * @param title        Short game name.
 * @param description  One-sentence explanation of how to play.
 * @param skillTags    Up to 2 skill chips shown on the card.
 * @param difficulty   Which level bucket this game belongs to.
 * @param stars        Difficulty sub-rating within the level (1–3).
 * @param isLocked     Whether the game requires prior progress to unlock.
 * @param xpReward     XP shown on the card as motivation.
 */
data class GameItem(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val skillTags: List<GameSkillTag>,
    val difficulty: GameDifficulty,
    val stars: Int = 0,
    val isLocked: Boolean = false,
    val xpReward: Int = 0
)