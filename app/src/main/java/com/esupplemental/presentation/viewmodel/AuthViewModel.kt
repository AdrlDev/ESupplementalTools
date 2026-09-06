package com.esupplemental.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.model.User
import com.esupplemental.domain.usecases.LoginUseCase
import com.esupplemental.domain.usecases.RegisterUseCase
import com.esupplemental.domain.utils.PasswordHasher
import com.esupplemental.domain.utils.UserPreferences
import com.esupplemental.presentation.state.AuthState
import com.esupplemental.presentation.state.LoginFormState
import com.esupplemental.presentation.state.RegistrationFormState
import kotlinx.coroutines.launch
import java.util.UUID

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val userPrefs: UserPreferences
) : ViewModel() {

    var state by mutableStateOf(AuthState())
        private set

    var formState by mutableStateOf(RegistrationFormState())
        private set

    var loginFormState by mutableStateOf(LoginFormState())
        private set

    fun onLogin(email: String, passwordHash: String) {
        if (!validateLogin(email, passwordHash)) return

        viewModelScope.launch {
            state = state.copy(isLoading = true)
            loginUseCase(email, passwordHash).onSuccess { user ->
                userPrefs.saveSession(user.id, UUID.randomUUID().toString())
                state = state.copy(user = user, isSuccess = true, isLoading = false)
            }.onFailure {
                state = state.copy(error = it.message, isLoading = false)
            }
        }
    }

    fun onRegister(name: String, email: String, passwordHash: String) {
        if (!validateForm(name, email, passwordHash)) return

        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            val userId = UUID.randomUUID().toString()
            val newUser = User(userId, name, email, PasswordHasher.hash(passwordHash))

            registerUseCase(newUser).onSuccess {
                userPrefs.saveSession(userId, UUID.randomUUID().toString())
                state = state.copy(isSuccess = true, isLoading = false)
            }.onFailure {
                state = state.copy(error = it.message, isLoading = false)
            }
        }
    }

    private fun validateLogin(email: String, pass: String): Boolean {
        val emailErr = when {
            email.isBlank() -> "Please enter your email"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
        val passErr = if (pass.isBlank()) "Please enter your password" else null

        loginFormState = LoginFormState(
            emailError = emailErr,
            passwordError = passErr,
            isValid = emailErr == null && passErr == null
        )
        return loginFormState.isValid
    }

    private fun validateForm(name: String, email: String, pass: String): Boolean {
        val nameErr = if (name.isBlank()) "Name cannot be empty" else null
        val emailErr = when {
            email.isBlank() -> "Email cannot be empty"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
        val passErr = when {
            pass.isBlank() -> "Password cannot be empty"
            pass.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        formState = RegistrationFormState(
            nameError = nameErr,
            emailError = emailErr,
            passwordError = passErr,
            isValid = nameErr == null && emailErr == null && passErr == null
        )
        return formState.isValid
    }

    fun logout() {
        viewModelScope.launch {
            userPrefs.clearSession()
        }
    }
}
