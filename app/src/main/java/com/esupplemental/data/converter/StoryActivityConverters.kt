package com.esupplemental.data.converter

import androidx.room.TypeConverter
import com.esupplemental.data.model.OpenEndedQuestion
import com.esupplemental.data.model.game.StoryEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StoryActivityConverters {
    private val gson = Gson()

    // Converter for List<StoryEvent>
    @TypeConverter
    fun fromStoryEventList(value: List<StoryEvent>?): String {
        val type = object : TypeToken<List<StoryEvent>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toStoryEventList(value: String): List<StoryEvent> {
        val type = object : TypeToken<List<StoryEvent>>() {}.type
        return gson.fromJson(value, type)
    }

    // Converter for List<OpenEndedQuestion>
    @TypeConverter
    fun fromOpenEndedList(value: List<OpenEndedQuestion>?): String {
        val type = object : TypeToken<List<OpenEndedQuestion>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toOpenEndedList(value: String): List<OpenEndedQuestion> {
        val type = object : TypeToken<List<OpenEndedQuestion>>() {}.type
        return gson.fromJson(value, type)
    }
}