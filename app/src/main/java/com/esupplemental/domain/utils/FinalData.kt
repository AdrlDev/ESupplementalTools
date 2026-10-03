package com.esupplemental.domain.utils

import com.esupplemental.R
import com.esupplemental.data.model.*

class FinalData {
    companion object {
        // ══════════════════════════════════════════════════════════════════════════
        // SONGS
        // ══════════════════════════════════════════════════════════════════════════

        val songs = listOf(
            // ── Popular / Uplifting ───────────────────────────────────────────────
            MediaItem(
                id = "s-1",
                title = "ARAL Summer Official Theme Song",
                singer = "ARAL Summer Official Theme Song",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("4:41"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("aept9iZDOVQ"),
                audioUrl = "https://drive.google.com/file/d/11kbkpo_QfE-UhKrkTCVov_XPKsiAxqvQ/view?usp=sharing",
            ),
            MediaItem(
                id = "s-2",
                title = "Ancient of Days",
                singer = "Ron Kenoly",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("7:51"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("V4dR_zuB3qk"),
                audioUrl = "https://drive.google.com/file/d/1Dam-mYTR6g3OmE65maP7WolSSvkNdFdn/view?usp=sharing",
            ),
            MediaItem(
                id = "s-3",
                title = "Books in the Old Testament",
                singer = "Children’s Songbook",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("2:58"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("FfAdqWZvgiU"),
                audioUrl = "https://drive.google.com/file/d/1n86kcxCcj6Nvev0fUMmBJAY3_AgNOZAw/view?usp=sharing",
            ),
            MediaItem(
                id = "s-4",
                title = "A Million Dreams",
                singer = "The Greatest Showman Cast",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("4:30"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("pSQk-4fddDI"),
                audioUrl = "https://drive.google.com/file/d/1yztcy7ein-99iGlPGUxkEt0W9TlOK4XN/view?usp=sharing",
            ),
            MediaItem(
                id = "s-5",
                title = "Hes got the whole world in his hands",
                singer = "Kids Songs",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("2:23"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("rAPNUVHLh1Q"),
                audioUrl = "https://drive.google.com/file/d/11zhAhjTul_kMDg4BKwEwZ0xEID3D9bvF/view?usp=sharing",
            ),
            MediaItem(
                id = "s-6",
                title = "Awit ng Anak sa Magulang",
                singer = "INC Music",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("4:29"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("fvA9qzAOqVg"),
                audioUrl = "https://drive.google.com/file/d/14oAJE6TmF4QCG0F-xFgHz9a1W6SHfPHG/view?usp=sharing",
            ),
            MediaItem(
                id = "s-7",
                title = "Tatsulok",
                singer = "Bamboo",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("5:13"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("SB5jUZrnbpg"),
                audioUrl = "https://drive.google.com/file/d/1cOteI6x5iwlwJRyBGhsHMpQlYzMWpdZQ/view?usp=sharing",
            ),
            MediaItem(
                id = "s-8",
                title = "Under the warka tree",
                singer = "(Under the warka tree)",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("5:37"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("Om8imqOBqoU"),
                audioUrl = "https://drive.google.com/file/d/1E8OlqE6NDfzQ-0BSombseJ3NSWuUHCwE/view?usp=sharing",
            ),
            MediaItem(
                id = "s-9",
                title = "Your ways better",
                singer = "CityAlight",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("3:18"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("sHMMs8j_ezs"),
                audioUrl = "https://drive.google.com/file/d/1puV5OjH0s1vMiwW5SA8eBB2lDPfMaXhV/view?usp=sharing",
            ),
            MediaItem(
                id = "s-10",
                title = "Salamat",
                singer = "Yeng Constantino",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("3:17"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("xOkBD4uPkcw"),
                audioUrl = "https://drive.google.com/file/d/1puzuH9hu3cGUIhOIeAfcjcspV6zeXgG4/view?usp=sharing",
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
                thumbnailRes = R.drawable.tilapia,
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
                thumbnailRes = R.drawable.black_beauty,
                transcript = "black_beauty.md",
                audioUrl = "https://drive.google.com/file/d/1vO-gYuqXEA6GI-tugFE97lmtC9BmTrV4/view?usp=sharing",
                moral = "Even through hardship and change, kindness, patience, and courage can help us endure and remain good."
            ),
            MediaItem(
                id = "t-3",
                title = "In Memory of Frankie",
                category = StoryCategory.KINDNESS,
                type = MediaType.STORY,
                durationSeconds = Utility.timeToSeconds("5:53"),
                thumbnailRes = R.drawable.in_memory_of_franky,
                transcript = "in_memory_of_frankie.md",
                moral = "True friendship is shown through love, loyalty, and remembering those who made a difference in our lives."
            ),

            MediaItem(
                id = "t-4",
                title = "Motorboat Miracle",
                category = StoryCategory.COURAGE,
                type = MediaType.STORY,
                durationSeconds = Utility.timeToSeconds("5:53"),
                thumbnailRes = R.drawable.motor_boat_miracle,
                transcript = "motorboat_miracle.md",
                moral = "Courage and quick thinking can help us overcome danger and protect others."
            ),

            MediaItem(
                id = "t-5",
                title = "The Boy Who Brought Heaven Nearer",
                category = StoryCategory.KINDNESS,
                type = MediaType.STORY,
                durationSeconds = Utility.timeToSeconds("5:53"),
                thumbnailRes = R.drawable.the_boy_who_brought_the_heaven_nearer,
                transcript = "the_boy_who_brought_heaven_nearer.md",
                moral = "A kind heart and willingness to help others can bring hope and happiness into their lives."
            ),

            MediaItem(
                id = "t-6",
                title = "The Day the Frogs Cried",
                category = StoryCategory.ENVIRONMENT,
                type = MediaType.STORY,
                durationSeconds = Utility.timeToSeconds("5:53"),
                thumbnailRes = R.drawable.the_day_the_frogs_cry,
                transcript = "the_day_the_frogs_cried.md",
                moral = "Every living creature has a place in nature, and we must care for and protect our environment."
            ),

            MediaItem(
                id = "t-7",
                title = "The Forerunners of Air Flight",
                category = StoryCategory.DETERMINATION,
                type = MediaType.STORY,
                durationSeconds = Utility.timeToSeconds("5:53"),
                thumbnailRes = R.drawable.the_forerunners,
                transcript = "the_forerunners_of_air_flight.md",
                moral = "Great achievements begin with curiosity, courage, and the determination to keep trying."
            ),

            MediaItem(
                id = "t-8",
                title = "The Legends of Archimedes Go On",
                category = StoryCategory.CURIOSITY,
                type = MediaType.STORY,
                durationSeconds = Utility.timeToSeconds("5:53"),
                thumbnailRes = R.drawable.the_legends_of_arch,
                transcript = "the_legends_of_archimedes_go_on.md",
                moral = "Curiosity and the desire to understand how things work can lead to discoveries that help others."
            ),

            MediaItem(
                id = "t-9",
                title = "The Notes of Note-Re Dame",
                category = StoryCategory.CREATIVITY,
                type = MediaType.STORY,
                durationSeconds = Utility.timeToSeconds("5:53"),
                thumbnailRes = R.drawable.the_note_of_note,
                transcript = "the_notes_of_note_re_dame.md",
                moral = "Creativity and imagination can help us discover our own strengths and make a difference."
            ),

            MediaItem(
                id = "t-10",
                title = "The Prince and the Pauper",
                category = StoryCategory.EMPATHY,
                type = MediaType.STORY,
                durationSeconds = Utility.timeToSeconds("5:53"),
                thumbnailRes = R.drawable.the_prince_and_the_pauper,
                transcript = "the_prince_and_the_pauper.md",
                moral = "Understanding another person's life teaches us empathy and reminds us that everyone deserves kindness and respect."
            )
        )

        // Poems use the same media/player pipeline as stories. The second Little Seed
        // entry is retained because the source document contains a distinct version.
        val poems = listOf(
            MediaItem("p-1", "The Little Seed", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_little_seed.md"),
            MediaItem("p-2", "The River's Journey", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_rivers_journey.md"),
            MediaItem("p-3", "The Empty Playground", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_empty_playground.md"),
            MediaItem("p-4", "The Lantern", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_lantern.md"),
            MediaItem("p-5", "The Bridge of Tomorrow", singer = "Devina Natali Putri", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_bridge_of_tomorrow.md"),
            MediaItem("p-6", "Mosquitoes In The Air", singer = "Bernard F. Asuncion", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/mosquitoes_in_the_air.md"),
            MediaItem("p-7", "Everything Is a Poem", singer = "J. Patrick Lewis", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/everything_is_a_poem.md"),
            MediaItem("p-8", "The Lost Words", singer = "Radclyffe Hall", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_lost_words.md"),
            MediaItem("p-9", "The Little Seed", singer = "Miguel Andres Reyes", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_little_seed_miguel_andres_reyes.md"),
            MediaItem("p-10", "The Morning of New Beginnings", singer = "Hannah Grace Mendoza", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_morning_of_new_beginnings.md"),
            MediaItem("p-11", "The Kindness Tree", singer = "Lucas James Rivera", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_kindness_tree.md"),
            MediaItem("p-12", "The Little Inventor", singer = "Ethan Miguel Garcia", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_little_inventor.md"),
            MediaItem("p-13", "The Journey of a Raindrop", singer = "Isabella Rose Flores", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_journey_of_a_raindrop.md"),
            MediaItem("p-14", "The Dream That Grew", singer = "Noah Daniel Bautista", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_dream_that_grew.md"),
            MediaItem("p-15", "A Friend Like the Sun", singer = "Daniel Matthew Cruz", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/a_friend_like_the_sun.md"),
            MediaItem("p-16", "The River's Song", singer = "Sofia Mae Villanueva", type = MediaType.POEM, durationSeconds = 0, transcript = "poems/the_rivers_song.md")
        )

        // ══════════════════════════════════════════════════════════════════════════
        // STORY ACTIVITIES
        // ══════════════════════════════════════════════════════════════════════════

        fun storyActivities(loadAsset: (String) -> String): Map<String, StoryActivity> =
            stories.associate { story ->
                story.id to StoryActivityFactory.create(
                    mediaId = story.id,
                    title = story.title,
                    markdown = loadAsset(story.transcript)
                )
            }

        fun poemActivities(loadAsset: (String) -> String): Map<String, StoryActivity> =
            poems.associate { poem ->
                poem.id to PoemActivityFactory.create(poem.id, loadAsset(poem.transcript))
            }

        private val songActivityAssets = mapOf(
            "s-1" to ("songs/questions/01_aral_summer_official_aral_summer_official_theme_song_questions.md" to "songs/answers/01_aral_summer_official_aral_summer_official_theme_song_answers.md"),
            "s-2" to ("songs/questions/02_ancient_of_days_ron_kenoly_questions.md" to "songs/answers/02_ancient_of_days_ron_kenoly_answers.md"),
            "s-3" to ("songs/questions/03_books_in_the_old_children's_songbook_questions.md" to "songs/answers/03_books_in_the_old_children's_songbook_answers.md"),
            "s-4" to ("songs/questions/06_a_million_dreams_questions.md" to "songs/answers/06_a_million_dreams_answers.md"),
            "s-5" to ("songs/questions/05_he's_got_the_whole_world_kids_songs_questions.md" to "songs/answers/05_he's_got_the_whole_world_kids_songs_answers.md"),
            "s-6" to ("songs/questions/04_awit_ng_anak_sa_magulang_questions.md" to "songs/answers/04_awit_ng_anak_sa_magulang_answers.md"),
            "s-7" to ("songs/questions/08_tatsulok_questions.md" to "songs/answers/08_tatsulok_answers.md"),
            "s-8" to ("songs/questions/09_under_the_warka_tree_questions.md" to "songs/answers/09_under_the_warka_tree_answers.md"),
            "s-9" to ("songs/questions/10_your_ways_better_cityalight_questions.md" to "songs/answers/10_your_ways_better_cityalight_answers.md"),
            "s-10" to ("songs/questions/07_salamat_questions.md" to "songs/answers/07_salamat_answers.md")
        )

        fun songActivities(loadAsset: (String) -> String): List<SongActivity> =
            songs.mapNotNull { song ->
                val assets = songActivityAssets[song.id] ?: return@mapNotNull null
                runCatching {
                    SongActivityFactory.create(
                        mediaId = song.id,
                        questionsMarkdown = loadAsset(assets.first),
                        answersMarkdown = loadAsset(assets.second)
                    )
                }.getOrNull()
            }
    }
}
