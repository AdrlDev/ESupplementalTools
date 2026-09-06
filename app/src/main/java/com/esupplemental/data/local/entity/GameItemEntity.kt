package com.esupplemental.data.local.entity

import androidx.room.Entity
import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.domain.model.game.GameSkillTag

@Entity(tableName = "games", primaryKeys = ["userId", "id"])
data class GameItemEntity(
    val userId: String,
    val id: String,
    val iconKey: String,
    val title: String,
    val description: String,
    val skillTags: List<GameSkillTag>, // Handled by TypeConverter
    val difficulty: GameDifficulty,     // Handled by TypeConverter
    val stars: Int,
    val isLocked: Boolean,
    val xpReward: Int
)
