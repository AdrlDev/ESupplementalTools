package com.esupplemental.presentation.ui.screens.game.easy.word_master

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Spellcheck
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.data.model.game.WordItem
import com.esupplemental.presentation.state.game_state.ListenSlapUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.GameFeedbackBanner
import com.esupplemental.presentation.ui.screens.game.GameFeedbackType
import com.esupplemental.presentation.ui.screens.game.GamePrimaryButton
import com.esupplemental.presentation.ui.screens.game.GameTimer
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.ListenSlapPhase
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing

@Composable
fun GamePlayScreen(
    state: ListenSlapUiState,
    onTypedWordChange: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onReplay: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val storyTitle = state.currentWord?.sourceTitle

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Story Context Reference Header ---
        if (!storyTitle.isNullOrBlank()) {
            Surface(
                color = ArcadeColors.Navy.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ArcadeColors.Navy.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = spacing.medium)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ArcadeColors.Navy.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoStories,
                            contentDescription = null,
                            tint = ArcadeColors.Navy,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "STORY VOCABULARY",
                            style = MaterialTheme.typography.labelSmall,
                            color = ArcadeColors.Navy,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = storyTitle,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // --- Phase-specific Content ---
        AnimatedContent(
            targetState = state.phase,
            transitionSpec = {
                fadeIn(tween(250)).togetherWith(fadeOut(tween(150)))
            },
            label = "gameplay_phase"
        ) { phase ->
            when (phase) {
                ListenSlapPhase.SPEAKING -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WordMasterEqualizerBanner()
                    }
                }

                ListenSlapPhase.SELECTING -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        GameTimer(
                            remainingMs = state.timerMs,
                            totalMs = state.maxTimerMs
                        )

                        Spacer(Modifier.height(spacing.medium))

                        // Replay Audio Speaker Button
                        val speakerColor = colorScheme.secondary
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(96.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            speakerColor.copy(alpha = 0.25f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                speakerColor.copy(alpha = 0.95f),
                                                speakerColor.copy(alpha = 0.70f)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.6f),
                                                Color.White.copy(alpha = 0.1f)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .clickable { onReplay() }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                    contentDescription = "Replay Audio",
                                    modifier = Modifier.size(34.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(Modifier.height(spacing.extraSmall))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Replay,
                                contentDescription = null,
                                tint = speakerColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Tap to hear again",
                                style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = speakerColor
                            )
                        }

                        Spacer(Modifier.height(spacing.medium))

                        // Playful Word Slot Tiles Indicator for Grade School Kids
                        state.currentWord?.word?.let { word ->
                            WordSlotsIndicator(targetWord = word, typedWord = state.typedWord)
                            Spacer(Modifier.height(spacing.large))
                        }

                        // Outlined Text Field (Speed Typer style)
                        OutlinedTextField(
                            value = state.typedWord,
                            onValueChange = onTypedWordChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            label = { Text("What did you hear?") },
                            placeholder = { Text("Type the word here...") },
                            shape = MaterialTheme.shapes.large,
                            enabled = state.phase == ListenSlapPhase.SELECTING,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { if (state.typedWord.isNotBlank()) onSubmitAnswer() }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ArcadeColors.Navy,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(Modifier.height(spacing.large))

                        // Primary Action Button (Speed Typer style)
                        GamePrimaryButton(
                            text = "SUBMIT SPELLING",
                            onClick = onSubmitAnswer,
                            icon = Icons.Rounded.CheckCircle,
                            accent = ArcadeColors.Navy,
                            enabled = state.typedWord.isNotBlank()
                        )
                    }
                }

                ListenSlapPhase.CORRECT,
                ListenSlapPhase.WRONG -> {
                    val isCorrect = phase == ListenSlapPhase.CORRECT
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = spacing.medium)
                    ) {
                        GameFeedbackBanner(
                            title = if (isCorrect) "Perfect Spelling!" else "Good Effort!",
                            message = if (isCorrect) {
                                "Awesome! You spelled '${state.currentWord?.word.orEmpty()}' correctly. +10 XP!"
                            } else {
                                "The word was '${state.currentWord?.word.orEmpty()}'. Keep practicing!"
                            },
                            type = if (isCorrect) GameFeedbackType.SUCCESS else GameFeedbackType.ENCOURAGEMENT
                        )

                        Spacer(Modifier.height(spacing.large))

                        // Spelled word reveal card
                        Surface(
                            color = if (isCorrect) ArcadeColors.Emerald.copy(alpha = 0.12f) else ArcadeColors.Rose.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(
                                1.5.dp,
                                if (isCorrect) ArcadeColors.Emerald else ArcadeColors.Rose
                            ),
                            modifier = Modifier.padding(horizontal = spacing.medium)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 20.dp)
                            ) {
                                Text(
                                    text = state.currentWord?.word.orEmpty().uppercase(),
                                    style = typography.headlineLarge,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 4.sp,
                                    color = if (isCorrect) ArcadeColors.Emerald else ArcadeColors.Rose
                                )
                                if (!isCorrect && state.typedWord.isNotBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "You typed: ${state.typedWord}",
                                        style = typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

/**
 * Playful, kid-friendly word slot blocks with tactile 3D tile effects and interactive letter animations.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordSlotsIndicator(targetWord: String, typedWord: String) {
    val cleanTyped = typedWord.trim()
    val colorScheme = MaterialTheme.colorScheme
    val infiniteTransition = rememberInfiniteTransition(label = "slot_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Playful Word Length Badge Chip
        Surface(
            color = ArcadeColors.Teal.copy(alpha = 0.12f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, ArcadeColors.Teal.copy(alpha = 0.35f)),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Spellcheck,
                    contentDescription = null,
                    tint = ArcadeColors.Teal,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Word Length: ${targetWord.length} Letters",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ArcadeColors.Teal
                )
            }
        }

        // 3D Toy-style Letter Blocks
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            targetWord.forEachIndexed { index, targetChar ->
                val typedChar = cleanTyped.getOrNull(index)
                val isFilled = typedChar != null
                val isCurrentSlot = !isFilled && index == cleanTyped.length
                val isMatch = typedChar != null && typedChar.equals(targetChar, ignoreCase = true)

                val blockScale by animateFloatAsState(
                    targetValue = if (isCurrentSlot) pulseScale else if (isFilled) 1.02f else 1f,
                    animationSpec = spring(),
                    label = "block_scale_$index"
                )

                val blockBackground: Brush = when {
                    isMatch -> Brush.verticalGradient(
                        colors = listOf(
                            ArcadeColors.Emerald.copy(alpha = 0.30f),
                            ArcadeColors.Emerald.copy(alpha = 0.12f)
                        )
                    )
                    isFilled -> Brush.verticalGradient(
                        colors = listOf(
                            ArcadeColors.Navy.copy(alpha = 0.25f),
                            ArcadeColors.Navy.copy(alpha = 0.08f)
                        )
                    )
                    isCurrentSlot -> Brush.verticalGradient(
                        colors = listOf(
                            ArcadeColors.Teal.copy(alpha = 0.22f),
                            ArcadeColors.Teal.copy(alpha = 0.06f)
                        )
                    )
                    else -> Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            colorScheme.surfaceVariant.copy(alpha = 0.15f)
                        )
                    )
                }

                val borderColor = when {
                    isMatch -> ArcadeColors.Emerald
                    isFilled -> ArcadeColors.Navy
                    isCurrentSlot -> ArcadeColors.Teal
                    else -> colorScheme.outline.copy(alpha = 0.35f)
                }

                Box(
                    modifier = Modifier
                        .scale(blockScale)
                        .size(width = 44.dp, height = 54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(blockBackground)
                        .border(
                            width = if (isCurrentSlot || isFilled) 2.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isFilled) {
                            Text(
                                text = typedChar.uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                                fontWeight = FontWeight.Black,
                                color = if (isMatch) ArcadeColors.Emerald else ArcadeColors.Navy
                            )
                        } else if (isCurrentSlot) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ArcadeColors.Teal)
                            )
                        } else {
                            Text(
                                text = "_",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WordMasterEqualizerBanner() {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val infiniteTransition = rememberInfiniteTransition(label = "word_master_equalizer")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    ArcadeColors.Navy.copy(alpha = 0.7f * pulseAlpha),
                    colorScheme.primary.copy(alpha = 0.3f)
                )
            )
        ),
        modifier = Modifier.fillMaxWidth().padding(vertical = spacing.small)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .scale(pulseScale)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ArcadeColors.Navy.copy(alpha = 0.35f),
                                ArcadeColors.Navy.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = ArcadeColors.Navy.copy(alpha = 0.85f * pulseAlpha),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Hearing,
                    contentDescription = "Listening",
                    tint = ArcadeColors.Navy,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            WordMasterEqualizerWaveform(infiniteTransition = infiniteTransition)

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Listen Closely to the Word...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = ArcadeColors.Navy,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Pay close attention! You will need to spell and type this word as fast as you can.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun WordMasterEqualizerWaveform(infiniteTransition: InfiniteTransition) {
    val barCount = 7
    val barMaxHeights = listOf(20, 36, 26, 48, 30, 42, 22)

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(50.dp)
    ) {
        repeat(barCount) { index ->
            val maxH = barMaxHeights.getOrElse(index) { 28 }
            val barScale by infiniteTransition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 350 + (index % 3) * 110,
                        delayMillis = index * 55,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wm_bar_$index"
            )

            val barBrush = if (index % 2 == 0) {
                Brush.verticalGradient(
                    colors = listOf(
                        ArcadeColors.Navy,
                        ArcadeColors.Navy.copy(alpha = 0.5f)
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height((maxH * barScale + 6).dp)
                    .clip(CircleShape)
                    .background(barBrush)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GamePlayScreenPreview() {
    ESupplementalTheme {
        AppBackground {
            GamePlayScreen(
                state = ListenSlapUiState(
                    currentWord = WordItem("1", "champion", "", "The Little Hero", "m-1"),
                    typedWord = "champ"
                ),
                onTypedWordChange = {},
                onSubmitAnswer = {},
                onReplay = {}
            )
        }
    }
}
