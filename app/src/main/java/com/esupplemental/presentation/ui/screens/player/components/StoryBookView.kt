package com.esupplemental.presentation.ui.screens.player.components

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.draw.alpha
import com.esupplemental.domain.model.media.StoryChunk
import com.esupplemental.domain.model.media.StoryPage
import com.esupplemental.data.remote.model.StoryAudioStatus
import androidx.compose.material3.CircularProgressIndicator
import coil.compose.AsyncImage
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.presentation.state.PlayerUiState
import com.esupplemental.presentation.ui.theme.spacing
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Senior Engineer Note: This component encapsulates the "Story Book" experience.
 * It handles the page-flip animations, synchronization with playback,
 * and the "Closed Book" cover state.
 */
@Composable
fun StoryBookView(
    state: PlayerUiState,
    item: MediaItem,
    accentColor: Color,
    onSeek: (Float) -> Unit,
    onSeekMs: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val isClosed = state.currentPositionSeconds == 0 && !state.isPlaying

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isClosed) "STORY COVER" else "STORY BOOK",
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )
            )

            if (!isClosed) {
                val currentPage = if (state.transcriptPages.isNotEmpty()) {
                    val pageIdx = state.transcriptPages.indexOfFirst { page ->
                        page.chunks.any { it.index == state.currentSentenceIndex }
                    }.coerceAtLeast(0)
                    pageIdx + 1
                } else 0

                Text(
                    text = "PAGE $currentPage / ${state.transcriptPages.size}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(Modifier.height(spacing.medium))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            val isProcessing = state.storyAudioStatus == StoryAudioStatus.PROCESSING

            AnimatedContent(
                targetState = when {
                    isProcessing -> "processing"
                    isClosed -> "closed"
                    else -> "pages"
                },
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "book_state_transition"
            ) { targetState ->
                when (targetState) {
                    "processing" -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = accentColor)
                            Spacer(Modifier.height(spacing.medium))
                            Text(
                                text = "Preparing story timing...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    "closed" -> {
                        BookCover(item = item, accentColor = accentColor)
                    }
                    else -> {
                        BookPages(
                            state = state,
                            accentColor = accentColor,
                            onSeek = onSeek,
                            onSeekMs = onSeekMs
                        )
                    }
                }
            }
        }

        if (state.isPlaybackFinished) {
            Spacer(Modifier.height(spacing.medium))
        }
    }
}

@Composable
private fun BookCover(
    item: MediaItem,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .graphicsLayer {
                rotationY = -15f // Slight 3D perspective
                cameraDistance = 8 * density
            },
        contentAlignment = Alignment.Center
    ) {
        // Book Spine Shadow
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(12.dp)
                .align(Alignment.CenterStart)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
        )

        CardCover(item, accentColor)
    }
}

@Composable
private fun CardCover(item: MediaItem, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!item.thumbnailUrl.isNullOrBlank()) {
                AsyncImage(
                    model = item.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (item.thumbnailRes != null && item.thumbnailRes != 0) {
                Image(
                    painter = painterResource(id = item.thumbnailRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(accentColor))
            }

            // Title Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = item.title.uppercase(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                )
            }
        }
    }
}

@Composable
private fun BookPages(
    state: PlayerUiState,
    accentColor: Color,
    onSeek: (Float) -> Unit,
    onSeekMs: (Long) -> Unit
) {
    val pages = state.transcriptPages
    val scope = rememberCoroutineScope()

    if (pages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No story pages available.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
        return
    }

    var currentPage by remember {
        mutableIntStateOf(0)
    }

    var isPageTurning by remember {
        mutableStateOf(false)
    }

    var isUserDragging by remember {
        mutableStateOf(false)
    }

    val turnProgress =
        remember {
            Animatable(0f)
        }

    var turnDirection by remember {
        mutableIntStateOf(0)
    }

    val narrationPage by remember(
        state.currentWordIndex,
        state.currentSentenceIndex,
        pages
    ) {
        derivedStateOf {

            val wordIndex =
                state.currentWordIndex

            if (wordIndex >= 0) {
                pages.indexOfFirst { page ->
                    page.chunks.any { chunk ->
                        wordIndex in chunk.startWordIndex..chunk.endWordIndex
                    }
                }.takeIf { it >= 0 } ?: currentPage
            } else if (state.currentSentenceIndex >= 0) {
                // Fallback for legacy
                pages.indexOfFirst { page ->
                    page.chunks.any { it.index == state.currentSentenceIndex }
                }.takeIf { it >= 0 } ?: currentPage
            } else {
                currentPage
            }
        }
    }

    fun seekToPage(page: StoryPage) {
        if (page.chunks.isEmpty()) return

        val firstChunk = page.chunks.first()
        if (state.wordTimings.isNotEmpty() && firstChunk.startWordIndex in state.wordTimings.indices) {
            val targetMs = state.wordTimings[firstChunk.startWordIndex].startMs
            onSeekMs(targetMs)
        } else {
            // Fallback for legacy audio
            val fraction = (firstChunk.index.toFloat() / state.transcriptChunks.size.coerceAtLeast(1))
                .coerceIn(0f, 1f)
            onSeek(fraction)
        }
    }

    // Keep the visual book synchronized with narration.
    LaunchedEffect(
        narrationPage
    ) {
        if (isUserDragging || isPageTurning) return@LaunchedEffect

        val destination =
            narrationPage.coerceIn(
                pages.indices
            )

        if (currentPage != destination) {
            isPageTurning = true
            try {
                turnDirection = if (destination > currentPage) 1 else -1
                turnProgress.snapTo(0f)
                turnProgress.animateTo(1f, tween(520))
                currentPage = destination
                turnProgress.snapTo(0f)
                turnDirection = 0
            } finally {
                isPageTurning = false
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        val widthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        var accumulatedDrag by remember { mutableFloatStateOf(0f) }

        val dragModifier = Modifier.pointerInput(currentPage, pages.size, isPageTurning) {
            if (isPageTurning) return@pointerInput

            detectHorizontalDragGestures(
                onDragStart = {
                    isUserDragging = true
                    accumulatedDrag = 0f
                },
                onHorizontalDrag = { change, dragAmount ->
                    change.consume()
                    accumulatedDrag += dragAmount

                    val wantsNext = accumulatedDrag < 0 && currentPage < pages.lastIndex
                    val wantsPrevious = accumulatedDrag > 0 && currentPage > 0

                    val direction = when {
                        wantsNext -> 1
                        wantsPrevious -> -1
                        else -> 0
                    }

                    turnDirection = direction
                    if (direction != 0) {
                        val progress = (abs(accumulatedDrag) / widthPx)
                            .coerceIn(0f, 1f)

                        scope.launch {
                            turnProgress.snapTo(progress)
                        }
                    } else {
                        scope.launch {
                            turnProgress.snapTo(0f)
                        }
                    }
                },
                onDragCancel = {
                    isUserDragging = false
                    scope.launch {
                        turnProgress.animateTo(0f, tween(180))
                        turnDirection = 0
                        accumulatedDrag = 0f
                    }
                },
                onDragEnd = {
                    isUserDragging = false
                    val shouldComplete = turnDirection != 0 &&
                            (turnProgress.value >= 0.22f || abs(accumulatedDrag) > widthPx * 0.18f)

                    scope.launch {
                        if (shouldComplete) {
                            isPageTurning = true
                            try {
                                val targetPageIndex = (currentPage + turnDirection)
                                    .coerceIn(0, pages.lastIndex)

                                turnProgress.animateTo(1f, tween(260))
                                currentPage = targetPageIndex
                                turnProgress.snapTo(0f)
                                seekToPage(pages[targetPageIndex])
                            } finally {
                                isPageTurning = false
                            }
                        } else {
                            turnProgress.animateTo(0f, tween(220))
                        }

                        turnDirection = 0
                        accumulatedDrag = 0f
                    }
                }
            )
        }

        val displayedDirection = turnDirection
        val targetPage = when (displayedDirection) {
            1 -> (currentPage + 1).coerceAtMost(pages.lastIndex)
            -1 -> (currentPage - 1).coerceAtLeast(0)
            else -> currentPage
        }

        Box(
            modifier = dragModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Page underneath the page being turned.
            if (displayedDirection != 0) {
                StoryPaperPage(
                    pageIndex = targetPage,
                    pageChunks = pages[targetPage].chunks,
                    state = state,
                    accentColor = accentColor,
                    onSeek = onSeek,
                    onSeekMs = onSeekMs,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val reveal = turnProgress.value
                            scaleX = 0.965f + (0.035f * reveal)
                            scaleY = 0.985f + (0.015f * reveal)
                            alpha = 0.70f + (0.30f * reveal)
                        }
                )
            }

            // Current paper page. The pivot switches to the page edge being turned.
            StoryPaperPage(
                pageIndex = currentPage,
                pageChunks = pages[currentPage].chunks,
                state = state,
                accentColor = accentColor,
                onSeek = onSeek,
                onSeekMs = onSeekMs,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val progress = turnProgress.value
                        cameraDistance = 24f * density

                        when (displayedDirection) {
                            1 -> {
                                transformOrigin = TransformOrigin(0f, 0.5f)
                                rotationY = -178f * progress
                            }
                            -1 -> {
                                transformOrigin = TransformOrigin(1f, 0.5f)
                                rotationY = 178f * progress
                            }
                            else -> {
                                rotationY = 0f
                            }
                        }
                    }
            )

            // Fold highlight / shadow. This makes the 3D turn read more like paper than a card.
            if (displayedDirection != 0 && turnProgress.value > 0.01f) {
                PageFoldOverlay(
                    progress = turnProgress.value,
                    direction = displayedDirection,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun StoryPaperPage(
    pageIndex: Int,
    pageChunks: List<StoryChunk>,
    state: PlayerUiState,
    accentColor: Color,
    onSeek: (Float) -> Unit,
    onSeekMs: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val paperColor = if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        Color(0xFFFFFBF1)
    }

    Surface(
        modifier = modifier.padding(horizontal = 18.dp, vertical = 12.dp),
        shape = RoundedCornerShape(10.dp),
        color = paperColor,
        tonalElevation = 2.dp,
        shadowElevation = 10.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.055f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.025f)
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 28.dp),
                verticalArrangement = Arrangement.Center
            ) {
                pageChunks.forEach { chunk ->
                    val isHighlighted = chunk.index == state.currentSentenceIndex

                    val textAlpha by animateFloatAsState(
                        targetValue = if (isHighlighted) 1f else 0.80f,
                        label = "story_page_text_alpha"
                    )

                    val annotatedText = if (isHighlighted && state.wordTimings.isNotEmpty()) {
                        buildAnnotatedString {
                            // Robust mapping: split by whitespace but preserve original string for character ranges
                            val words = chunk.text.split(Regex("(?<=\\s)|(?=\\s)"))
                            var currentWordIdx = chunk.startWordIndex
                            
                            words.forEach { word ->
                                if (word.isBlank()) {
                                    append(word)
                                } else {
                                    val isActive = currentWordIdx == state.currentWordIndex
                                    if (isActive) {
                                        withStyle(
                                            SpanStyle(
                                                color = accentColor,
                                                fontWeight = FontWeight.Bold,
                                                background = accentColor.copy(alpha = 0.12f)
                                            )
                                        ) {
                                            append(word)
                                        }
                                    } else {
                                        append(word)
                                    }
                                    currentWordIdx++
                                }
                            }
                        }
                    } else {
                        buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = if (isHighlighted) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium
                                )
                            ) {
                                append(chunk.text)
                            }
                        }
                    }

                    Text(
                        text = annotatedText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            lineHeight = 28.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(textAlpha)
                            .padding(vertical = spacing.small)
                            .clickable {
                                if (state.wordTimings.isNotEmpty() && chunk.startWordIndex in state.wordTimings.indices) {
                                    onSeekMs(state.wordTimings[chunk.startWordIndex].startMs)
                                } else {
                                    val fraction = (chunk.index.toFloat() /
                                            state.transcriptChunks.size.coerceAtLeast(1))
                                        .coerceIn(0f, 1f)
                                    onSeek(fraction)
                                }
                            }
                    )
                }
            }

            Text(
                text = "${pageIndex + 1}",
                modifier = Modifier.align(Alignment.BottomCenter),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}

@Composable
private fun PageFoldOverlay(
    progress: Float,
    direction: Int,
    modifier: Modifier = Modifier
) {
    val p = progress.coerceIn(0f, 1f)
    val foldWidth = (22 + (58 * (1f - abs(0.5f - p) * 2f))).dp

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(foldWidth)
                .align(if (direction > 0) Alignment.CenterStart else Alignment.CenterEnd)
                .background(
                    Brush.horizontalGradient(
                        colors = if (direction > 0) {
                            listOf(
                                Color.White.copy(alpha = 0.18f * (1f - p)),
                                Color.Black.copy(alpha = 0.15f * p),
                                Color.Transparent
                            )
                        } else {
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.15f * p),
                                Color.White.copy(alpha = 0.18f * (1f - p))
                            )
                        }
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(34.dp)
                .align(if (direction > 0) Alignment.CenterStart else Alignment.CenterEnd)
                .background(
                    Brush.horizontalGradient(
                        colors = if (direction > 0) {
                            listOf(Color.Black.copy(alpha = 0.12f * p), Color.Transparent)
                        } else {
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.12f * p))
                        }
                    )
                )
        )
    }
}

// region Previews

@Preview(
    name = "Story Book View - Cover State",
    group = "Player Components",
    showBackground = true
)
@Preview(
    name = "Story Book View - Cover State (Dark)",
    group = "Player Components",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun StoryBookViewCoverPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.height(450.dp)) {
            Box(modifier = Modifier.padding(16.dp)) {
                StoryBookView(
                    state = PlayerUiState(
                        currentPositionSeconds = 0,
                        isPlaying = false
                    ),
                    item = MediaItem(
                        id = "1",
                        title = "The Secret of the Whispering Woods",
                        type = MediaType.STORY,
                        durationSeconds = 280,
                        thumbnailUrl = null
                    ),
                    accentColor = MaterialTheme.colorScheme.primary,
                    onSeek = {},
                    onSeekMs = {}
                )
            }
        }
    }
}

@Preview(
    name = "Story Book View - Pages State",
    group = "Player Components",
    showBackground = true
)
@Composable
private fun StoryBookViewPagesPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.height(450.dp)) {
            Box(modifier = Modifier.padding(16.dp)) {
                StoryBookView(
                    state = PlayerUiState(
                        currentPositionSeconds = 30,
                        isPlaying = true,
                        currentSentenceIndex = 3,
                        transcriptPages = listOf(
                            StoryPage(0, listOf(
                                StoryChunk(0, "Once upon a time, in a dense and magical forest,", 0, 9),
                                StoryChunk(1, "the trees whispered secrets to the wind.", 10, 16),
                                StoryChunk(2, "Little fox ventured into the unknown,", 17, 22),
                                StoryChunk(3, "seeking the lost constellation of stars.", 23, 28)
                            ))
                        ),
                        transcriptChunks = listOf(
                            StoryChunk(0, "Once upon a time, in a dense and magical forest,", 0, 9),
                            StoryChunk(1, "the trees whispered secrets to the wind.", 10, 16),
                            StoryChunk(2, "Little fox ventured into the unknown,", 17, 22),
                            StoryChunk(3, "seeking the lost constellation of stars.", 23, 28)
                        )
                    ),
                    item = MediaItem(
                        id = "1",
                        title = "The Secret of the Whispering Woods",
                        type = MediaType.STORY,
                        durationSeconds = 280,
                        thumbnailUrl = null
                    ),
                    accentColor = MaterialTheme.colorScheme.primary,
                    onSeek = {},
                    onSeekMs = {}
                )
            }
        }
    }
}
