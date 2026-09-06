package com.esupplemental.data.local.repository.impl

import com.esupplemental.data.local.dao.HomeDao
import com.esupplemental.data.local.repository.HomeRepository
import com.esupplemental.data.mapper.DataMapper.toDomainModel
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeRepositoryImpl(private val homeDao: HomeDao) : HomeRepository {
    override fun getUserStats(userId: String): Flow<UserStats> =
        homeDao.getUserStats(userId).map { it?.toDomainModel() ?: UserStats() }

    override fun getMediaByType(type: MediaType): Flow<List<MediaItem>> =
        homeDao.getMediaByType(type.name).map { entities ->
            // Map each entity in the list to its domain model
            entities.map { it.toDomainModel() }
        }
}