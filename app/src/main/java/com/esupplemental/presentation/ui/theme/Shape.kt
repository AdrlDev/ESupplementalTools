package com.esupplemental.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material 3 Shape scale.
 * These are used by Material components like Cards, Buttons, and Dialogs.
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * Custom shapes for specific "Playful" elements that don't fit the standard scale.
 */
object PlayfulShapes {
    val StickyNote = RoundedCornerShape(2.dp)
    val GameCard = RoundedCornerShape(20.dp)
    val ActionButton = RoundedCornerShape(14.dp)
}
