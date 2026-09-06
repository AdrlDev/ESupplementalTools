package com.esupplemental.presentation.ui.screens.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.components.SearchBarSection
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.viewmodel.NoteViewModel
import org.koin.androidx.compose.koinViewModel

// ─────────────────────────────────────────────────────────────────────────────
// NOTE LIST SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NoteListScreen(
    viewModel: NoteViewModel = koinViewModel(),
    onOpenNote: (String) -> Unit,
    onNewNote: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val headerColor = MaterialTheme.colorScheme.primary
    val onHeader = MaterialTheme.colorScheme.onPrimary

    var searchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current

    val filteredNotes = remember(state.allNotes, searchQuery) {
        if (searchQuery.isBlank()) {
            state.allNotes
        } else {
            state.allNotes.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.summary.contains(searchQuery, ignoreCase = true) ||
                        it.mainIdea.contains(searchQuery, ignoreCase = true) ||
                        it.keywords.any { kw -> kw.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    AppBackground(topColor = headerColor) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── HEADER (Two-Layer Pattern) ──────────────────────────────
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Layer 1 – status-bar color fill (square, no clip)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsTopHeight(WindowInsets.statusBars)
                            .background(headerColor)
                    )

                    // Layer 2 – rounded header card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                            .background(headerColor)
                            .statusBarsPadding()
                            .padding(bottom = 32.dp)
                    ) {
                        // Top Navigation Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Back",
                                    tint = onHeader
                                )
                            }

                            Text(
                                text = "Note Taking",
                                style = MaterialTheme.typography.displayMedium,
                                color = onHeader
                            )

                            IconButton(onClick = {
                                searchVisible = !searchVisible
                                if (!searchVisible) {
                                    searchQuery = ""
                                    keyboard?.hide()
                                }
                            }) {
                                Icon(
                                    imageVector = if (searchVisible) Icons.Default.Close else Icons.Rounded.Search,
                                    contentDescription = if (searchVisible) "Close search" else "Search",
                                    tint = onHeader
                                )
                            }
                        }

                        // Subtitle text (hidden when search is visible)
                        AnimatedVisibility(
                            visible = !searchVisible,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Text(
                                text = "${state.allNotes.size} saved notes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = onHeader.copy(alpha = 0.85f),
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                            )
                        }

                        // Search Bar Section Component
                        SearchBarSection(
                            searchVisible = searchVisible,
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            onHeader = onHeader,
                            placeholderText = "Search notes…",
                            keyboard = keyboard
                        )
                    }
                }

                // ── LIST CONTAINER ──────────────────────────────────────────
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-24).dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    if (filteredNotes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isEmpty()) "No notes created yet"
                                else "No notes found for \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                top = 12.dp,
                                bottom = 88.dp, // Space for FAB
                                start = 12.dp,
                                end = 12.dp
                            )
                        ) {
                            itemsIndexed(filteredNotes, key = { _, note -> note.id }) { index, note ->
                                NotePreviewCard(
                                    title = note.title,
                                    preview = note.summary.take(80).ifEmpty { note.mainIdea.take(80) },
                                    keywordsCount = note.keywords.size,
                                    onClick = { onOpenNote(note.id) }
                                )
                                if (index < filteredNotes.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── FLOATING ACTION BUTTON ─────────────────────────────────────
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.newNote()
                    onNewNote()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Note", fontWeight = FontWeight.Bold) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 28.dp, bottom = 16.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEWS
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun NoteListContentPreview() {
    ESupplementalTheme {
        // Sample preview data
        val sampleNotes = listOf(
            MockNote("1", "Kotlin Basics", "Summary of Kotlin syntax and features.", "Kotlin is a modern language.", listOf("Kotlin", "Android")),
            MockNote("2", "Jetpack Compose", "Declarative UI toolkit for Android.", "Compose simplifies UI development.", listOf("Compose", "UI")),
            MockNote("3", "Architecture Patterns", "MVVM and Clean Architecture principles.", "Separation of concerns is key.", listOf("MVVM", "Clean Architecture"))
        )

        var searchVisible by remember { mutableStateOf(false) }

        AppBackground(topColor = MaterialTheme.colorScheme.primary) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Mockup
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(bottom = 32.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(onClick = {}) {
                                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                                Text("Note Taking", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onPrimary)
                                IconButton(onClick = { searchVisible = !searchVisible }) {
                                    Icon(Icons.Rounded.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                            Text(
                                text = "${sampleNotes.size} saved notes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Content Mockup
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-24).dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        LazyColumn(contentPadding = PaddingValues(12.dp)) {
                            itemsIndexed(sampleNotes) { index, note ->
                                NotePreviewCard(
                                    title = note.title,
                                    preview = note.summary,
                                    keywordsCount = note.keywords.size,
                                    onClick = {}
                                )
                                if (index < sampleNotes.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }

                ExtendedFloatingActionButton(
                    onClick = {},
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("New Note", fontWeight = FontWeight.Bold) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 28.dp, bottom = 16.dp)
                )
            }
        }
    }
}

// Simple mock data model for Preview purposes
private data class MockNote(
    val id: String,
    val title: String,
    val summary: String,
    val mainIdea: String,
    val keywords: List<String>
)