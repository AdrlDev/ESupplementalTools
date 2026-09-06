package com.esupplemental.presentation.state

data class LoginFormState(
    val emailError: String? = null,
    val passwordError: String? = null,
    val isValid: Boolean = false
)