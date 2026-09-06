package com.esupplemental.data.local.dao

import androidx.room.*
import com.esupplemental.data.local.entity.UserEntity
import com.esupplemental.data.local.entity.UserStatsEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Add this to handle stats
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStatsEntity)

    // Helper transaction to ensure both happen together
    @Transaction
    suspend fun registerUserWithStats(user: UserEntity, stats: UserStatsEntity) {
        insertUser(user)
        insertUserStats(stats)
    }

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("UPDATE users SET name = :newName WHERE id = :userId")
    suspend fun updateUserName(userId: String, newName: String)

    @Query("UPDATE user_stats SET userName = :newName WHERE userId = :userId")
    suspend fun updateUserStatsName(userId: String, newName: String)

    @Transaction
    suspend fun updateProfileName(userId: String, newName: String) {
        updateUserName(userId, newName)
        updateUserStatsName(userId, newName)
    }

    @Query("UPDATE users SET passwordHash = :passwordHash WHERE id = :userId")
    suspend fun updateUserPassword(userId: String, passwordHash: String)
}