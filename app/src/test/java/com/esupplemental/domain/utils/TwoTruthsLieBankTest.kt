package com.esupplemental.domain.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TwoTruthsLieBankTest {

    @Test
    fun `builds five stable rounds with two spoken truths and one story distractor`() {
        val stories = (1..6).map(::story)

        val questions = TwoTruthsLieBank.buildQuestions(stories)
        val rebuilt = TwoTruthsLieBank.buildQuestions(stories)

        assertEquals(5, questions.size)
        assertEquals(questions, rebuilt)
        questions.forEachIndexed { index, question ->
            assertEquals("Story ${index + 1}", question.sourceTitle)
            assertEquals(3, question.statements.size)
            assertEquals(2, question.statements.count { it.isTrue })
            assertEquals(1, question.statements.count { !it.isTrue })
            assertEquals(
                question.statements.filter { it.isTrue }.joinToString(" ") { it.text },
                question.audioText
            )
            assertTrue(question.id.startsWith("story-${index + 1}_two_truths_lie_"))
        }
    }

    @Test
    fun `changes the audio cache id when a source story context changes`() {
        val original = (1..6).map(::story)
        val changed = original.toMutableList().apply {
            this[0] = this[0].copy(
                markdown = this[0].markdown.replace("story 1", "the first story")
            )
        }

        val originalQuestion = TwoTruthsLieBank.buildQuestions(original).first()
        val changedQuestion = TwoTruthsLieBank.buildQuestions(changed).first()

        assertNotEquals(originalQuestion.audioText, changedQuestion.audioText)
        assertNotEquals(originalQuestion.id, changedQuestion.id)
    }

    private fun story(index: Int) = TwoTruthsLieBank.StoryContent(
        id = "story-$index",
        title = "Story $index",
        markdown = """
            # Story $index

            The careful traveler from story $index finds a hidden map beside the quiet river.

            Later, the traveler from story $index asks a patient friend to follow the map safely.

            At sunset, both friends from story $index return home and share what they learned.
        """.trimIndent()
    )
}
