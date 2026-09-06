package com.esupplemental.presentation.ui.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.data.model.Note
import com.esupplemental.presentation.ui.theme.PlayfulShapes
import com.esupplemental.presentation.ui.theme.spacing

/**
 * A playful list of notes designed like a physical corkboard with sticky notes.
 */
@Composable
fun NotesLibraryTab(
    notes: List<Note>,
    onNoteClick: (String) -> Unit
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val stickyNoteColor = colorScheme.secondaryContainer
    val stickyTextColor = colorScheme.onSecondaryContainer

    if (notes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No notes yet. Start writing!",
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = 80.dp,
                start = 24.dp, // Aligned with header
                end = 24.dp,
                top = 16.dp
            ), // Aligned with header
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            itemsIndexed(notes, key = { _, note -> note.id }) { index, note ->
                val rotation = if (index % 2 == 0) -2f else 1.5f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ElevatedCard(
                        onClick = { onNoteClick(note.id) },
                        shape = PlayfulShapes.StickyNote,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { rotationZ = rotation },
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = stickyNoteColor,
                            contentColor = stickyTextColor
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(spacing.medium)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Description,
                                    contentDescription = null,
                                    tint = colorScheme.tertiary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(Modifier.width(spacing.small))
                                Text(
                                    text = note.title.ifEmpty { "UNTITLED" }.uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }

                            Spacer(Modifier.height(spacing.small))

                            val previewText = note.summary.ifEmpty { note.mainIdea }
                            Text(
                                text = previewText.take(150),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = stickyTextColor.copy(alpha = 0.8f),
                                maxLines = 3
                            )
                        }
                    }

                    // "Tape" Sticker
                    Surface(
                        color = colorScheme.surface.copy(alpha = 0.55f),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .width(60.dp)
                            .height(20.dp)
                            .offset(y = (-10).dp)
                            .graphicsLayer { rotationZ = rotation + 5f },
                        shape = PlayfulShapes.StickyNote
                    ) {}
                }
            }
        }
    }
}
