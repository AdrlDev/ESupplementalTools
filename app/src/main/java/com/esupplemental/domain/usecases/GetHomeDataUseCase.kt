package com.esupplemental.domain.usecases

import com.esupplemental.data.local.repository.HomeRepository
import com.esupplemental.data.model.MediaType
import com.esupplemental.presentation.state.HomeUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetHomeDataUseCase(private val repository: HomeRepository) {
    operator fun invoke(userId: String): Flow<HomeUiState> {
        return combine(
            repository.getUserStats(userId),
            repository.getMediaByType(MediaType.SONG),
            repository.getMediaByType(MediaType.STORY)
        ) { stats, songs, stories ->
            HomeUiState(
                userStats = stats,
                songs = songs,
                stories = stories
            )
        }
    }
}