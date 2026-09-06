package com.esupplemental.presentation.ui.screens.game.easy.word_master

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.esupplemental.presentation.ui.screens.game.GameResultScreen
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

/** Shared result presentation used by Word Master, Minimal Pairs, and 2 Truths 1 Lie. */
@Composable
fun GameOverScreen(
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
        accent = ArcadeColors.Navy
    )
}

@Preview(showBackground = true)
@Composable
private fun GameOverScreenPreview() {
    ESupplementalTheme(dynamicColor = false) {
        GameOverScreen(8, 10, 12, {}, {})
    }
}
