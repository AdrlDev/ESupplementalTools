package com.esupplemental.data.converter

import androidx.room.TypeConverter
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.StoryCategory

class MediaConverters {
    @TypeConverter
    fun fromMediaType(value: MediaType): String {
        return value.name
    }

    @TypeConverter
    fun toMediaType(value: String): MediaType {
        return MediaType.valueOf(value)
    }

    @TypeConverter
    fun fromMediaCategory(value: StoryCategory): String {
        return value.name
    }

    @TypeConverter
    fun toMediaCategory(value: String): StoryCategory {
        return StoryCategory.valueOf(value)
    }
}