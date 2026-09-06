package com.esupplemental.domain.usecases.media

import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaItem

class GetMediaDetailUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(id: String): MediaItem? {
        return repository.getMediaDetail(id)
    }
}
