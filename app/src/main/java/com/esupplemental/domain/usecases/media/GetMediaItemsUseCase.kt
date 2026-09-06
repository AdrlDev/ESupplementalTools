package com.esupplemental.domain.usecases.media

import androidx.paging.PagingData
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMediaItemsUseCase(private val repository: MediaRepository) {
    fun getPaged(type: MediaType): Flow<PagingData<MediaItem>> {
        return repository.getMediaListPaged(type)
    }

    fun getFiltered(type: MediaType, query: String): Flow<List<MediaItem>> {
        return repository.getMediaList(type).map { list ->
            if (query.isBlank()) {
                list
            } else {
                list.filter {
                    val cat = it.category?.name ?: it.singer ?: ""
                    it.title.contains(query, ignoreCase = true) ||
                            cat.contains(query, ignoreCase = true)
                }
            }
        }
    }
}
