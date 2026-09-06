package com.esupplemental.presentation.state

data class RegistrationFormState(
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isValid: Boolean = false
)