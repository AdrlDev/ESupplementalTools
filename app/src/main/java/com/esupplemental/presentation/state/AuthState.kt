package com.esupplemental.presentation.state

import com.esupplemental.data.model.User

data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)