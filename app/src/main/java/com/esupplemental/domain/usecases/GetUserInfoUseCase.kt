package com.esupplemental.domain.usecases

import com.esupplemental.data.local.repository.AuthRepository

class GetUserInfoUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: String) = repository.getCurrentUser(userId)
}