package com.esupplemental.data.model

import com.esupplemental.R
import com.esupplemental.domain.utils.Utility
import com.esupplemental.domain.utils.YouTubeUtils

object FakeData {
    // ══════════════════════════════════════════════════════════════════════════
    // SONGS
    // ══════════════════════════════════════════════════════════════════════════

    val songs = listOf(
        // ── Popular / Uplifting ───────────────────────────────────────────────
        MediaItem(
            id = "pop_1",
            title = "When She Cries",
            singer = "Bugoy Drilon",
            type = MediaType.SONG,
            durationSeconds = Utility.timeToSeconds("3:25"),
            thumbnailUrl = YouTubeUtils.getThumbnailUrl("Th3YyonpS2c"),
            audioUrl = "https://drive.google.com/file/d/1bDtVF4Y1ISZ8MdMN0BD3HN0OisXtEPdM/view?usp=sharing",
            moral = "Appreciate what you have before it's gone."
        ),
        MediaItem(
            id = "pop_2",
            title = "Kalapastangan",
            singer = "fitterkarma",
            type = MediaType.SONG,
            durationSeconds = Utility.timeToSeconds("4:36"),
            thumbnailUrl = YouTubeUtils.getThumbnailUrl("lk5Tg6RB5ew"),
            audioUrl = "https://drive.google.com/file/d/1AH_Emua2-QQy-D0xuF87CBi5yxwpnC-D/view?usp=sharing",
            moral = "Love can make us vulnerable but also stronger."
        ),
        MediaItem(
            id = "pop_3",
            title = "Miracle Nights",
            singer = "Allmost",
            type = MediaType.SONG,
            durationSeconds = Utility.timeToSeconds("6:17"),
            thumbnailUrl = YouTubeUtils.getThumbnailUrl("7cgsJp_HFOA"),
            audioUrl = "https://drive.google.com/file/d/1GJWWFgVXa4TjdF4mQRvc6CGDGH53m1pb/view?usp=sharing",
            moral = "Every journey leads you to where you need to be."
        ),
        MediaItem(
            id = "pop_4",
            title = "Multo",
            singer = "Cup of Joe",
            type = MediaType.SONG,
            durationSeconds = Utility.timeToSeconds("3:57"),
            thumbnailUrl = YouTubeUtils.getThumbnailUrl("B33a8YkS-hU"), // Using placeholders for demo
            audioUrl = "https://drive.google.com/file/d/1XVfV8fqR-2WDkPaROR-E17xvOAOmRXfZ/view?usp=sharing",
            moral = "Practice makes perfect with math."
        ),
        MediaItem(
            id = "pop_5",
            title = "MYSB (Miss You so Bad)",
            singer = "Skusta Clee",
            type = MediaType.SONG,
            durationSeconds = Utility.timeToSeconds("4:44"),
            thumbnailUrl = YouTubeUtils.getThumbnailUrl("vLHH-ACg0o0"),
            audioUrl = "https://drive.google.com/file/d/1L6kEVL9S_lTouiCyhpBz2yPeqmq4lSvG/view?usp=sharing",
            moral = "Science is all around us every day."
        ),
        MediaItem(
            id = "pop_6",
            title = "Palayo Sa Mundo",
            singer = "Jolianne, Arthur Nery",
            type = MediaType.SONG,
            durationSeconds = Utility.timeToSeconds("4:51"),
            thumbnailUrl = YouTubeUtils.getThumbnailUrl("mVRAGW3TggU"),
            audioUrl = "https://drive.google.com/file/d/1dh4XRNZfcy9mQnPNQV_KQah8TkdHiio6/view?usp=sharing",
            moral = "The universe is vast and full of wonder."
        )
    )

    // ══════════════════════════════════════════════════════════════════════════
    // STORIES
    // ══════════════════════════════════════════════════════════════════════════

    val stories = listOf(
        MediaItem(
            id = "t-1",
            title = "A Turning Point for Tilapia",
            category = StoryCategory.PERSEVERANCE,
            type = MediaType.STORY,
            durationSeconds = Utility.timeToSeconds("5:25"),
            thumbnailRes = R.drawable.mouse_and_lion,
            transcript = "a_turning_point_for_tilapia_culture.md",
            audioUrl = "https://drive.google.com/file/d/1YLPh4NSkr7vUvWdAHfa7bi2pjjtymwbB/view?usp=sharing",
            moral = "With perseverance, responsibility, and a willingness to learn, challenges can become opportunities to make a lasting difference."
        ),
        MediaItem(
            id = "t-2",
            title = "Black Beauty",
            category = StoryCategory.DETERMINATION,
            type = MediaType.STORY,
            durationSeconds = Utility.timeToSeconds("5:53"),
            thumbnailRes = R.drawable.ann_gobles,
            transcript = "black_beauty.md",
            moral = "Even through hardship and change, kindness, patience, and courage can help us endure and remain good."
        )
    )

    // ══════════════════════════════════════════════════════════════════════════
    // SONG ACTIVITIES
    // ══════════════════════════════════════════════════════════════════════════

    val songActivities = mapOf(

        "pop_1" to SongActivity(
            mediaId = "pop_1",
            fillBlanks = listOf(
                FillBlankItem("pop1_fb1", "For a while there, it was ___.", "rough"),
                FillBlankItem("pop1_fb2", "I found a girl my parents ___.", "love"),
                FillBlankItem("pop1_fb3", "But I know the things He gives me, He can take ___.", "away"),
                FillBlankItem("pop1_fb4", "There's no man as ___ as the man who stands to lose you.", "terrified")
            ),
            messageQuestion = MultipleChoiceItem(
                id = "pop1_mc",
                question = "What is the main message of 'Beautiful Things'?",
                options = listOf(
                    "Money and success are the most important things",
                    "Appreciate what you have because it can be taken away",
                    "Love is always painful and difficult",
                    "Family is more important than friends"
                ),
                correctIndex = 1
            )
        ),

        "pop_2" to SongActivity(
            mediaId = "pop_2",
            fillBlanks = listOf(
                FillBlankItem("pop2_fb1", "I lose ___, when you're not next to me.", "control"),
                FillBlankItem("pop2_fb2", "Without you, nothing feels ___.", "complete"),
                FillBlankItem("pop2_fb3", "You are the ___ that I breathe.", "air")
            ),
            messageQuestion = MultipleChoiceItem(
                id = "pop2_mc",
                question = "What feeling does the singer describe in 'Lose Control'?",
                options = listOf(
                    "Happiness when alone",
                    "No control when someone is gone",
                    "Helplessness without a loved one",
                    "Excitement about the future"
                ),
                correctIndex = 2
            )
        ),

        "pop_3" to SongActivity(
            mediaId = "pop_3",
            fillBlanks = listOf(
                FillBlankItem("pop3_fb1", "It's nice to meet you, where you ___?", "been"),
                FillBlankItem("pop3_fb2", "I've been ___ all my life for this.", "waiting"),
                FillBlankItem("pop3_fb3", "Every moment led to you ___.", "somehow")
            ),
            messageQuestion = MultipleChoiceItem(
                id = "pop3_mc",
                question = "What does 'Nice To Meet You' suggest about life's journey?",
                options = listOf(
                    "Life is random and has no purpose",
                    "Every moment in life leads you to where you need to be",
                    "Meeting new people is always disappointing",
                    "The past doesn't matter"
                ),
                correctIndex = 1
            )
        ),

        "edu_1" to SongActivity(
            mediaId = "edu_1",
            fillBlanks = listOf(
                FillBlankItem("edu1_fb1", "Skip count by ___, that's the trick!", "six"),
                FillBlankItem("edu1_fb2", "6, 12, 18, ___, 30, 36.", "24"),
                FillBlankItem("edu1_fb3", "42, 48, 54, ___.", "60")
            ),
            messageQuestion = MultipleChoiceItem(
                id = "edu1_mc",
                question = "What is the next number when skip counting by 6 after 36?",
                options = listOf("40", "42", "44", "48"),
                correctIndex = 1
            )
        ),

        "edu_2" to SongActivity(
            mediaId = "edu_2",
            fillBlanks = listOf(
                FillBlankItem("edu2_fb1", "Evaporation, ___, precipitation!", "condensation"),
                FillBlankItem("edu2_fb2", "The sun heats up the ocean and the ___.", "lake"),
                FillBlankItem("edu2_fb3", "Water vapor rises, ___ it makes.", "clouds")
            ),
            messageQuestion = MultipleChoiceItem(
                id = "edu2_mc",
                question = "What is the correct order of the water cycle?",
                options = listOf(
                    "Precipitation → Condensation → Evaporation",
                    "Condensation → Evaporation → Precipitation",
                    "Evaporation → Condensation → Precipitation",
                    "Evaporation → Precipitation → Condensation"
                ),
                correctIndex = 2
            )
        ),

        "edu_3" to SongActivity(
            mediaId = "edu_3",
            fillBlanks = listOf(
                FillBlankItem("edu3_fb1", "Mercury, Venus, Earth, and ___.", "Mars"),
                FillBlankItem("edu3_fb2", "___, Saturn are the big stars.", "Jupiter"),
                FillBlankItem("edu3_fb3", "___ planets orbit every day!", "Eight")
            ),
            messageQuestion = MultipleChoiceItem(
                id = "edu3_mc",
                question = "How many planets are in our solar system?",
                options = listOf("Seven", "Eight", "Nine", "Ten"),
                correctIndex = 1
            )
        )
    )
}