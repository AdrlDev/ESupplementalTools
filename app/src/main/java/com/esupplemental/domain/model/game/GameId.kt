package com.esupplemental.domain.model.game

enum class GameId(val id: String) {
    LISTEN_SLAP("listen_slap"),
    STORY_ORDER("story_order"),
    CHARACTER_QUEST("character_quest"),
    DIS_APPEARING_TEXT("disappearing_text"),
    TWO_TRUTHS_LIE("two_truths_lie"),
    MINIMAL_PAIRS("minimal_pairs"),
    SPEED_TYPER("speed_typer"),
    FOLLOW_DIRECTIONS("follow_directions"),
    STORY_RECALL("story_recall");

    companion object {
        fun fromId(id: String?): GameId? {
            if (id == "word_bingo") return CHARACTER_QUEST
            return entries.find { it.id == id }
        }

        fun fromIdOrDefault(id: String?): GameId {
            if (id == "word_bingo") return CHARACTER_QUEST
            return entries.find { it.id == id } ?: LISTEN_SLAP
        }
    }
}
