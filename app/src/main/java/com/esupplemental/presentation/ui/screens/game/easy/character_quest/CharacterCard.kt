package com.esupplemental.presentation.ui.screens.game.easy.character_quest

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.domain.utils.CharacterOption
import com.esupplemental.presentation.ui.theme.ArcadeColors

enum class CharacterCardState {
    IDLE,
    SELECTED,
    CORRECT,
    WRONG,
    REVEALED_CORRECT
}

@Composable
fun CharacterCard(
    character: CharacterOption,
    cardState: CharacterCardState,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val scale by animateFloatAsState(
        targetValue = when (cardState) {
            CharacterCardState.SELECTED -> 1.04f
            CharacterCardState.CORRECT, CharacterCardState.REVEALED_CORRECT -> 1.05f
            CharacterCardState.WRONG -> 0.96f
            CharacterCardState.IDLE -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "char_card_scale"
    )

    val targetBorderColor = when (cardState) {
        CharacterCardState.CORRECT, CharacterCardState.REVEALED_CORRECT -> ArcadeColors.Success
        CharacterCardState.WRONG -> ArcadeColors.Rose
        CharacterCardState.SELECTED -> colorScheme.primary
        CharacterCardState.IDLE -> colorScheme.outline.copy(alpha = 0.25f)
    }

    val targetBgColor = when (cardState) {
        CharacterCardState.CORRECT, CharacterCardState.REVEALED_CORRECT -> ArcadeColors.Success.copy(alpha = 0.15f)
        CharacterCardState.WRONG -> ArcadeColors.Rose.copy(alpha = 0.15f)
        CharacterCardState.SELECTED -> colorScheme.primary.copy(alpha = 0.15f)
        CharacterCardState.IDLE -> colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }

    val animatedBorderColor by animateColorAsState(targetBorderColor, tween(250), label = "border_color")
    val animatedBgColor by animateColorAsState(targetBgColor, tween(250), label = "bg_color")

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
        border = BorderStroke(
            width = if (cardState != CharacterCardState.IDLE) 2.5.dp else 1.5.dp,
            color = animatedBorderColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Character Avatar Emoji Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(character.colorHex).copy(alpha = 0.2f),
                border = BorderStroke(1.dp, Color(character.colorHex).copy(alpha = 0.5f)),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = character.avatarEmoji,
                        fontSize = 28.sp
                    )
                }
            }

            // Name & Role Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    ),
                    color = when (cardState) {
                        CharacterCardState.CORRECT, CharacterCardState.REVEALED_CORRECT -> ArcadeColors.Success
                        CharacterCardState.WRONG -> ArcadeColors.Rose
                        else -> colorScheme.onSurface
                    }
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = character.roleOrTitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colorScheme.onSurfaceVariant
                )
            }

            // Status Indicator Icon
            when (cardState) {
                CharacterCardState.CORRECT, CharacterCardState.REVEALED_CORRECT -> {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Correct",
                        tint = ArcadeColors.Success,
                        modifier = Modifier.size(28.dp)
                    )
                }
                CharacterCardState.WRONG -> {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Wrong",
                        tint = ArcadeColors.Rose,
                        modifier = Modifier.size(28.dp)
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(1.dp, colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                    )
                }
            }
        }
    }
}
