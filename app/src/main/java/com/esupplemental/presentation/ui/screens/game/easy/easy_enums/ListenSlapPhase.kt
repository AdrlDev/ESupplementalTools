package com.esupplemental.presentation.ui.screens.game.easy.easy_enums

enum class ListenSlapPhase {
    IDLE,       // Start screen
    SPEAKING,   // TTS is reading the word — cards disabled
    SELECTING,  // Timer running — user can tap
    CORRECT,    // Brief green flash before next round
    WRONG,      // Brief red flash before next round
    GAME_OVER
}