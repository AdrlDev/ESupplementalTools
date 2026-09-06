package com.esupplemental.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun SearchBarSection(
    searchVisible: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onHeader: Color,
    placeholderText: String = "Search…",
    keyboard: SoftwareKeyboardController?
) {
    AnimatedVisibility(
        visible = searchVisible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        // Retain local state without re-creating on every keystroke
        var textFieldValue by remember {
            mutableStateOf(
                TextFieldValue(
                    text = searchQuery,
                    selection = TextRange(searchQuery.length)
                )
            )
        }

        // Sync with external resets (e.g. search bar closed or programmatically cleared)
        LaunchedEffect(searchQuery) {
            if (searchQuery != textFieldValue.text) {
                textFieldValue = TextFieldValue(
                    text = searchQuery,
                    selection = TextRange(searchQuery.length)
                )
            }
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    onSearchChange(newValue.text) // Sync string back to ViewModel
                },
                placeholder = {
                    Text(placeholderText, color = onHeader.copy(alpha = 0.6f))
                },
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = onHeader)
                },
                trailingIcon = {
                    if (textFieldValue.text.isNotEmpty()) {
                        IconButton(onClick = {
                            textFieldValue = TextFieldValue("")
                            onSearchChange("")
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = onHeader)
                        }
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Ltr),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = onHeader,
                    unfocusedBorderColor = onHeader.copy(alpha = 0.5f),
                    cursorColor = onHeader,
                    focusedTextColor = onHeader,
                    unfocusedTextColor = onHeader
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}