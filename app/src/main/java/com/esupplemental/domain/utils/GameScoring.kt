package com.esupplemental.domain.utils

object GameScoring {
    fun percentage(score: Int, total: Int): Int {
        if (total <= 0) return 0
        return (score.coerceIn(0, total) * 100) / total
    }

    fun stars(score: Int, total: Int): Int = when (percentage(score, total)) {
        100 -> 3
        in 70..99 -> 2
        in 40..69 -> 1
        else -> 0
    }
}
