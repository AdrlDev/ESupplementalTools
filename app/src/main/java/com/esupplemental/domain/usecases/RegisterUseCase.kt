package com.esupplemental.domain.usecases

import com.esupplemental.data.local.repository.AuthRepository
import com.esupplemental.data.model.User

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(user: User) = repository.registerUser(user)
}