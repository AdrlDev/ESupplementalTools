package com.esupplemental.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.domain.usecases.GetHomeDataUseCase
import com.esupplemental.domain.utils.UserPreferences
import com.esupplemental.presentation.state.HomeUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Initialize with an empty state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadHomeData() }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadHomeData() {
        viewModelScope.launch {
            // 1. Listen to userId changes from DataStore
            userPreferences.userId.flatMapLatest { id ->
                if (id != null) {
                    // 2. If we have an ID, observe the actual home data
                    getHomeDataUseCase(id)
                } else {
                    // 3. If no ID (logged out), return empty state
                    flowOf(HomeUiState())
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}
