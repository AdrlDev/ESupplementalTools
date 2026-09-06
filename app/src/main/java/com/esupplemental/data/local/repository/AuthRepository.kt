package com.esupplemental.data.local.repository

import com.esupplemental.data.model.User

interface AuthRepository {
    suspend fun registerUser(user: User): Result<Unit>
    suspend fun loginUser(email: String, passwordHash: String): Result<User>
    suspend fun getCurrentUser(userId: String): Result<User>
    suspend fun updateProfileName(userId: String, newName: String): Result<Unit>
    suspend fun updatePassword(userId: String, oldPassword: String, newPassword: String): Result<Unit>
}