package com.esupplemental.presentation.ui.screens.exercise

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.domain.utils.GameScoring
import com.esupplemental.presentation.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// ── Score Banner ───────────────────────────────────────────────────────────

/**
 * Celebratory score reveal card — "Classroom Arcade" style.
 *
 * Layout
 * ──────
 * ┌──────────────────────────────────────────────────┐
 * │  [Spinning emoji decoration]                     │
 * │──────────────────────────────────────────────────│
 * │  Gradient top strip  •  "Your Score!" label      │
 * │──────────────────────────────────────────────────│
 * │  Large fraction  ···  Large percentage pill      │
 * │  [Star row based on score percentage]            │
 * └──────────────────────────────────────────────────┘
 *
 * Stars use the same thresholds as the games: 3 at 100%, 2 at 70%, 1 at 40%.
 */
@Composable
fun ScoreBanner(
    score: Int,
    total: Int,
    accentColor: Color
) {
    val safeTotal = total.coerceAtLeast(0)
    val safeScore = if (safeTotal > 0) score.coerceIn(0, safeTotal) else 0
    val pct = GameScoring.percentage(safeScore, safeTotal)
    val starCount = GameScoring.stars(safeScore, safeTotal)

    // ── Entry scale pop ────────────────────────────────────────────────────
    var appeared by remember { mutableStateOf(false) }
    val entryScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "entry_scale"
    )
    LaunchedEffect(Unit) { appeared = true }

    // ── Spinning confetti decoration ───────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "score_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4_000, easing = LinearEasing)),
        label = "spin"
    )

    Box(
        modifier = Modifier
            .scale(entryScale)
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // ── Gradient header strip ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(accentColor, accentColor.copy(alpha = 0.60f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Spinning star decoration
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = Color.Yellow,
                        modifier = Modifier.rotate(spinAngle)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Your Score!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        color = Color.White
                    )
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = Color.Yellow,
                        modifier = Modifier.rotate(spinAngle)
                    )
                }
            }

            // ── Score body ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Fraction column
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$safeScore",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 52.sp
                        ),
                        color = accentColor
                    )
                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .height(3.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.45f))
                    )
                    Text(
                        text = "$safeTotal",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = accentColor.copy(alpha = 0.65f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "correct",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                // Percentage pill
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    accentColor.copy(alpha = 0.18f),
                                    accentColor.copy(alpha = 0.06f)
                                )
                            )
                        )
                        .shadow(0.dp)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$pct",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 40.sp
                            ),
                            color = accentColor,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = accentColor.copy(alpha = 0.70f)
                        )
                    }
                }
            }

            // ── Star rating row ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor.copy(alpha = 0.06f))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { idx ->
                    val filled = idx < starCount
                    // Staggered pop-in per star
                    var starAppeared by remember { mutableStateOf(false) }
                    val starScale by animateFloatAsState(
                        targetValue = if (starAppeared) 1f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "star_$idx"
                    )
                    LaunchedEffect(Unit) {
                        delay((idx * 150L).milliseconds)
                        starAppeared = true
                    }
                    Icon(
                        imageVector = if (filled) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                        contentDescription = null,
                        tint = if (filled) StarGold else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.25f
                        ),
                        modifier = Modifier
                            .scale(starScale)
                            .size(
                                if (filled) 35.dp else 26.dp
                            )
                    )
                    if (idx < 2) Spacer(Modifier.width(8.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = when (starCount) {
                        3 -> "Amazing!"
                        2 -> "Good job!"
                        else -> "Keep trying!"
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = accentColor
                )
                val vIcon = when(starCount) {
                    3 -> Icons.Rounded.Celebration
                    2 -> Icons.Rounded.ThumbUp
                    else -> Icons.Rounded.FitnessCenter
                }
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = vIcon,
                    contentDescription = null,
                    tint = accentColor
                )
            }
        }
    }
}


// ── Previews ──────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Score Banner – Perfect (Light)")
@Composable
private fun ScoreBannerPerfectPreview() {
    ESupplementalTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScoreBanner(score = 10, total = 10, accentColor = BrandNavy)
            ScoreBanner(score = 7, total = 10, accentColor = BrandTeal)
            ScoreBanner(score = 3, total = 10, accentColor = BrandAqua)
        }
    }
}

@Preview(showBackground = true, name = "Score Banner – Dark")
@Composable
private fun ScoreBannerDarkPreview() {
    ESupplementalTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ScoreBanner(score = 8, total = 10, accentColor = BrandDarkPrimary)
        }
    }
}
