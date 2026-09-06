package com.esupplemental.presentation.ui.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun TranscriptContent(transcript: String) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = transcript,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Transcript - Light Mode")
@Composable
fun TranscriptContentLightPreview() {
    val sampleTranscript = """
        [Verse 1]
        Sa ilalim ng puting ilaw,
        Sa dilaw na buwan.
        Pakinggan mo ang aking bulong,
        Sa dilaw na buwan.
        
        [Chorus]
        Ayaw ko nang mag-isa,
        Gusto ko ay kasama ka.
        Sa ilalim ng puting ilaw,
        Sa dilaw na buwan.
    """.trimIndent()

    ESupplementalTheme(darkTheme = false) {
        AppBackground {
            TranscriptContent(transcript = sampleTranscript)
        }
    }
}

@Preview(showBackground = true, name = "Transcript - Dark Mode")
@Composable
fun TranscriptContentDarkPreview() {
    val sampleTranscript = "This is a sample story transcript. It helps the user follow along with the audio content effectively."

    ESupplementalTheme(darkTheme = true) {
        AppBackground {
            TranscriptContent(transcript = sampleTranscript)
        }
    }
}