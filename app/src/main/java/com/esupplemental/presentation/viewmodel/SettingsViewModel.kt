package com.esupplemental.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.AuthRepository
import com.esupplemental.data.model.User
import com.esupplemental.domain.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val currentUser: User? = null,
    val isLoadingUser: Boolean = true,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class SettingsViewModel(
    private val userPrefs: UserPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val darkModeSetting: StateFlow<String> = userPrefs.darkModeSetting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val themeColorSetting: StateFlow<String> = userPrefs.themeColorSetting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "navy")

    val soundEffectsSetting: StateFlow<Boolean> = userPrefs.soundEffectsSetting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true, errorMessage = null) }
            userPrefs.userId.collect { userId ->
                if (userId != null) {
                    authRepository.getCurrentUser(userId).onSuccess { user ->
                        _uiState.update { it.copy(currentUser = user, isLoadingUser = false) }
                    }.onFailure { err ->
                        _uiState.update { it.copy(isLoadingUser = false, errorMessage = err.message) }
                    }
                } else {
                    _uiState.update { it.copy(currentUser = null, isLoadingUser = false) }
                }
            }
        }
    }

    fun setDarkMode(mode: String) {
        viewModelScope.launch {
            userPrefs.setDarkModeSetting(mode)
        }
    }

    fun setThemeColor(colorKey: String) {
        viewModelScope.launch {
            userPrefs.setThemeColorSetting(colorKey)
        }
    }

    fun setSoundEffects(enabled: Boolean) {
        viewModelScope.launch {
            userPrefs.setSoundEffectsSetting(enabled)
        }
    }

    fun updateUsername(newName: String) {
        val user = _uiState.value.currentUser ?: return
        if (newName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Username cannot be empty") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, successMessage = null) }
            authRepository.updateProfileName(user.id, newName).onSuccess {
                _uiState.update {
                    it.copy(
                        currentUser = user.copy(name = newName.trim()),
                        isUpdating = false,
                        successMessage = "Username updated successfully!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(isUpdating = false, errorMessage = err.message ?: "Failed to update username")
                }
            }
        }
    }

    fun updatePassword(oldPass: String, newPass: String) {
        val user = _uiState.value.currentUser ?: return
        if (oldPass.isBlank() || newPass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all password fields") }
            return
        }
        if (newPass.length < 6) {
            _uiState.update { it.copy(errorMessage = "New password must be at least 6 characters") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, successMessage = null) }
            authRepository.updatePassword(user.id, oldPass, newPass).onSuccess {
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        successMessage = "Password changed successfully!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(isUpdating = false, errorMessage = err.message ?: "Failed to change password")
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
