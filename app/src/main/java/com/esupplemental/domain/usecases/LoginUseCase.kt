package com.esupplemental.domain.usecases

import com.esupplemental.data.local.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, passwordHash: String) =
        repository.loginUser(email, passwordHash)
}