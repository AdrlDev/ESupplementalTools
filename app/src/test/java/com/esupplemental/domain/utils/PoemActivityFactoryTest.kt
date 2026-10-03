package com.esupplemental.domain.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PoemActivityFactoryTest {
    @Test
    fun `parses multiple choice and true false questions into shared activity`() {
        val activity = PoemActivityFactory.create(
            mediaId = "p-test",
            markdown = """
                # Test Poem

                ## Poem
                A line,
                another line.

                ## Multiple Choice
                ### 1. Where is the seed?
                - A. In a garden
                - B. In the earth
                - C. On a tree
                - D. In a cloud
                Answer: B

                ## True or False
                ### 1. The seed is underground.
                Answer: True
            """.trimIndent()
        )

        assertEquals(2, activity.multipleChoiceQuestions.size)
        assertEquals(1, activity.multipleChoiceQuestions[0].correctAnswerIndex)
        assertEquals(listOf("True", "False"), activity.multipleChoiceQuestions[1].choices)
        assertEquals(0, activity.multipleChoiceQuestions[1].correctAnswerIndex)
        assertTrue(activity.reorderEvents.isEmpty())
    }

    @Test
    fun `extracts only poem body for playback`() {
        val transcript = PoemActivityFactory.extractTranscript(
            """
            # Test Poem
            Author: Author
            ## Poem
            First stanza.

            Second stanza.
            ## Multiple Choice
            ### 1. Question
            - A. One
            - B. Two
            - C. Three
            - D. Four
            Answer: B
            """.trimIndent()
        )

        assertEquals("First stanza.\n\nSecond stanza.", transcript)
        assertTrue("Multiple Choice" !in transcript)
    }
}
