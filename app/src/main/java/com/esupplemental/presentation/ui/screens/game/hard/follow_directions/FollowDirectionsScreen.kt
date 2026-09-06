package com.esupplemental.presentation.ui.screens.game.hard.follow_directions

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.presentation.state.game_state.*
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.*
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.viewmodel.FollowDirectionsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.tooling.preview.Preview
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun FollowDirectionsScreen(
    gameId: String,
    mediaId: String? = null,
    onBack: () -> Unit,
    viewModel: FollowDirectionsViewModel = koinViewModel(parameters = { parametersOf(gameId, mediaId) })
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = MaterialTheme.spacing

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.screenPadding)
                .padding(bottom = spacing.large)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            val statusText = when (state.phase) {
                FollowDirectionsPhase.IDLE -> "Listen to the directions and guide the hero!"
                FollowDirectionsPhase.LOADING -> "Preparing your path..."
                FollowDirectionsPhase.LISTENING -> "Listen carefully to the instructions."
                FollowDirectionsPhase.PLAYING -> "Follow the directions on the grid."
                FollowDirectionsPhase.CORRECT -> "You found the target!"
                FollowDirectionsPhase.WRONG -> "Oops! That's not right."
                FollowDirectionsPhase.GAME_OVER -> "Path completed! Awesome work."
            }

            GameTopBar(
                title = "Follow the Directions",
                subtitle = statusText,
                icon = Icons.Rounded.CompassCalibration,
                accent = ArcadeColors.Teal,
                onBack = onBack,
                currentStep = state.round.takeIf { it > 0 },
                totalSteps = state.totalRounds.takeIf { it > 0 },
                score = state.score,
                xp = state.xpEarned
            )

            Spacer(Modifier.height(spacing.large))

            AnimatedContent(
                targetState = state.phase,
                transitionSpec = {
                    fadeIn(tween(400)) togetherWith fadeOut(tween(300))
                },
                label = "follow_directions_content"
            ) { phase ->
                when (phase) {
                    FollowDirectionsPhase.IDLE -> {
                        GameStartContent(onStart = viewModel::startGame)
                    }
                    FollowDirectionsPhase.LOADING -> {
                        GameLoadingState(message = "Loading challenge...")
                    }
                    FollowDirectionsPhase.LISTENING -> {
                        ListeningContent(state.currentChallenge?.instructionText ?: "")
                    }
                    FollowDirectionsPhase.PLAYING, FollowDirectionsPhase.CORRECT, FollowDirectionsPhase.WRONG -> {
                        GridGameContent(
                            state = state,
                            onMove = viewModel::moveCharacter,
                            onReplay = viewModel::replayInstruction
                        )
                    }
                    FollowDirectionsPhase.GAME_OVER -> {
                        GameResultScreen(
                            score = state.score,
                            total = state.totalRounds * 20,
                            xp = state.xpEarned,
                            onPlayAgain = viewModel::startGame,
                            onBack = onBack,
                            accent = ArcadeColors.Teal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameStartContent(onStart: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(24.dp)
    ) {
        Text(
            "Navigation Challenge",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Listen to the instructions based on the story, then move your character to the target destination!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        GamePrimaryButton(
            text = "I'M READY",
            onClick = onStart,
            icon = Icons.Rounded.PlayArrow,
            accent = ArcadeColors.Teal
        )
    }
}

@Composable
private fun ListeningContent(instruction: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(32.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(ArcadeColors.Teal.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Hearing,
                null,
                tint = ArcadeColors.Teal,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Listen carefully...",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = ArcadeColors.Teal
        )
        Spacer(Modifier.height(16.dp))
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                instruction,
                modifier = Modifier.padding(20.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
private fun GridGameContent(
    state: FollowDirectionsUiState,
    onMove: (Direction) -> Unit,
    onReplay: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val isReview = state.phase == FollowDirectionsPhase.CORRECT || state.phase == FollowDirectionsPhase.WRONG

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Instruction Summary with Replay
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = spacing.medium)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isReview) {
                        state.currentChallenge?.instructionText.orEmpty()
                    } else {
                        "Listen closely to the audio directions! Moves remaining: ${state.movesLeft}"
                    },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall
                )
                IconButton(onClick = onReplay) {
                    Icon(Icons.Rounded.Replay, "Replay Audio")
                }
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .padding(spacing.medium)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.3f
                    )
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            val safeGridSize =
                state.gridSize.coerceAtLeast(1)

            val cellWidth =
                maxWidth / safeGridSize

            val cellHeight =
                maxHeight / safeGridSize

            /*
             * GRID CELLS
             */
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                repeat(safeGridSize) {

                    Row {
                        repeat(safeGridSize) {

                            Box(
                                modifier = Modifier
                                    .size(
                                        width = cellWidth,
                                        height = cellHeight
                                    )
                                    .border(
                                        width = 0.5.dp,
                                        color = MaterialTheme
                                            .colorScheme
                                            .outlineVariant
                                            .copy(alpha = 0.2f)
                                    )
                            )
                        }
                    }
                }
            }

            /*
             * TARGET / FLAG
             *
             * Visible only once reached or during result review.
             */
            val showTarget = isReview || state.characterPosition == state.targetPosition
            if (showTarget) {
                val targetX = cellWidth * state.targetPosition.x
                val targetY = cellHeight * state.targetPosition.y

                Box(
                    modifier = Modifier
                        .offset(
                            x = targetX,
                            y = targetY
                        )
                        .size(
                            width = cellWidth,
                            height = cellHeight
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Flag,
                        contentDescription = "Target",
                        tint = ArcadeColors.Reward,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            /*
             * CHARACTER
             *
             * Animate X and Y independently in Dp.
             *
             * This avoids mixing:
             * - pixels
             * - dp
             * - Offset
             */
            val characterX by animateDpAsState(
                targetValue =
                    cellWidth *
                            state.characterPosition.x,
                animationSpec =
                    tween(durationMillis = 300),
                label = "character_x"
            )

            val characterY by animateDpAsState(
                targetValue =
                    cellHeight *
                            state.characterPosition.y,
                animationSpec =
                    tween(durationMillis = 300),
                label = "character_y"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = characterX,
                        y = characterY
                    )
                    .size(
                        width = cellWidth,
                        height = cellHeight
                    ),
                contentAlignment = Alignment.Center
            ) {

                Surface(
                    shape = CircleShape,
                    color = ArcadeColors.Teal,
                    modifier = Modifier.size(
                        cellWidth * 0.7f
                    )
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Icon(
                            imageVector =
                                Icons.Rounded.Person,
                            contentDescription =
                                "Player",
                            tint = Color.White,
                            modifier =
                                Modifier.size(
                                    cellWidth * 0.4f
                                )
                        )
                    }
                }
            }
        }

        Spacer(
            Modifier.height(spacing.large)
        )

        /*
         * CONTROLS
         */
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            IconButton(
                onClick = {
                    onMove(Direction.UP)
                },
                enabled =
                    state.phase ==
                            FollowDirectionsPhase.PLAYING,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector =
                        Icons.Rounded.KeyboardArrowUp,
                    contentDescription =
                        "Move Up"
                )
            }

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(32.dp)
            ) {

                IconButton(
                    onClick = {
                        onMove(Direction.LEFT)
                    },
                    enabled =
                        state.phase ==
                                FollowDirectionsPhase.PLAYING,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription =
                            "Move Left"
                    )
                }

                IconButton(
                    onClick = {
                        onMove(Direction.RIGHT)
                    },
                    enabled =
                        state.phase ==
                                FollowDirectionsPhase.PLAYING,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription =
                            "Move Right"
                    )
                }
            }

            IconButton(
                onClick = {
                    onMove(Direction.DOWN)
                },
                enabled =
                    state.phase ==
                            FollowDirectionsPhase.PLAYING,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector =
                        Icons.Rounded.KeyboardArrowDown,
                    contentDescription =
                        "Move Down"
                )
            }
        }

        if (
            state.phase ==
            FollowDirectionsPhase.CORRECT
        ) {
            Spacer(
                Modifier.height(spacing.medium)
            )

            GameFeedbackBanner(
                title = "Success!",
                message =
                    "You reached the target.",
                type =
                    GameFeedbackType.SUCCESS
            )
        }

        if (
            state.phase ==
            FollowDirectionsPhase.WRONG
        ) {
            Spacer(
                Modifier.height(spacing.medium)
            )

            GameFeedbackBanner(
                title = "Try Again!",
                message =
                    "That wasn't the correct path.",
                type =
                    GameFeedbackType.ENCOURAGEMENT
            )
        }
    }
}

@Preview(
    name = "Follow Directions - Start",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun FollowDirectionsStartPreview() {
    ESupplementalTheme {
        AppBackground {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                GameTopBar(
                    title =
                        "Follow the Directions",
                    subtitle =
                        "Listen to the directions and guide the hero!",
                    icon =
                        Icons.Rounded.CompassCalibration,
                    accent =
                        ArcadeColors.Teal,
                    onBack = {},
                    currentStep = null,
                    totalSteps = null,
                    score = 0,
                    xp = 0
                )

                Spacer(
                    Modifier.height(32.dp)
                )

                GameStartContent(
                    onStart = {}
                )
            }
        }
    }
}

@Preview(
    name = "Follow Directions - Listening",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun FollowDirectionsListeningPreview() {
    ESupplementalTheme {
        AppBackground {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                GameTopBar(
                    title =
                        "Follow the Directions",
                    subtitle =
                        "Listen carefully to the instructions.",
                    icon =
                        Icons.Rounded.CompassCalibration,
                    accent =
                        ArcadeColors.Teal,
                    onBack = {},
                    currentStep = 2,
                    totalSteps = 5,
                    score = 20,
                    xp = 5
                )

                Spacer(
                    Modifier.height(32.dp)
                )

                ListeningContent(
                    instruction =
                        "Move two steps to the right, then one step down toward the flag."
                )
            }
        }
    }
}

