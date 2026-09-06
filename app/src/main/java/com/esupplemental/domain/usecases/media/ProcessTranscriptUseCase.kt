package com.esupplemental.domain.usecases.media

import com.esupplemental.domain.model.media.StoryChunk

class ProcessTranscriptUseCase {
    /**
     * Splits a transcript into speakable sentence chunks and calculates word ranges.
     * Normalizes markdown, boundaries, and repeated whitespace.
     */
    operator fun invoke(text: String?): List<StoryChunk> {
        if (text.isNullOrBlank()) return emptyList()

        // Normalize text
        val normalized = text
            .replace(Regex("#+\\s*.*"), "") // Remove markdown headers
            .replace(Regex("\\*\\*|__|\\*|_"), "") // Remove emphasis markers
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1") // Simplify links to just their text
            .replace(Regex("\\r\\n|\\r"), "\n") // Normalize newlines
            .replace(Regex("\\n{3,}"), "\n\n") // Limit consecutive blank lines
            .trim()

        // Split into sentences using a more robust regex that handles common abbreviations
        // This is a simple version; real world might use a more complex boundary detector
        val sentenceRegex = Regex("(?<=[.!?])\\s+(?=[A-Z\"“])|(?<=[.!?])\\s*$")
        val rawSentences = normalized.split(sentenceRegex)
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val chunks = mutableListOf<StoryChunk>()
        var currentWordOffset = 0

        rawSentences.forEachIndexed { index, sentence ->
            // We use the normalized version of the sentence to count words consistently with how ElevenLabs does
            val wordsInSentence = sentence.split(Regex("\\s+")).filter { it.isNotEmpty() }
            val wordCount = wordsInSentence.size
            
            chunks.add(
                StoryChunk(
                    index = index,
                    text = sentence,
                    startWordIndex = currentWordOffset,
                    endWordIndex = currentWordOffset + wordCount - 1
                )
            )
            currentWordOffset += wordCount
        }

        return chunks
    }
}

