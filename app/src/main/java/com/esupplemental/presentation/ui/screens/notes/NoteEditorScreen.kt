package com.esupplemental.presentation.ui.screens.notes

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.viewmodel.NoteViewModel

@Composable
fun NoteEditorScreen(
    noteId: String,
    viewModel: NoteViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(noteId) { viewModel.loadNote(noteId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val headerColor = MaterialTheme.colorScheme.primary
    val onHeader = MaterialTheme.colorScheme.onPrimary

    AppBackground(topColor = headerColor) {
        NoteEditorContent(
            title = state.title,
            mainIdea = state.mainIdea,
            keyDetails = state.keyDetails,
            summary = state.summary,
            keywords = state.keywords,
            newKeyword = state.newKeyword,
            isBold = state.isBold,
            isItalic = state.isItalic,
            isUnderline = state.isUnderline,
            isSaved = state.isSaved,
            headerColor = headerColor,
            onHeader = onHeader,
            onTitleChange = viewModel::onTitleChange,
            onMainIdeaChange = viewModel::onMainIdeaChange,
            onKeyDetailsChange = viewModel::onKeyDetailsChange,
            onSummaryChange = viewModel::onSummaryChange,
            onNewKeywordChange = viewModel::onNewKeywordChange,
            onAddKeyword = viewModel::addKeyword,
            onRemoveKeyword = viewModel::removeKeyword,
            onToggleBold = viewModel::toggleBold,
            onToggleItalic = viewModel::toggleItalic,
            onToggleUnderline = viewModel::toggleUnderline,
            onSave = viewModel::saveNote,
            onBack = onBack
        )
    }
}

@Composable
fun NoteEditorContent(
    title: String,
    mainIdea: String,
    keyDetails: String,
    summary: String,
    keywords: List<String>,
    newKeyword: String,
    isBold: Boolean,
    isItalic: Boolean,
    isUnderline: Boolean,
    isSaved: Boolean,
    headerColor: Color,
    onHeader: Color,
    onTitleChange: (String) -> Unit,
    onMainIdeaChange: (String) -> Unit,
    onKeyDetailsChange: (String) -> Unit,
    onSummaryChange: (String) -> Unit,
    onNewKeywordChange: (String) -> Unit,
    onAddKeyword: () -> Unit,
    onRemoveKeyword: (String) -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    onToggleUnderline: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // 👈 Adjusts layout bounds when soft keyboard pops up
    ) {

        // ── EDITOR HEADER ────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(headerColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(headerColor)
                    .statusBarsPadding()
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = onHeader
                        )
                    }

                    Text(
                        text = "Note Editor",
                        style = MaterialTheme.typography.displayMedium,
                        color = onHeader,
                        modifier = Modifier.weight(1f)
                    )

                    if (isSaved) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = "Saved",
                            tint = onHeader
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Saved",
                            style = MaterialTheme.typography.bodySmall,
                            color = onHeader
                        )
                        Spacer(Modifier.width(12.dp))
                    }

                    Button(
                        onClick = onSave,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ── EDITOR CONTENT CARD ─────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .offset(y = (-24).dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title input
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        placeholder = { Text("Note title…") },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Formatting toolbar
                item {
                    FormattingToolbar(
                        isBold = isBold,
                        isItalic = isItalic,
                        isUnderline = isUnderline,
                        onBold = onToggleBold,
                        onItalic = onToggleItalic,
                        onUnderline = onToggleUnderline
                    )
                }

                // Main Idea Section
                item {
                    NoteSectionHeader(
                        icon = Icons.Rounded.Lightbulb,
                        label = "Main Idea"
                    )
                    Spacer(Modifier.size(8.dp))
                    NoteSectionField(
                        sectionLabel = "",
                        value = mainIdea,
                        onValueChange = onMainIdeaChange,
                        isBold = isBold,
                        isItalic = isItalic,
                        isUnderline = isUnderline,
                        placeholder = "What is this about?"
                    )
                }

                // Key Details Section
                item {
                    NoteSectionHeader(
                        icon = Icons.AutoMirrored.Rounded.FormatListBulleted,
                        label = "Key Details"
                    )
                    Spacer(Modifier.size(8.dp))
                    NoteSectionField(
                        sectionLabel = "",
                        value = keyDetails,
                        onValueChange = onKeyDetailsChange,
                        isBold = isBold,
                        isItalic = isItalic,
                        isUnderline = isUnderline,
                        placeholder = "Important facts and details…",
                        minLines = 3
                    )
                }

                // Summary Section
                item {
                    NoteSectionHeader(
                        icon = Icons.Rounded.Description,
                        label = "Summary"
                    )
                    Spacer(Modifier.size(8.dp))
                    NoteSectionField(
                        sectionLabel = "",
                        value = summary,
                        onValueChange = onSummaryChange,
                        isBold = isBold,
                        isItalic = isItalic,
                        isUnderline = isUnderline,
                        placeholder = "Sum it up in a few sentences…",
                        minLines = 3
                    )
                }

                // Keywords Section
                item {
                    KeywordsSection(
                        keywords = keywords,
                        newKeyword = newKeyword,
                        onNewKeywordChange = onNewKeywordChange,
                        onAddKeyword = onAddKeyword,
                        onRemoveKeyword = onRemoveKeyword
                    )
                }
            }
        }
    }
}

// ── REUSABLE SECTION HEADER ───────────────────────────────────────────────────

@Composable
fun NoteSectionHeader(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEWS
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Note Editor – Light")
@Composable
private fun NoteEditorContentPreview() {
    ESupplementalTheme {
        AppBackground(topColor = MaterialTheme.colorScheme.primary) {
            NoteEditorContent(
                title = "Jetpack Compose Architecture",
                mainIdea = "Declarative UI framework for modern Android apps.",
                keyDetails = "1. Uses composable functions.\n2. State drives UI updates.\n3. Recomposition re-executes changed components.",
                summary = "Jetpack Compose simplifies layout and state handling compared to traditional XML layouts.",
                keywords = listOf("Compose", "Kotlin", "Android", "UI"),
                newKeyword = "",
                isBold = true,
                isItalic = false,
                isUnderline = false,
                isSaved = true,
                headerColor = MaterialTheme.colorScheme.primary,
                onHeader = MaterialTheme.colorScheme.onPrimary,
                onTitleChange = {},
                onMainIdeaChange = {},
                onKeyDetailsChange = {},
                onSummaryChange = {},
                onNewKeywordChange = {},
                onAddKeyword = {},
                onRemoveKeyword = {},
                onToggleBold = {},
                onToggleItalic = {},
                onToggleUnderline = {},
                onSave = {},
                onBack = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Note Editor – Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NoteEditorContentDarkPreview() {
    ESupplementalTheme(darkTheme = true) {
        AppBackground(topColor = MaterialTheme.colorScheme.primary) {
            NoteEditorContent(
                title = "",
                mainIdea = "",
                keyDetails = "",
                summary = "",
                keywords = emptyList(),
                newKeyword = "StateFlow",
                isBold = false,
                isItalic = false,
                isUnderline = false,
                isSaved = false,
                headerColor = MaterialTheme.colorScheme.primary,
                onHeader = MaterialTheme.colorScheme.onPrimary,
                onTitleChange = {},
                onMainIdeaChange = {},
                onKeyDetailsChange = {},
                onSummaryChange = {},
                onNewKeywordChange = {},
                onAddKeyword = {},
                onRemoveKeyword = {},
                onToggleBold = {},
                onToggleItalic = {},
                onToggleUnderline = {},
                onSave = {},
                onBack = {}
            )
        }
    }
}