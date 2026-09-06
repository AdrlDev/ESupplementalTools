package com.esupplemental.presentation.state.game_state

import com.esupplemental.data.model.game.StoryEvent
import com.esupplemental.data.model.game.StoryQuestion
import com.esupplemental.domain.model.media.StoryChunk
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.CardCheckState
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.StoryOrderPhase

data class StoryOrderUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val phase: StoryOrderPhase = StoryOrderPhase.LISTENING,
    val currentStory: StoryQuestion? = null,
    val userOrderedEvents: List<StoryEvent> = emptyList(),
    val cardCheckStates: Map<String, CardCheckState> = emptyMap(),
    val isAudioPlaying: Boolean = false,
    val isGeneratingAudio: Boolean = false,
    val isAudioBuffering: Boolean = false,
    val captionParagraphs: List<String> = emptyList(),
    val transcriptChunks: List<StoryChunk> = emptyList(),
    val currentCaptionIndex: Int = 0,
    val currentWordIndex: Int = -1,
    val hasListened: Boolean = false,
    val questionIndex: Int = 0,
    val totalQuestions: Int = 5,
    val totalPossibleEvents: Int = 0,
    val correctCountThisRound: Int = 0,
    val totalCorrect: Int = 0,
    val xpEarned: Int = 0
) {
    val isAudioLoading: Boolean get() = isGeneratingAudio || isAudioBuffering
    val currentCaption: String?
        get() = captionParagraphs.getOrNull(currentCaptionIndex)
    val currentChunk: StoryChunk?
        get() = transcriptChunks.getOrNull(currentCaptionIndex)
}
