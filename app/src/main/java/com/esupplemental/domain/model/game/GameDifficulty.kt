package com.esupplemental.domain.model.game

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.ui.graphics.vector.ImageVector

enum class GameDifficulty(val label: String, val emoji: ImageVector) {
    EASY("Easy", Icons.Rounded.SentimentSatisfied),
    MODERATE("Moderate", Icons.Rounded.Speed),
    HARD("Hard", Icons.Rounded.LocalFireDepartment)
}
