package com.esupplemental.data.local.repository.impl

import com.esupplemental.data.local.dao.UserDao
import com.esupplemental.data.local.entity.UserStatsEntity
import com.esupplemental.data.local.repository.AuthRepository
import com.esupplemental.data.mapper.DataMapper.toDomainModel
import com.esupplemental.data.mapper.DataMapper.toEntity
import com.esupplemental.data.model.User
import com.esupplemental.domain.utils.PasswordHasher

class AuthRepositoryImpl(private val userDao: UserDao) : AuthRepository {
    override suspend fun registerUser(user: User): Result<Unit> = runCatching {
        val userEntity = user.toEntity()

        // Create initial stats for the new user
        val initialStats = UserStatsEntity(
            userId = user.id,
            userName = user.name,
            level = 1,
            levelProgress = 0f,
            songsCompleted = 0,
            storiesCompleted = 0,
            quizzesAverage = 0f,
            overallScore = 0f
        )

        userDao.registerUserWithStats(userEntity, initialStats)
    }

    override suspend fun loginUser(email: String, passwordHash: String): Result<User> = runCatching {
        val entity = userDao.getUserByEmail(email)
        if (entity != null && PasswordHasher.verify(passwordHash, entity.passwordHash)) {
            entity.toDomainModel()
        } else {
            throw Exception("Invalid credentials")
        }
    }

    override suspend fun getCurrentUser(userId: String): Result<User> = runCatching {
        userDao.getUserById(userId)?.toDomainModel() ?: throw Exception("User not found")
    }

    override suspend fun updateProfileName(userId: String, newName: String): Result<Unit> = runCatching {
        if (newName.isBlank()) throw Exception("Name cannot be empty")
        userDao.updateProfileName(userId, newName.trim())
    }

    override suspend fun updatePassword(
        userId: String,
        oldPassword: String,
        newPassword: String
    ): Result<Unit> = runCatching {
        val user = userDao.getUserById(userId) ?: throw Exception("User not found")
        if (!PasswordHasher.verify(oldPassword, user.passwordHash)) {
            throw Exception("Current password is incorrect")
        }
        if (newPassword.length < 6) {
            throw Exception("New password must be at least 6 characters")
        }
        val newHash = PasswordHasher.hash(newPassword)
        userDao.updateUserPassword(userId, newHash)
    }
}