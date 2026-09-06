package com.esupplemental.data.local.repository

import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.UserStats
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getUserStats(userId: String): Flow<UserStats>
    fun getMediaByType(type: MediaType): Flow<List<MediaItem>>
}