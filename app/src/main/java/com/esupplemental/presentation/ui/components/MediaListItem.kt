package com.esupplemental.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.R
import com.esupplemental.presentation.ui.theme.BrandAqua
import com.esupplemental.presentation.ui.theme.BrandNavy
import com.esupplemental.presentation.ui.theme.BrandTeal
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun MediaListItem(
    title: String,
    subtitle: String,
    durationLabel: String? = null,
    thumbnailRes: Int? = null,
    thumbnailUrl: String? = null,
    placeholderColor: Color = MaterialTheme.colorScheme.primary,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val formattedTitle = remember(title) {
        title.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Playful Thumbnail Container ---
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(placeholderColor, placeholderColor.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!thumbnailUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (thumbnailRes != null) {
                    // ── Actual Image Thumbnail ──
                    Image(
                        painter = painterResource(id = thumbnailRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // ── Fallback Sticker Icon ──
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .offset(x = (-10).dp, y = (-10).dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    )
                    Text(
                        text = title.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formattedTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Trailing Section
            if (trailingIcon != null) {
                trailingIcon()
            } else if (durationLabel != null) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = CircleShape
                ) {
                    Text(
                        text = durationLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Media Item - Song")
@Composable
fun MediaListItemPreview() {
    ESupplementalTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            MediaListItem(
                title = "the brave little toaster",
                subtitle = "English Vocabulary • Story",
                durationLabel = "5:20",
                placeholderColor = BrandNavy,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Media Item List View")
@Composable
fun MediaListGroupPreview() {
    ESupplementalTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            MediaListItem(
                title = "alphabet adventure",
                subtitle = "Phonics • Song",
                durationLabel = "2:15",
                placeholderColor = BrandTeal,
                onClick = {}
            )
            MediaListItem(
                title = "counting stars",
                subtitle = "Math • Song",
                thumbnailRes = R.drawable.mouse_and_lion,
                durationLabel = "3:45",
                placeholderColor = BrandAqua,
                onClick = {}
            )
        }
    }
}
