package com.esupplemental.domain.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StoryActivityFactoryTest {

    @Test
    fun `creates ordered events and answerable questions from markdown`() {
        val markdown = """
            # The Test Story

            The first event introduces the hero and explains the journey that lies ahead.

            The second event sends the hero across a river to search for a missing friend.

            The third event reveals a difficult obstacle blocking the path through the forest.

            The fourth event shows the hero solving the obstacle with patience and courage.

            The fifth event reunites the hero with the friend after the long search succeeds.

            The final event brings everyone safely home and completes their memorable journey.
        """.trimIndent()

        val activity = StoryActivityFactory.create(
            mediaId = "t-test",
            title = "The Test Story",
            markdown = markdown
        )

        assertEquals("t-test", activity.mediaId)
        assertEquals(5, activity.reorderEvents.size)
        assertEquals((1..5).toList(), activity.reorderEvents.map { it.correctOrder })
        activity.reorderEvents.forEachIndexed { index, event ->
            assertTrue(event.id.startsWith("t-test_event_${index + 1}_"))
        }

        assertEquals(3, activity.multipleChoiceQuestions.size)
        activity.multipleChoiceQuestions.forEach { question ->
            assertEquals(4, question.choices.size)
            assertTrue(question.correctAnswerIndex in question.choices.indices)
            assertTrue(
                question.choices[question.correctAnswerIndex] in
                    activity.reorderEvents.map { it.description }
            )
        }

        val changedActivity = StoryActivityFactory.create(
            mediaId = "t-test",
            title = "The Test Story",
            markdown = markdown.replace("first event", "opening event")
        )
        assertNotEquals(
            activity.reorderEvents.first().id,
            changedActivity.reorderEvents.first().id
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects a story without enough narrative events`() {
        StoryActivityFactory.create(
            mediaId = "t-short",
            title = "Short Story",
            markdown = "# Short Story\n\nOnly one sufficiently descriptive event is available here."
        )
    }
}
