package com.esupplemental.presentation.ui.screens.player

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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.components.MediaListItem
import com.esupplemental.presentation.ui.components.SearchBarSection
import com.esupplemental.presentation.ui.components.toTimeLabel
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.viewmodel.PlayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MediaListScreen(
    type: MediaType,
    viewModel: PlayerViewModel = koinViewModel(),
    onItemClick: (String) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(type) { viewModel.loadListForType(type) }
    val state by viewModel.uiState.collectAsState()
    val isSong = type == MediaType.SONG
    val isPoem = type == MediaType.POEM
    val headerColor = if (isSong) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.secondary

    AppBackground(
        topColor = headerColor
    ) {
        MediaListContent(
            headerColor = headerColor,
            isSong = isSong,
            isPoem = isPoem,
            searchQuery = state.searchQuery,
            filteredList = state.filteredList,
            onSearchChange = { viewModel.onSearchQueryChange(it) },
            onItemClick = onItemClick,
            onBack = onBack
        )
    }
}

@Composable
fun MediaListContent(
    isSong: Boolean,
    isPoem: Boolean,
    headerColor: Color,
    searchQuery: String,
    filteredList: List<MediaItem>,
    onSearchChange: (String) -> Unit,
    onItemClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val onHeader = if (isSong) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSecondary
    val listLabel = when { isSong -> "Songs"; isPoem -> "Poems"; else -> "Stories" }
    val instructionText = when { isSong -> "Choose a song to listen"; isPoem -> "Choose a poem to read"; else -> "Choose a story to read" }
    val searchHint = when { isSong -> "Search songs…"; isPoem -> "Search poems…"; else -> "Search stories…" }

    var searchVisible by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxSize()) {

        // ══════════════════════════════════════════════════════════════════
        // HEADER — two-layer trick for colored status bar + rounded bottom
        // ══════════════════════════════════════════════════════════════════
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
                // ── Top bar ───────────────────────────────────────────────
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
                        text = listLabel,
                        style = MaterialTheme.typography.displayMedium,
                        color = onHeader
                    )

                    // Search icon toggles the search bar; becomes ✕ when open
                    IconButton(onClick = {
                        searchVisible = !searchVisible
                        if (!searchVisible) {
                            onSearchChange("")   // clear query when closing
                            keyboard?.hide()
                        }
                    }) {
                        Icon(
                            imageVector = if (searchVisible) Icons.Rounded.Close
                            else Icons.Rounded.Search,
                            contentDescription = if (searchVisible) "Close search"
                            else "Search",
                            tint = onHeader
                        )
                    }
                }

                // ── Instruction text (hidden while search is open) ─────────
                AnimatedVisibility(
                    visible = !searchVisible,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Text(
                        text = instructionText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = onHeader.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }

                // ── Search bar component ──────────────────────────────────
                SearchBarSection(
                    searchVisible = searchVisible,
                    searchQuery = searchQuery,
                    onSearchChange = onSearchChange,
                    onHeader = onHeader,
                    placeholderText = searchHint,
                    keyboard = keyboard
                )
            }
        }

        // ── List card ──────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .offset(y = (-24).dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) "No items available"
                        else "No results for \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)) {
                    itemsIndexed(filteredList, key = { _, item -> item.id }) { index, item ->
                        val subTitle = item.category?.name ?: item.singer ?: ""
                        MediaListItem(
                            title = item.title,
                            subtitle = subTitle,
                            thumbnailRes = item.thumbnailRes,
                            thumbnailUrl = item.thumbnailUrl,
                            durationLabel = item.durationSeconds.toTimeLabel(),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.PlayCircleFilled,
                                    contentDescription = "Play ${item.title}",
                                    tint = headerColor,
                                    modifier = Modifier.size(40.dp)
                                )
                            },
                            onClick = { onItemClick(item.id) }
                        )
                        if (index < filteredList.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Songs – light")
@Composable
private fun MediaListSongsPreview() {
    ESupplementalTheme {
        MediaListContent(
            isSong = true,
            isPoem = false,
            headerColor = MaterialTheme.colorScheme.primary,
            searchQuery = "", onSearchChange = {},
            filteredList = emptyList(),
            onItemClick = {}, onBack = {}
        )
    }
}

@Preview(
    showBackground = true, name = "Stories – dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun MediaListStoriesDarkPreview() {
    ESupplementalTheme(darkTheme = true) {
        MediaListContent(
            isSong = false,
            isPoem = false,
            headerColor = MaterialTheme.colorScheme.secondary,
            searchQuery = "Patintero", onSearchChange = {},
            filteredList = emptyList(),
            onItemClick = {}, onBack = {}
        )
    }
}
