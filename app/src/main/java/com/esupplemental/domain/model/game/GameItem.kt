package com.esupplemental.domain.model.game

import androidx.compose.ui.graphics.vector.ImageVector

data class GameItem(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val description: String,
    val skillTags: List<GameSkillTag>,
    val difficulty: GameDifficulty,
    val stars: Int = 0,
    val isLocked: Boolean = false,
    val xpReward: Int = 0
)
