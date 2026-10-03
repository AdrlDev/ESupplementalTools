package com.esupplemental.presentation.ui.screens.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.presentation.state.SongExerciseUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.components.StorySubmitButton
import com.esupplemental.presentation.ui.theme.SuccessGreen
import com.esupplemental.presentation.viewmodel.SongExerciseViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SongExerciseScreen(
    mediaId: String,
    viewModel: SongExerciseViewModel = koinViewModel(),
    onViewResults: (Int, Int, Long) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mediaId) { viewModel.loadActivity(mediaId) }
    LaunchedEffect(state.submissionError) {
        state.submissionError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSubmissionError()
        }
    }

    SongExerciseContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onFillBlankAnswerChange = viewModel::onFillBlankAnswerChange,
        onMessageOptionSelected = viewModel::onMessageOptionSelected,
        onSubmit = viewModel::submit,
        onViewResults = onViewResults
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SongExerciseContent(
    state: SongExerciseUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onFillBlankAnswerChange: (String, String) -> Unit,
    onMessageOptionSelected: (Int) -> Unit,
    onSubmit: () -> Unit,
    onViewResults: (Int, Int, Long) -> Unit
) {
    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Song Challenge", fontWeight = FontWeight.ExtraBold)
                            Text("Listen, remember, and answer", style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            when {
                state.loading -> LoadingContent(padding)
                state.error != null -> ErrorContent(state.error, padding)
                state.activity == null -> ErrorContent("This song challenge is unavailable.", padding)
                else -> {
                    val activity = state.activity
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = padding.calculateTopPadding() + 16.dp,
                            bottom = padding.calculateBottomPadding() + 32.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                "Complete the blanks from the song, then choose its message.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        item {
                            Text("Fill in the missing words", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        itemsIndexed(activity.fillBlanks, key = { _, item -> item.id }) { index, item ->
                            FillBlankItem(
                                sentence = item.sentence,
                                answer = state.fillBlankAnswers[item.id].orEmpty(),
                                isSubmitted = state.isSubmitted,
                                isCorrect = state.isSubmitted && normalize(state.fillBlankAnswers[item.id].orEmpty()) == normalize(item.answer),
                                isWrong = state.isSubmitted && normalize(state.fillBlankAnswers[item.id].orEmpty()) != normalize(item.answer),
                                correctAnswer = item.answer,
                                onAnswerChange = { onFillBlankAnswerChange(item.id, it) },
                                itemNumber = index + 1
                            )
                        }
                        item {
                            Text("What’s the message?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        item {
                            MessageQuestionCard(
                                state = state,
                                onOptionSelected = onMessageOptionSelected
                            )
                        }
                        item {
                            if (state.isSubmitted && state.resultId != null) {
                                Button(
                                    onClick = { onViewResults(state.score, state.total, state.resultId) },
                                    modifier = Modifier.fillMaxWidth().height(58.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) { Text("VIEW RESULTS", fontWeight = FontWeight.Bold) }
                            } else {
                                StorySubmitButton(
                                    onClick = onSubmit,
                                    enabled = !state.isSubmitting,
                                    isLoading = state.isSubmitting
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageQuestionCard(
    state: SongExerciseUiState,
    onOptionSelected: (Int) -> Unit
) {
    val question = state.activity?.messageQuestion ?: return
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(question.question, fontWeight = FontWeight.Bold)
            question.options.forEachIndexed { index, option ->
                val selected = state.selectedMessageOption == index
                val correct = state.isSubmitted && index == question.correctIndex
                val tint = when {
                    correct -> SuccessGreen
                    state.isSubmitted && selected -> MaterialTheme.colorScheme.error
                    selected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !state.isSubmitted && !state.isSubmitting) { onOptionSelected(index) }
                        .background(tint.copy(alpha = if (selected || correct) 0.12f else 0f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (selected || correct) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = tint
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(option, color = if (selected || correct) tint else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(padding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text("Loading your challenge…")
    }
}

@Composable
private fun ErrorContent(message: String, padding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(12.dp))
        Text(message, color = MaterialTheme.colorScheme.error)
    }
}

private fun normalize(value: String): String = value
    .trim()
    .lowercase()
    .replace(Regex("[^\\p{L}\\p{N}']+"), " ")
    .trim()
