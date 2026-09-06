package com.esupplemental.presentation.ui.screens.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeywordsSection(
    keywords: List<String>,
    newKeyword: String,
    onNewKeywordChange: (String) -> Unit,
    onAddKeyword: () -> Unit,
    onRemoveKeyword: (String) -> Unit
) {
    Column {
        Text(
            text = "🔑 Important Words",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            keywords.forEach { kw ->
                KeywordChipItem(
                    text = kw,
                    onRemove = { onRemoveKeyword(kw) }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newKeyword,
                onValueChange = onNewKeywordChange,
                placeholder = { Text("Add keyword…") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            FilledIconButton(
                onClick = onAddKeyword,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add keyword",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KeywordsSectionPreview() {
    ESupplementalTheme {
        var keywords by remember { mutableStateOf(listOf("Android", "Kotlin", "Compose")) }
        var newKeyword by remember { mutableStateOf("") }

        Surface(modifier = Modifier.padding(16.dp)) {
            KeywordsSection(
                keywords = keywords,
                newKeyword = newKeyword,
                onNewKeywordChange = { newKeyword = it },
                onAddKeyword = {
                    if (newKeyword.isNotBlank()) {
                        keywords = keywords + newKeyword.trim()
                        newKeyword = ""
                    }
                },
                onRemoveKeyword = { kw -> keywords = keywords.filter { it != kw } }
            )
        }
    }
}