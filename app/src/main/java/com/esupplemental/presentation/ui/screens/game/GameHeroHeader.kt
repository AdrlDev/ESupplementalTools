package com.esupplemental.presentation.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun GameHeroHeader(
    totalXp: Int,
    decoSpin: Float,
    decoFloat: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // Spinning star background decoration
        Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f),
            modifier = Modifier
                .size(22.dp)
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 24.dp)
                .rotate(decoSpin)
        )
        Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f),
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.CenterStart)
                .padding(start = 14.dp)
                .rotate(-decoSpin)
        )

        // Floating Controller Material Icon
        Icon(
            imageVector = Icons.Rounded.SportsEsports,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
            modifier = Modifier
                .size(128.dp)
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 24.dp)
                .offset(y = decoFloat.dp)
        )

        // Main Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 22.dp)
        ) {
            Text(
                text = "Game Zone",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Learn while you play!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Spacer(Modifier.height(10.dp))

            // XP Pill with Star Icon
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f))
                    .padding(horizontal = 14.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "XP Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "$totalXp XP",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Game Header - Static")
@Composable
fun GameHeroHeaderPreview() {
    ESupplementalTheme {
        GameHeroHeader(
            totalXp = 1250,
            decoSpin = 45f,
            decoFloat = 0f
        )
    }
}

@Preview(showBackground = true, name = "Game Header - Animated State")
@Composable
fun GameHeroHeaderAnimatedPreview() {
    ESupplementalTheme {
        GameHeroHeader(
            totalXp = 9999,
            decoSpin = 180f,
            decoFloat = 10f
        )
    }
}
