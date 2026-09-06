package com.esupplemental.presentation.ui.screens.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.domain.utils.GameScoring
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.BrandText
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.RewardTextDark
import com.esupplemental.presentation.ui.theme.RewardTextLight
import com.esupplemental.presentation.ui.theme.StarGold
import com.esupplemental.presentation.ui.theme.SuccessGreen
import com.esupplemental.presentation.ui.theme.SuccessGreenDark
import com.esupplemental.presentation.ui.theme.WarningTextDark
import com.esupplemental.presentation.ui.theme.WarningTextLight
import com.esupplemental.presentation.ui.theme.spacing
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Immutable
data class GameSemanticColors(
    val success: Color,
    val warning: Color,
    val danger: Color,
    val xp: Color,
    val star: Color,
    val locked: Color,
    val raisedSurface: Color
)

@Composable
fun rememberGameSemanticColors(): GameSemanticColors {
    val scheme = MaterialTheme.colorScheme
    return remember(scheme) {
        val isDark = scheme.background.luminance() < 0.5f
        GameSemanticColors(
            success = if (isDark) SuccessGreenDark else SuccessGreen,
            warning = if (isDark) WarningTextDark else WarningTextLight,
            danger = scheme.error,
            xp = if (isDark) RewardTextDark else RewardTextLight,
            star = if (isDark) RewardTextDark else RewardTextLight,
            locked = scheme.onSurfaceVariant.copy(alpha = 0.55f),
            raisedSurface = scheme.surfaceVariant.copy(alpha = 0.72f)
        )
    }
}

enum class GameAnswerState { DEFAULT, SELECTED, CORRECT, INCORRECT }
enum class GameFeedbackType { SUCCESS, ENCOURAGEMENT, INFO }

@Composable
fun GameTopBar(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    currentStep: Int? = null,
    totalSteps: Int? = null,
    score: Int? = null,
    xp: Int? = null
) {
    val scheme = MaterialTheme.colorScheme
    val spacing = MaterialTheme.spacing
    val progress = if (currentStep != null && totalSteps != null && totalSteps > 0) {
        currentStep.toFloat() / totalSteps
    } else null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.14f), scheme.surface.copy(alpha = 0.92f))
                )
            )
            .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(26.dp))
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                Surface(
                    shape = CircleShape,
                    color = scheme.surface.copy(alpha = 0.8f),
                    tonalElevation = 2.dp
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back to games")
                    }
                }
                Spacer(Modifier.width(spacing.small))
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = readableContentColor(accent), modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(spacing.small))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = scheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (score != null || (xp != null && xp > 0)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (score != null) GameMetricBadge("$score", "SCORE", accent)
                if (xp != null && xp > 0) {
                    Spacer(Modifier.width(6.dp))
                    GameMetricBadge("+$xp", "XP", ArcadeColors.Reward)
                }
            }
        }

        if (progress != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "ROUND $currentStep / $totalSteps",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = scheme.onSurfaceVariant
                )
                Spacer(Modifier.width(spacing.small))
                GameProgressBar(progress, accent, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun GameProgressBar(
    progress: Float,
    accent: Color,
    modifier: Modifier = Modifier,
    height: Int = 10
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "game_progress"
    )
    Box(
        modifier = modifier
            .height(height.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.09f))
    ) {
        Box(
            Modifier
                .fillMaxWidth(animated)
                .height(height.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.75f), accent)))
        )
    }
}

@Composable
fun GameTimer(
    remainingMs: Long,
    totalMs: Long,
    modifier: Modifier = Modifier
) {
    val colors = rememberGameSemanticColors()
    val fraction = if (totalMs > 0) (remainingMs.toFloat() / totalMs).coerceIn(0f, 1f) else 0f
    val targetColor = when {
        fraction <= 0.1f -> colors.danger
        fraction <= 0.3f -> colors.warning
        else -> MaterialTheme.colorScheme.primary
    }
    val color by animateColorAsState(targetColor, tween(350), label = "timer_color")
    val seconds = ((remainingMs + 999) / 1000).coerceAtLeast(0)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = color.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.28f))
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Schedule, "Time remaining", tint = color, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(7.dp))
                Text("TIME", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.weight(1f))
                AnimatedContent(seconds, label = "timer_seconds") { value ->
                    Text("00:${value.toString().padStart(2, '0')}", fontWeight = FontWeight.Black, color = color)
                }
            }
            GameProgressBar(fraction, color)
        }
    }
}

@Composable
fun GamePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    accent: Color = MaterialTheme.colorScheme.primary
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed && enabled) 0.96f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "game_button_press"
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = accent,
            contentColor = readableContentColor(accent)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 5.dp,
            pressedElevation = 1.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (icon != null) {
            Icon(icon, null, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
    }
}

@Composable
fun GameSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accent: Color = MaterialTheme.colorScheme.primary
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, accent.copy(alpha = 0.45f))
    ) {
        if (icon != null) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = accent, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GameAnswerOption(
    label: String,
    text: String,
    state: GameAnswerState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accent: Color = MaterialTheme.colorScheme.primary
) {
    val scheme = MaterialTheme.colorScheme
    val semantic = rememberGameSemanticColors()
    val target = when (state) {
        GameAnswerState.DEFAULT -> scheme.surface
        GameAnswerState.SELECTED -> accent.copy(alpha = 0.15f).compositeOver(scheme.surface)
        GameAnswerState.CORRECT -> semantic.success.copy(alpha = 0.16f).compositeOver(scheme.surface)
        GameAnswerState.INCORRECT -> semantic.danger.copy(alpha = 0.13f).compositeOver(scheme.surface)
    }
    val border = when (state) {
        GameAnswerState.DEFAULT -> MaterialTheme.colorScheme.outlineVariant
        GameAnswerState.SELECTED -> accent
        GameAnswerState.CORRECT -> semantic.success
        GameAnswerState.INCORRECT -> semantic.danger
    }
    val background by animateColorAsState(target, tween(220), label = "answer_background")
    val borderColor by animateColorAsState(border, tween(220), label = "answer_border")
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, spring(), label = "answer_press")

    Surface(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        modifier = modifier.fillMaxWidth().scale(scale).semantics { role = Role.Button },
        shape = RoundedCornerShape(18.dp),
        color = background,
        border = BorderStroke(if (state == GameAnswerState.DEFAULT) 1.dp else 2.dp, borderColor),
        shadowElevation = if (state == GameAnswerState.DEFAULT) 1.dp else 3.dp
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 76.dp)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = borderColor.copy(alpha = 0.16f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(label, color = borderColor, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                color = scheme.onSurface,
                fontWeight = FontWeight.Bold,
                softWrap = true,
                overflow = TextOverflow.Visible
            )
            if (state == GameAnswerState.CORRECT) Icon(Icons.Rounded.CheckCircle, "Correct", tint = semantic.success)
            if (state == GameAnswerState.INCORRECT) Icon(Icons.Rounded.WarningAmber, "Incorrect", tint = semantic.danger)
        }
    }
}

@Composable
fun GameFeedbackBanner(
    title: String,
    message: String,
    type: GameFeedbackType,
    modifier: Modifier = Modifier,
    xp: Int? = null
) {
    val semantic = rememberGameSemanticColors()
    val color = when (type) {
        GameFeedbackType.SUCCESS -> semantic.success
        GameFeedbackType.ENCOURAGEMENT -> semantic.warning
        GameFeedbackType.INFO -> MaterialTheme.colorScheme.primary
    }
    val icon = when (type) {
        GameFeedbackType.SUCCESS -> Icons.Rounded.CheckCircle
        GameFeedbackType.ENCOURAGEMENT -> Icons.Rounded.WarningAmber
        GameFeedbackType.INFO -> Icons.Rounded.Star
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.28f))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(34.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = color)
                Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (xp != null && xp > 0) GameMetricBadge("+$xp", "XP", semantic.xp)
        }
    }
}

@Composable
fun GameStarRating(stars: Int, modifier: Modifier = Modifier, animated: Boolean = true) {
    var visible by remember(stars) { mutableIntStateOf(if (animated) 0 else stars) }
    LaunchedEffect(stars, animated) {
        if (animated) {
            visible = 0
            repeat(stars.coerceIn(0, 3)) {
                delay(180.milliseconds)
                visible += 1
            }
        }
    }
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            val earned = index < visible
            val scale by animateFloatAsState(if (earned) 1f else 0.84f, spring(Spring.DampingRatioMediumBouncy), label = "star_$index")
            Icon(
                if (earned) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                if (earned) "Earned star" else "Empty star",
                tint = if (earned) StarGold else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(42.dp).scale(scale)
            )
        }
    }
}

@Composable
fun GameResultScreen(
    score: Int,
    total: Int,
    xp: Int,
    onPlayAgain: () -> Unit,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    accent: Color = MaterialTheme.colorScheme.primary
) {
    val displayTotal = total.coerceAtLeast(0)
    val displayScore = score.coerceIn(0, displayTotal)
    val displayXp = xp.coerceAtLeast(0)
    val percentage = GameScoring.percentage(score, total)
    val stars = GameScoring.stars(score, total)
    val heading = title ?: when {
        percentage == 100 -> "Challenge mastered!"
        percentage >= 70 -> "Great work!"
        percentage >= 40 -> "Nice progress!"
        else -> "Good try—keep going!"
    }
    val support = subtitle ?: when {
        percentage == 100 -> "A perfect run. That practice paid off!"
        percentage >= 70 -> "You are getting stronger every round."
        percentage >= 40 -> "One more try could earn another star."
        else -> "Every attempt trains your brain. Ready again?"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        AnimatedVisibility(true, enter = fadeIn(tween(350)) + scaleIn(initialScale = 0.8f)) {
            Surface(shape = CircleShape, color = accent.copy(alpha = 0.14f), border = BorderStroke(1.dp, accent.copy(alpha = 0.3f))) {
                Icon(Icons.Rounded.EmojiEvents, heading, tint = accent, modifier = Modifier.padding(22.dp).size(58.dp))
            }
        }
        Text(heading, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black), textAlign = TextAlign.Center)
        Text(support, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        GameStarRating(stars)

        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                ResultMetric("$displayScore / $displayTotal", "CORRECT", accent)
                ResultMetric("$percentage%", "ACCURACY", ArcadeColors.Teal)
                ResultMetric("+$displayXp", "XP", ArcadeColors.Reward)
            }
        }

        GamePrimaryButton("PLAY AGAIN", onPlayAgain, icon = Icons.Rounded.Refresh, accent = accent)
        if (onBack != null) GameSecondaryButton("BACK TO GAMES", onBack, icon = Icons.AutoMirrored.Rounded.ArrowBack, accent = accent)
    }
}

@Composable
fun GameLoadingState(message: String, modifier: Modifier = Modifier, accent: Color = MaterialTheme.colorScheme.primary) {
    Column(modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CircularProgressIndicator(color = accent, strokeWidth = 4.dp)
        Text(message, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Text("Preparing your next challenge…", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun GameLockedState(message: String, modifier: Modifier = Modifier) {
    val semantic = rememberGameSemanticColors()
    Column(modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(shape = CircleShape, color = semantic.locked.copy(alpha = 0.12f)) {
            Icon(Icons.Rounded.Lock, "Locked", tint = semantic.locked, modifier = Modifier.padding(18.dp).size(38.dp))
        }
        Text("More adventures are waiting!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun GameMetricBadge(value: String, label: String, color: Color) {
    val scheme = MaterialTheme.colorScheme
    val readableColor = readableForegroundColor(color, scheme.surface)
    Surface(shape = RoundedCornerShape(13.dp), color = color.copy(alpha = 0.15f)) {
        Column(Modifier.padding(horizontal = 9.dp, vertical = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = readableColor)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ResultMetric(value: String, label: String, color: Color) {
    val scheme = MaterialTheme.colorScheme
    val readableColor = readableForegroundColor(color, scheme.surfaceVariant)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = readableColor)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun readableContentColor(background: Color): Color =
    if (contrastRatio(BrandText, background) >= contrastRatio(Color.White, background)) {
        BrandText
    } else {
        Color.White
    }

@Composable
private fun readableForegroundColor(preferred: Color, background: Color): Color {
    val fallback = MaterialTheme.colorScheme.onSurface
    return if (contrastRatio(preferred, background) >= 4.5f) preferred else fallback
}

private fun contrastRatio(foreground: Color, background: Color): Float {
    val lighter = maxOf(foreground.luminance(), background.luminance())
    val darker = minOf(foreground.luminance(), background.luminance())
    return (lighter + 0.05f) / (darker + 0.05f)
}

@Preview(showBackground = true, name = "Game UI Kit")
@Composable
private fun GameUiKitPreview() {
    ESupplementalTheme(dynamicColor = false) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GameTopBar("Memory Quest", "Listen, remember, and win stars", Icons.Rounded.Star, ArcadeColors.Navy, {}, currentStep = 3, totalSteps = 5, score = 2, xp = 6)
            GameTimer(6_000, 20_000)
            GameAnswerOption("A", "The bright green forest", GameAnswerState.SELECTED, {})
            GameFeedbackBanner("Awesome!", "You found the right answer.", GameFeedbackType.SUCCESS, xp = 3)
        }
    }
}

@Preview(showBackground = true, name = "Result - Dark")
@Composable
private fun GameResultPreview() {
    ESupplementalTheme(darkTheme = true, dynamicColor = false) {
        GameResultScreen(8, 10, 12, {}, {}, Modifier.padding(18.dp), accent = ArcadeColors.Navy)
    }
}

@Preview(showBackground = true, name = "Answer and Feedback States")
@Composable
private fun GameStatesPreview() {
    ESupplementalTheme(dynamicColor = false) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            GameProgressBar(0.68f, ArcadeColors.Teal)
            GameAnswerOption("A", "Ready to choose", GameAnswerState.DEFAULT, {})
            GameAnswerOption("B", "My selected answer", GameAnswerState.SELECTED, {})
            GameAnswerOption("C", "That is correct", GameAnswerState.CORRECT, {})
            GameAnswerOption("D", "Good try—look again", GameAnswerState.INCORRECT, {})
            GameFeedbackBanner("Almost!", "Use the clue and try the next one.", GameFeedbackType.ENCOURAGEMENT)
        }
    }
}

@Preview(showBackground = true, name = "Locked State - Dark")
@Composable
private fun GameLockedPreview() {
    ESupplementalTheme(darkTheme = true, dynamicColor = false) {
        GameLockedState("Complete the previous challenge to unlock this adventure.")
    }
}
