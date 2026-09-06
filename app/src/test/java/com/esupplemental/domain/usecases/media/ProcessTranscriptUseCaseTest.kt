package com.esupplemental.domain.usecases.media

import org.junit.Assert.assertEquals
import org.junit.Test

class ProcessTranscriptUseCaseTest {

    private val useCase = ProcessTranscriptUseCase()

    @Test
    fun `invoke splits into logical chunks and calculates word ranges`() {
        val transcript = "Solo was lonely. He wanted an adventure!"
        
        val result = useCase(transcript)
        
        assertEquals(2, result.size)
        
        // "Solo was lonely." -> 3 words
        assertEquals("Solo was lonely.", result[0].text)
        assertEquals(0, result[0].startWordIndex)
        assertEquals(2, result[0].endWordIndex)
        
        // "He wanted an adventure!" -> 4 words
        assertEquals("He wanted an adventure!", result[1].text)
        assertEquals(3, result[1].startWordIndex)
        assertEquals(6, result[1].endWordIndex)
    }

    @Test
    fun `invoke normalizes markdown and whitespace`() {
        val transcript = """
            # Chapter One
            
            Solo was **lonely**.
            
            He wanted an [adventure](url)!
        """.trimIndent()
        
        val result = useCase(transcript)
        
        assertEquals(2, result.size)
        assertEquals("Solo was lonely.", result[0].text)
        assertEquals("He wanted an adventure!", result[1].text)
    }
}
