package com.esupplemental.presentation.ui.screens.game.moderate.disappearing_text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.esupplemental.presentation.ui.screens.game.GameResultScreen
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun DisappearingTextGameOverScreen(
    score: Int,
    total: Int,
    xp: Int,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    GameResultScreen(
        score = score,
        total = total,
        xp = xp,
        onPlayAgain = onPlayAgain,
        onBack = onBack,
        modifier = modifier,
        title = "Memory mission complete!",
        accent = ArcadeColors.Navy
    )
}

@Preview(showBackground = true)
@Composable
private fun DisappearingTextResultPreview() {
    ESupplementalTheme(dynamicColor = false) {
        DisappearingTextGameOverScreen(4, 5, 12, {}, {})
    }
}
