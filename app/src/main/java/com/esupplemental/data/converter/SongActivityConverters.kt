package com.esupplemental.data.converter

import androidx.room.TypeConverter
import com.esupplemental.data.model.FillBlankItem
import com.esupplemental.data.model.MultipleChoiceItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SongActivityConverters {
    private val gson = Gson()

    // Converter for List<FillBlankItem>
    @TypeConverter
    fun fromFillBlankList(value: List<FillBlankItem>?): String {
        val type = object : TypeToken<List<FillBlankItem>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toFillBlankList(value: String): List<FillBlankItem> {
        val type = object : TypeToken<List<FillBlankItem>>() {}.type
        return gson.fromJson(value, type)
    }

    // Converter for MultipleChoiceItem (Single Object)
    @TypeConverter
    fun fromMultipleChoiceItem(value: MultipleChoiceItem?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMultipleChoiceItem(value: String): MultipleChoiceItem {
        return gson.fromJson(value, MultipleChoiceItem::class.java)
    }
}