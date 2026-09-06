package com.esupplemental.data.converter

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.TypeConverter
import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.domain.model.game.GameSkillTag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GameConverters {
    private val gson = Gson()

    // --- Difficulty Enum ---
    @TypeConverter
    fun fromDifficulty(difficulty: GameDifficulty): String = difficulty.name

    @TypeConverter
    fun toDifficulty(value: String): GameDifficulty = GameDifficulty.valueOf(value)

    // --- Skill Tags List (using Gson) ---
    @TypeConverter
    fun fromSkillTagList(tags: List<GameSkillTag>?): String? {
        return gson.toJson(tags)
    }

    @TypeConverter
    fun toSkillTagList(value: String?): List<GameSkillTag>? {
        if (value == null) return emptyList()

        val listType = object : TypeToken<List<GameSkillTag>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromImageVector(vector: ImageVector?): String? {
        return gson.toJson(vector)
    }

    @TypeConverter
    fun toImageVector(value: String?): ImageVector? {
        if (value == null) return null
        return try {
            gson.fromJson(value, ImageVector::class.java)
        } catch (e: Exception) {
            null
        }
    }
}