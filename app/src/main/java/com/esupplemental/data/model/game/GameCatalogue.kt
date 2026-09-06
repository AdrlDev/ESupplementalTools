package com.esupplemental.data.model.game

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CompassCalibration
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.TouchApp
import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.domain.model.game.GameId
import com.esupplemental.domain.model.game.GameItem
import com.esupplemental.domain.model.game.GameSkillTag

class GameCatalogue {
    companion object {
        val all: List<GameItem> = listOf(

            // ── EASY ──────────────────────────────────────────────────────────
            GameItem(
                id = GameId.LISTEN_SLAP.id,
                icon = Icons.Rounded.Keyboard,
                title = "Word Master",
                description = "Listen to the word, then type it back correctly before time runs out!",
                skillTags = listOf(GameSkillTag.VOCABULARY, GameSkillTag.FOCUS),
                difficulty = GameDifficulty.EASY,
                stars = 0,
                isLocked = false,
                xpReward = 0
            ),
            GameItem(
                id = GameId.STORY_ORDER.id,
                icon = Icons.AutoMirrored.Rounded.MenuBook,
                title = "Story Order",
                description = "Listen to a story, then drag events into the right order!",
                skillTags = listOf(GameSkillTag.COMPREHENSION, GameSkillTag.SEQUENCING),
                difficulty = GameDifficulty.EASY,
                stars = 0,
                isLocked = true,
                xpReward = 0
            ),
            GameItem(
                id = GameId.CHARACTER_QUEST.id,
                icon = Icons.Rounded.RecordVoiceOver,
                title = "Character Quest",
                description = "Listen to what the story character said or did, then tap the right character!",
                skillTags = listOf(GameSkillTag.COMPREHENSION, GameSkillTag.DETAIL),
                difficulty = GameDifficulty.EASY,
                stars = 0,
                isLocked = false,
                xpReward = 0
            ),

            // ── MODERATE ──────────────────────────────────────────────────────
            GameItem(
                id = GameId.DIS_APPEARING_TEXT.id,
                icon = Icons.Rounded.AutoFixHigh,
                title = "Disappearing Text",
                description = "Read the sentence, watch it vanish word by word, then type it back!",
                skillTags = listOf(GameSkillTag.MEMORY, GameSkillTag.FOCUS),
                difficulty = GameDifficulty.MODERATE,
                stars = 0,
                isLocked = true,
                xpReward = 0
            ),
            GameItem(
                id = GameId.TWO_TRUTHS_LIE.id,
                icon = Icons.Rounded.Psychology,
                title = "2 Truths 1 Lie",
                description = "Listen to the audio, then spot the ONE statement that was NOT said.",
                skillTags = listOf(GameSkillTag.CRITICAL_THINKING, GameSkillTag.COMPREHENSION),
                difficulty = GameDifficulty.MODERATE,
                stars = 0,
                isLocked = true,
                xpReward = 0
            ),
            GameItem(
                id = GameId.MINIMAL_PAIRS.id,
                icon = Icons.Rounded.Hearing,
                title = "Minimal Pairs Battle",
                description = "\"Ship\" or \"sheep\"? Tap the word you actually heard in the audio!",
                skillTags = listOf(GameSkillTag.PRONUNCIATION, GameSkillTag.FOCUS),
                difficulty = GameDifficulty.MODERATE,
                stars = 0,
                isLocked = true,
                xpReward = 0
            ),

            // ── HARD ──────────────────────────────────────────────────────────
            GameItem(
                id = GameId.SPEED_TYPER.id,
                icon = Icons.Rounded.Bolt,
                title = "Speed Typer",
                description = "Listen to a story excerpt once, then type exactly what you heard — speed and accuracy both matter!",
                skillTags = listOf(GameSkillTag.SPEED, GameSkillTag.MEMORY),
                difficulty = GameDifficulty.HARD,
                stars = 0,
                isLocked = true,
                xpReward = 0
            ),
            GameItem(
                id = GameId.FOLLOW_DIRECTIONS.id,
                icon = Icons.Rounded.CompassCalibration,
                title = "Follow the Directions",
                description = "Listen to directions inspired by the story, then guide the character through the scene correctly!",
                skillTags = listOf(GameSkillTag.DETAIL, GameSkillTag.COMPREHENSION),
                difficulty = GameDifficulty.HARD,
                stars = 0,
                isLocked = true,
                xpReward = 0
            ),
            GameItem(
                id = GameId.STORY_RECALL.id,
                icon = Icons.Rounded.Psychology,
                title = "Story Recall Challenge",
                description = "Remember the details — answer challenging questions about characters, events, and clues from the story!",
                skillTags = listOf(GameSkillTag.MEMORY, GameSkillTag.COMPREHENSION),
                difficulty = GameDifficulty.HARD,
                stars = 0,
                isLocked = true,
                xpReward = 0
            )
        )

        fun byDifficulty(difficulty: GameDifficulty) = all.filter { it.difficulty == difficulty }
    }
}