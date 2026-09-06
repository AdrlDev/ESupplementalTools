package com.esupplemental.presentation.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.esupplemental.data.model.UserStats
import com.esupplemental.presentation.state.HomeUiState
import com.esupplemental.presentation.ui.components.ActivityCard
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.components.SectionHeader
import com.esupplemental.presentation.ui.theme.*
import com.esupplemental.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onSongsClick: () -> Unit,
    onStoriesClick: () -> Unit,
    onNoteToolClick: () -> Unit,
    onGameClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    HomeScreenContent(
        state = state,
        onSongsClick = onSongsClick,
        onStoriesClick = onStoriesClick,
        onNoteToolClick = onNoteToolClick,
        onGameClick = onGameClick
    )
}

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onSongsClick: () -> Unit,
    onStoriesClick: () -> Unit,
    onNoteToolClick: () -> Unit,
    onGameClick: () -> Unit
) {
    val stats = state.userStats
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenPadding)
        ) {
            val formattedName = stats.userName.split(" ").joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { it.uppercase() }
            }

            Spacer(Modifier.height(spacing.screenPadding))

            HeaderSection(
                title = "Hi, $formattedName!",
                subTitle = "Ready to play and learn?"
            )

            ProgressSummaryCard(level = stats.level, levelProgress = stats.levelProgress)

            Spacer(Modifier.height(spacing.extraLarge))

            SectionHeader(title = "Choose an Activity")

            Spacer(Modifier.height(spacing.small))

            ActivityCard(
                title = "Songs",
                description = "Listen to songs and fill in the blanks",
                icon = Icons.Rounded.MusicNote,
                containerColor = colorScheme.primary,
                onClick = onSongsClick
            )
            Spacer(Modifier.height(spacing.small))
            ActivityCard(
                title = "Stories",
                description = "Listen to stories and answer questions",
                icon = Icons.AutoMirrored.Rounded.MenuBook,
                containerColor = colorScheme.secondary,
                onClick = onStoriesClick
            )
            Spacer(Modifier.height(spacing.small))
            ActivityCard(
                title = "Note-Taking Tool",
                description = "Organize your ideas with structured notes",
                icon = Icons.Rounded.Edit,
                containerColor = colorScheme.tertiary,
                onClick = onNoteToolClick
            )
            Spacer(Modifier.height(spacing.small))
            ActivityCard(
                title = "Games",
                description = "Challenge yourself with interactive learning exercises.",
                icon = Icons.Rounded.Games,
                containerColor = colorScheme.primaryContainer,
                onClick = onGameClick
            )
            Spacer(Modifier.height(spacing.extraLarge))
        }
    }
}

@Preview(showBackground = true, name = "Home Screen - Light Mode")
@Composable
fun HomeScreenPreview() {
    ESupplementalTheme(darkTheme = false) {
        val state = HomeUiState(
            userStats = UserStats(userName = "Adriel")
        )

        HomeScreenContent(
            state = state,
            onSongsClick = {},
            onStoriesClick = {},
            onNoteToolClick = {},
            onGameClick = {}
        )
    }
}
