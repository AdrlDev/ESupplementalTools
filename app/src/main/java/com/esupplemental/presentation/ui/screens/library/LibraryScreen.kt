package com.esupplemental.presentation.ui.screens.library

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.automirrored.rounded.StickyNote2
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.Note
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.theme.*
import com.esupplemental.presentation.viewmodel.LibraryViewModel
import com.esupplemental.presentation.viewmodel.NoteViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryScreen(
    libraryViewModel: LibraryViewModel = koinViewModel(),
    noteViewModel: NoteViewModel = koinViewModel(),
    onSongClick: (String) -> Unit,
    onStoryClick: (String) -> Unit,
    onPoemClick: (String) -> Unit,
    onNoteClick: (String) -> Unit
) {
    val noteState by noteViewModel.uiState.collectAsStateWithLifecycle()
    val pagedMedia = libraryViewModel.pagedMedia.collectAsLazyPagingItems()

    AppBackground(showBlobs = false) {
        LibraryScreenContent(
            pagedMedia = pagedMedia,
            notes = noteState.allNotes,
            onSongClick = onSongClick,
            onStoryClick = onStoryClick,
            onPoemClick = onPoemClick,
            onNoteClick = onNoteClick,
            onPageChanged = libraryViewModel::selectPage
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreenContent(
    pagedMedia: LazyPagingItems<MediaItem>,
    notes: List<Note>,
    onSongClick: (String) -> Unit,
    onStoryClick: (String) -> Unit,
    onPoemClick: (String) -> Unit,
    onNoteClick: (String) -> Unit,
    onPageChanged: (Int) -> Unit
) {
    val tabs = remember {
        listOf(
            TabItem("Songs", Icons.Rounded.MusicNote, MediaType.SONG),
            TabItem("Stories", Icons.Rounded.AutoStories, MediaType.STORY),
            TabItem("Poems", Icons.Rounded.AutoStories, MediaType.POEM),
            TabItem("Notes", Icons.AutoMirrored.Rounded.StickyNote2, null)
        )
    }

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect(onPageChanged)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ── Playful Header ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            HeaderDecorativeShapes(Modifier.fillMaxSize())

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "My Library",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    ) {
                        Text(
                            text = "Level Up your Skills",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                LibraryHeaderIcon()
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Tabs ──
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            divider = {},
            edgePadding = 24.dp,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(pagerState.currentPage),
                    width = 32.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
            }
        )
        {
            tabs.forEachIndexed { index, tab ->
                val isSelected by remember { 
                    derivedStateOf { pagerState.currentPage == index } 
                }
                
                Tab(
                    selected = isSelected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(tab.icon, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                tab.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top,
            beyondViewportPageCount = 0 // Optimization: Only render the current tab
        ) { page ->
            when (page) {
                0 -> MediaLibraryTab(
                    items = pagedMedia,
                    accentColor = MaterialTheme.colorScheme.primary,
                    onItemClick = onSongClick
                )

                1 -> MediaLibraryTab(
                    items = pagedMedia,
                    accentColor = MaterialTheme.colorScheme.secondary,
                    onItemClick = onStoryClick
                )

                2 -> MediaLibraryTab(
                    items = pagedMedia,
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    onItemClick = onPoemClick
                )

                3 -> NotesLibraryTab(
                    notes = notes,
                    onNoteClick = onNoteClick
                )
            }
        }
    }
}

@Composable
private fun LibraryHeaderIcon() {
    val transition = rememberInfiniteTransition(label = "library_header_icon")
    val verticalOffset = transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "library_header_bob"
    )
    val rotation = transition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "library_header_rotation"
    )

    Box(
        modifier = Modifier
            .size(132.dp)
            .offset(y = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(108.dp)
                .graphicsLayer {
                    translationY = verticalOffset.value
                    rotationZ = rotation.value
                },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 6.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.LibraryBooks,
                    contentDescription = "Library",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(38.dp)
                .graphicsLayer {
                    translationY = -verticalOffset.value * 0.7f
                    rotationZ = -rotation.value * 2f
                },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "Library Full - Light",
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun LibraryScreenFullPreview() {
    ESupplementalTheme(darkTheme = false) {
        AppBackground {
            Column(modifier = Modifier.fillMaxSize()) {
                // Mock Header
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)) {
                    HeaderDecorativeShapes()
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "My Library",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.height(4.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 16.dp,
                                    bottomStart = 16.dp,
                                    bottomEnd = 16.dp
                                )
                            ) {
                                Text(
                                    text = "Level Up your Skills",
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Mock Tabs
                PrimaryScrollableTabRow(
                    selectedTabIndex = 2,
                    containerColor = Color.Transparent,
                    divider = {},
                    edgePadding = 24.dp,
                    indicator = {}
                ) {
                    listOf("Songs", "Stories", "Notes").forEachIndexed { index, title ->
                        Tab(
                            selected = index == 2,
                            onClick = {},
                            text = { Text(title) }
                        )
                    }
                }

                // Mock Notes List Content
                NotesLibraryTab(
                    notes = listOf(
                        Note(id = "1", title = "SAMPLE NOTES", summary = "sample summary")
                    ),
                    onNoteClick = {}
                )
            }
        }
    }
}
