package com.esupplemental.presentation.state

import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.UserStats

data class HomeUiState(
    val userStats: UserStats = UserStats(),
    val songs: List<MediaItem> = emptyList(),
    val stories: List<MediaItem> = emptyList()
)