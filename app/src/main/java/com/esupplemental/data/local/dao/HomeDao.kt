package com.esupplemental.data.local.dao

import androidx.room.*
import com.esupplemental.data.local.entity.MediaItemEntity
import com.esupplemental.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {
    @Query("SELECT * FROM user_stats WHERE userId = :userId LIMIT 1")
    fun getUserStats(userId: String): Flow<UserStatsEntity?>

    @Query("SELECT * FROM media_items WHERE type = :type")
    fun getMediaByType(type: String): Flow<List<MediaItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStats(stats: UserStatsEntity)
}