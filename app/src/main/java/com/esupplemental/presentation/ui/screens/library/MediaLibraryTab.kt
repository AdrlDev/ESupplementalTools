package com.esupplemental.presentation.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.esupplemental.data.model.MediaItem
import com.esupplemental.presentation.ui.components.MediaListItem
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun MediaLibraryTab(
    items: LazyPagingItems<MediaItem>,
    accentColor: Color,
    onItemClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 80.dp, 
            start = 24.dp, // Aligned with header
            end = 24.dp, 
            top = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = items.itemCount,
            key = items.itemKey { it.id },
            contentType = items.itemContentType { "media_item" }
        ) { index ->
            val item = items[index]
            if (item != null) {
                val subTitle = item.category?.name ?: item.singer ?: ""
                MediaListItem(
                    title = item.title,
                    subtitle = subTitle,
                    thumbnailRes = item.thumbnailRes,
                    thumbnailUrl = item.thumbnailUrl,
                    placeholderColor = accentColor,
                    trailingIcon = {
                        FilledIconButton(
                            onClick = { onItemClick(item.id) },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = accentColor,
                                contentColor = contentColorFor(accentColor)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = "Play ${item.title}"
                            )
                        }
                    },
                    onClick = { onItemClick(item.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Library Tab - Stories")
@Composable
fun MediaLibraryTabPreview() {
    ESupplementalTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            // Preview with LazyPagingItems skipped
        }
    }
}
