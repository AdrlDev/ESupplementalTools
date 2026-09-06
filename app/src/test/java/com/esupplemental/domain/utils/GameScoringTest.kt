package com.esupplemental.domain.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class GameScoringTest {
    @Test
    fun `stars match progression thresholds`() {
        assertEquals(0, GameScoring.stars(0, 25))
        assertEquals(0, GameScoring.stars(9, 25))
        assertEquals(1, GameScoring.stars(10, 25))
        assertEquals(2, GameScoring.stars(18, 25))
        assertEquals(3, GameScoring.stars(25, 25))
    }

    @Test
    fun `invalid totals never award stars`() {
        assertEquals(0, GameScoring.stars(10, 0))
    }
}
