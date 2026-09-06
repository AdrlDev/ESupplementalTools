package com.esupplemental.presentation.ui.screens.game

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.domain.model.game.GameItem
import com.esupplemental.presentation.ui.theme.*

/**
 * A card representing a game in the catalog.
 * Respects Material 3 tokens and typography rules.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameCard(
    game: GameItem,
    isCompleted: Boolean,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
    levelNumber: Int? = null,
    isCurrent: Boolean = false
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    val difficulty = game.difficulty
    val difficultyColors = rememberGameDifficultyColors(difficulty)
    val semanticColors = rememberGameSemanticColors()
    val interactionSource = remember(game.id) { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && !game.isLocked) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_press_${game.id}"
    )

    var appeared by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.92f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_entry_${game.id}"
    )
    LaunchedEffect(game.id) { appeared = true }

    Box(
        modifier = modifier
            .scale(cardScale * pressScale)
            .fillMaxWidth()
            .shadow(if (isPressed) 2.dp else if (isCurrent) 11.dp else 6.dp, PlayfulShapes.GameCard)
            .clip(PlayfulShapes.GameCard)
            .background(colorScheme.surface)
            .border(
                if (isCurrent) 2.dp else 1.dp,
                difficultyColors.accent.copy(alpha = if (isCurrent) 0.62f else 0.18f),
                PlayfulShapes.GameCard
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !game.isLocked,
                onClick = onPlay
            )
            .semantics { role = Role.Button }
    ) {
        Column {
            // Accent strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(difficultyColors.container, difficultyColors.accent)
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.medium),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                // Game icon badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(shapes.medium)
                        .background(difficultyColors.accent)
                ) {
                    Icon(
                        imageVector = game.icon,
                        contentDescription = difficulty.label,
                        tint = difficultyColors.onAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    if (levelNumber != null || isCurrent) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (levelNumber != null) {
                                Surface(
                                    shape = CircleShape,
                                    color = difficultyColors.container
                                ) {
                                    Text(
                                        "LEVEL $levelNumber",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                        color = difficultyColors.onContainer
                                    )
                                }
                            }
                            if (isCurrent) {
                                Surface(shape = CircleShape, color = difficultyColors.accent) {
                                    Text(
                                        "NEXT QUEST",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                        color = difficultyColors.onAccent
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(spacing.extraSmall))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Text(
                            text = game.title,
                            style = typography.titleLarge.copy(
                                fontFamily = typography.titleLarge.fontFamily, // Force SharkBit for Title
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (isCompleted) {
                            Surface(
                                shape = shapes.extraSmall,
                                color = semanticColors.success.copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text = "✓ Done",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = semanticColors.success
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(spacing.extraSmall))
                    Text(
                        text = game.description,
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(spacing.small))

                    // Skill tags
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                    ) {
                        game.skillTags.forEach { tag ->
                            Surface(
                                shape = CircleShape,
                                color = difficultyColors.container
                            ) {
                                Text(
                                    text = tag.label,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = difficultyColors.onContainer
                                )
                            }
                        }
                    }
                }
            }

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(difficultyColors.container.copy(alpha = 0.72f))
                    .padding(horizontal = spacing.medium, vertical = spacing.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(3) { idx ->
                        Icon(
                            imageVector = if (idx < game.stars) Icons.Rounded.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (idx < game.stars) StarGold else colorScheme.onSurface.copy(alpha = 0.22f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(spacing.small))
                    Column {
                        GameProgressBar(
                            progress = game.stars / 3f,
                            accent = difficultyColors.accent,
                            modifier = Modifier.width(64.dp),
                            height = 6
                        )
                        Text(
                            text = "${game.stars * 100 / 3}% • ${game.xpReward} XP",
                            style = typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = difficultyColors.onContainer
                        )
                    }
                }

                if (game.isLocked) {
                    LockedButton()
                } else {
                    PlayButton(difficulty = difficulty, onClick = onPlay)
                }
            }
        }

        if (game.isLocked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(colorScheme.surface.copy(alpha = 0.72f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = when (game.difficulty) {
                            GameDifficulty.EASY -> "Complete the previous quest to unlock this level."
                            GameDifficulty.MODERATE -> "Complete the Easy adventure and previous quests."
                            GameDifficulty.HARD -> "Advanced challenge—finish the Moderate adventure first."
                        },
                        style = typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PlayButton(difficulty: GameDifficulty, onClick: () -> Unit) {
    val difficultyColors = rememberGameDifficultyColors(difficulty)
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = difficultyColors.accent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = difficultyColors.onAccent,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Play!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = difficultyColors.onAccent
            )
        }
    }
}

@Composable
fun LockedButton() {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.40f),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Locked",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.40f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameCardPreview() {
    ESupplementalTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GameCard(
                game = GameItem(id = "1", title = "Quiz Master", icon = Icons.Rounded.TouchApp, difficulty = GameDifficulty.HARD, description = "", skillTags = emptyList()),
                isCompleted = false,
                onPlay = {},
                levelNumber = 2,
                isCurrent = true
            )
        }
    }
}
