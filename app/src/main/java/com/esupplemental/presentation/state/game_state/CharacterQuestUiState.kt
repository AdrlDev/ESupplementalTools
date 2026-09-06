package com.esupplemental.presentation.state.game_state

import com.esupplemental.domain.utils.CharacterQuestQuestion
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.CharacterQuestPhase

data class CharacterQuestUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val phase: CharacterQuestPhase = CharacterQuestPhase.LISTENING,
    val questions: List<CharacterQuestQuestion> = emptyList(),
    val questionIndex: Int = 0,
    val totalQuestions: Int = 5,
    val currentQuestion: CharacterQuestQuestion? = null,
    val selectedCharacterIndex: Int? = null,
    val isCorrect: Boolean? = null,
    val isAudioPlaying: Boolean = false,
    val isGeneratingAudio: Boolean = false,
    val isAudioBuffering: Boolean = false,
    val score: Int = 0,
    val xpEarned: Int = 0
) {
    val isAudioLoading: Boolean get() = isGeneratingAudio || isAudioBuffering
}
