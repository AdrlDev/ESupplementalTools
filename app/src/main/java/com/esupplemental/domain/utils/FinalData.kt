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
                title = "God is Good",
                singer = "God is Good",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("4:29"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("VSktHtMLP-Q"), // Using placeholders for demo
                audioUrl = "https://drive.google.com/file/d/1JPrRIghg042QjkUDcAqbYHJybr7Eq5c5/view?usp=sharing",
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
                title = "I'm growing up",
                singer = "Kids Songs",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("2:18"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("b4tXwiBjkCk"),
                audioUrl = "https://drive.google.com/file/d/1gs5ARTY8G4IFJ4gakccM2mIhjZy9qIhc/view?usp=sharing",
            ),
            MediaItem(
                id = "s-7",
                title = "This is the day the lord has made",
                singer = "Christian Songs",
                type = MediaType.SONG,
                durationSeconds = Utility.timeToSeconds("2:04"),
                thumbnailUrl = YouTubeUtils.getThumbnailUrl("hBwM7M7ZIEQ"),
                audioUrl = "https://drive.google.com/file/d/1VmuP1CoYCT5jRrMu6NpBszpbopDUuxxe/view?usp=sharing",
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
    }
}
