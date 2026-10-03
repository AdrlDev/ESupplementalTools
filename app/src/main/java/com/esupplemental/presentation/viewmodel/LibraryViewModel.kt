package com.esupplemental.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.domain.usecases.media.GetMediaItemsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Owns only Library list state. Keeping playback out of this ViewModel avoids constructing
 * ExoPlayer merely because the user opened the Library screen.
 */
class LibraryViewModel(
    private val getMediaItemsUseCase: GetMediaItemsUseCase
) : ViewModel() {

    private val selectedMediaType = MutableStateFlow<MediaType?>(MediaType.SONG)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedMedia: Flow<PagingData<MediaItem>> = selectedMediaType
        .flatMapLatest { mediaType ->
            mediaType?.let(getMediaItemsUseCase::getPaged)
                ?: flowOf(PagingData.empty<MediaItem>())
        }
        .cachedIn(viewModelScope)

    fun selectPage(page: Int) {
        selectedMediaType.value = when (page) {
            0 -> MediaType.SONG
            1 -> MediaType.STORY
            2 -> MediaType.POEM
            else -> null
        }
    }
}
