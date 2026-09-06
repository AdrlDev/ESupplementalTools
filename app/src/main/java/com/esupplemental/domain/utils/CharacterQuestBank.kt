package com.esupplemental.domain.utils

import android.content.Context
import java.text.BreakIterator
import java.util.Locale

data class CharacterOption(
    val name: String,
    val roleOrTitle: String,
    val avatarEmoji: String,
    val colorHex: Long = 0xFF0797A5
)

data class CharacterQuestQuestion(
    val id: String,
    val storyTitle: String,
    val contextClue: String,
    val quoteOrTrait: String,
    val audioText: String,
    val characters: List<CharacterOption>,
    val correctIndex: Int,
    val explanation: String
)

/**
 * Dynamically builds Character Quest ("Who Said It? / Character Match") questions
 * directly from bundled story markdown (.md) resources.
 */
object CharacterQuestBank {

    private val blankLine = Regex("\\n\\s*\\n")
    private val stageDirection = Regex("\\[[^]]+]\\s*")
    private val brokenWord = Regex("(?<=\\p{L})-\\s+(?=\\p{Ll})")
    private val whitespace = Regex("\\s+")
    private val dialogueQuotes = Regex("[\"“](.*?)[\"”]")

    @Volatile
    private var cachedQuestions: List<CharacterQuestQuestion> = emptyList()

    private data class CharacterProfile(
        val storyId: String,
        val storyTitle: String,
        val character: CharacterOption,
        val keywords: List<String>,
        val defaultDistractors: List<CharacterOption>,
        val contextClue: String,
        val fallbackQuote: String,
        val explanation: String
    )

    private val characterProfiles = listOf(
        CharacterProfile(
            storyId = "t-2",
            storyTitle = "Black Beauty",
            character = CharacterOption("Black Beauty", "The Noble Horse", "🐴", 0xFF0B2E63),
            keywords = listOf("white star", "fine, black coat", "colts", "meadow", "bridle", "saddle", "saved us, Beauty", "horses"),
            defaultDistractors = listOf(
                CharacterOption("Farmer Grey", "The Kind Master", "👨‍🌾", 0xFF047857),
                CharacterOption("Ginger", "The Feisty Mare", "🐎", 0xFFB45309),
                CharacterOption("John Manly", "The Trusted Coachman", "🎩", 0xFF7C3AED)
            ),
            contextClue = "From the beloved tale of a faithful horse:",
            fallbackQuote = "\"I had a fine, black coat, one white foot, and a white star on my forehead. I always try my best to be gentle and brave.\"",
            explanation = "Black Beauty is the courageous horse with a white star who narrates the story!"
        ),
        CharacterProfile(
            storyId = "t-10",
            storyTitle = "The Prince and the Pauper",
            character = CharacterOption("Tom Canty", "The Dreamer Pauper", "👦", 0xFF0797A5),
            keywords = listOf("Offal Court", "pauper boy", "garment", "run races", "hunger", "dreams"),
            defaultDistractors = listOf(
                CharacterOption("Prince Edward", "Prince of Wales", "👑", 0xFFE3A008),
                CharacterOption("Miles Hendon", "The Brave Knight", "⚔️", 0xFFBE123C),
                CharacterOption("King Henry VIII", "The Royal King", "🏰", 0xFF6B21A8)
            ),
            contextClue = "From the royal switch in London:",
            fallbackQuote = "\"We boys of Offal Court run races to see who the fastest of us is, dreaming of kings and palaces.\"",
            explanation = "Tom Canty is the poor boy from Offal Court who loved to dream and ended up living in the palace!"
        ),
        CharacterProfile(
            storyId = "t-10",
            storyTitle = "The Prince and the Pauper",
            character = CharacterOption("Prince Edward", "Prince of Wales", "👑", 0xFFE3A008),
            keywords = listOf("Prince of Wales", "silk and satin", "How dare you", "Edward Tudor", "palace"),
            defaultDistractors = listOf(
                CharacterOption("Tom Canty", "The Dreamer Pauper", "👦", 0xFF0797A5),
                CharacterOption("Miles Hendon", "The Brave Knight", "⚔️", 0xFFBE123C),
                CharacterOption("The Gate Soldier", "Palace Guard", "🛡️", 0xFF047857)
            ),
            contextClue = "From the palace gates of Westminster:",
            fallbackQuote = "\"How dare you treat a poor boy that way! Open the gate and let the boy in!\"",
            explanation = "Prince Edward Tudor ordered the gates opened to welcome Tom Canty into the royal palace!"
        ),
        CharacterProfile(
            storyId = "t-8",
            storyTitle = "The Legends of Archimedes Go On",
            character = CharacterOption("Archimedes", "The Master Inventor", "🧪", 0xFF047857),
            keywords = listOf("Eureka", "golden crown", "water displaced", "tub", "lever", "pulley", "goldsmith"),
            defaultDistractors = listOf(
                CharacterOption("King Hiero II", "King of Syracuse", "👑", 0xFFE3A008),
                CharacterOption("Marcellus", "The Roman General", "🛡️", 0xFFBE123C),
                CharacterOption("The Goldsmith", "Crown Artisan", "💍", 0xFF0797A5)
            ),
            contextClue = "From ancient Syracuse and mathematical inventions:",
            fallbackQuote = "\"Eureka, Eureka! The amount of water that spills out solves the riddle of the golden crown!\"",
            explanation = "Archimedes shouted 'Eureka!' when he discovered water displacement to test the king's crown!"
        ),
        CharacterProfile(
            storyId = "t-5",
            storyTitle = "The Boy Who Brought Heaven Nearer",
            character = CharacterOption("Galileo Galilei", "The Pioneer Astronomer", "🔭", 0xFF0B2E63),
            keywords = listOf("telescope", "pendulum", "Jupiter", "stars", "Milky Way", "Tower of Pisa", "lenses"),
            defaultDistractors = listOf(
                CharacterOption("Hans Lippershey", "The Lens Maker", "👓", 0xFF0797A5),
                CharacterOption("Father Vincenzo", "The Scholar Priest", "⛪", 0xFF6B21A8),
                CharacterOption("The University Chancellor", "The Academic Leader", "📜", 0xFFB45309)
            ),
            contextClue = "From the starry discoveries of astronomy:",
            fallbackQuote = "\"I turned my telescope upon the heavens, and discovered moons moving around Jupiter!\"",
            explanation = "Galileo Galilei built the first astronomical telescope to explore the moons of Jupiter and the stars!"
        ),
        CharacterProfile(
            storyId = "t-6",
            storyTitle = "The Day the Frogs Cried",
            character = CharacterOption("The Wise Old Frog", "The Pond Guardian", "🐸", 0xFF047857),
            keywords = listOf("lily pad", "drying stream", "tadpoles", "cried", "pond", "water"),
            defaultDistractors = listOf(
                CharacterOption("Farmer Dan", "The Valley Farmer", "🚜", 0xFFB45309),
                CharacterOption("Young Tadpole", "The Little Swimmer", "🌊", 0xFF0797A5),
                CharacterOption("Mother Nature", "The Forest Spirit", "🍃", 0xFF7C3AED)
            ),
            contextClue = "From the lively pond ecosystem:",
            fallbackQuote = "\"From atop the lily pad, we must protect our fresh waters and guide the young tadpoles to safety.\"",
            explanation = "The Wise Old Frog used his experience to protect the pond creatures and care for the environment!"
        ),
        CharacterProfile(
            storyId = "t-3",
            storyTitle = "In Memory of Frankie",
            character = CharacterOption("Frankie", "The Loyal Shepherd Dog", "🐕", 0xFFB45309),
            keywords = listOf("Frankie", "barking", "loyal", "farm", "herd", "friendship", "friend"),
            defaultDistractors = listOf(
                CharacterOption("Young Leo", "Frankie's Best Friend", "🧒", 0xFF0797A5),
                CharacterOption("Grandpa Arthur", "The Farm Elder", "👴", 0xFF0B2E63),
                CharacterOption("Dr. Martinez", "The Town Vet", "🩺", 0xFF047857)
            ),
            contextClue = "From the heartwarming tribute to a loyal pet:",
            fallbackQuote = "\"I guarded the family farm with all my heart, barking bravely to protect everyone I loved.\"",
            explanation = "Frankie was the devoted, courageous dog whose loyalty and love left a lasting memory on the family!"
        ),
        CharacterProfile(
            storyId = "t-9",
            storyTitle = "The Notes of Note-Re Dame",
            character = CharacterOption("Quasimodo", "The Gentle Bellringer", "🔔", 0xFF6B21A8),
            keywords = listOf("bells", "towers", "Notre Dame", "Paris", "cathedral", "ringing", "notes"),
            defaultDistractors = listOf(
                CharacterOption("Captain Phoebus", "The City Guard", "🏇", 0xFFBE123C),
                CharacterOption("Esmeralda", "The Kind Dancer", "💃", 0xFFE3A008),
                CharacterOption("Archdeacon Frollo", "The Cathedral Elder", "⚖️", 0xFF0B2E63)
            ),
            contextClue = "From the bells of the great cathedral:",
            fallbackQuote = "\"High up in the cathedral towers, I rang the mighty bells that sang across the rooftops of Paris.\"",
            explanation = "Quasimodo lived in the towers of Notre Dame, ringing the historic bells with a gentle and loyal heart!"
        ),
        CharacterProfile(
            storyId = "t-7",
            storyTitle = "The Forerunners of Air Flight",
            character = CharacterOption("The Wright Brothers", "Aviation Pioneers", "✈️", 0xFF0B2E63),
            keywords = listOf("gliders", "Kitty Hawk", "airplane", "bicycle shop", "flight", "wings", "powered"),
            defaultDistractors = listOf(
                CharacterOption("Otto Lilienthal", "The Glider Champion", "🪂", 0xFF0797A5),
                CharacterOption("Daedalus", "The Mythical Maker", "🦅", 0xFFB45309),
                CharacterOption("The Balloonist", "The Cloud Explorer", "🎈", 0xFF7C3AED)
            ),
            contextClue = "From the birth of human aviation:",
            fallbackQuote = "\"Together in our bicycle workshop, we built gliders and achieved the world's first powered flight!\"",
            explanation = "Orville and Wilbur Wright persevered through experiments to achieve the first successful powered flight!"
        )
    )

    fun load(context: Context? = null): List<CharacterQuestQuestion> {
        if (cachedQuestions.isNotEmpty()) return cachedQuestions

        return synchronized(this) {
            if (cachedQuestions.isNotEmpty()) return cachedQuestions

            val generated = if (context != null) {
                buildDynamicQuestionsFromAssets(context)
            } else {
                buildStaticQuestions()
            }
            cachedQuestions = generated
            generated
        }
    }

    fun randomRounds(context: Context? = null, count: Int = 5, mediaId: String? = null): List<CharacterQuestQuestion> {
        val all = load(context)
        val filtered = if (!mediaId.isNullOrBlank()) {
            val matching = all.filter { it.storyTitle.contains(mediaId, ignoreCase = true) || it.id.contains(mediaId, ignoreCase = true) }
            matching.ifEmpty { all }
        } else {
            all
        }
        return filtered.shuffled().take(count.coerceAtMost(all.size))
    }

    private fun buildDynamicQuestionsFromAssets(context: Context): List<CharacterQuestQuestion> {
        val markdownMap = FinalData.stories.associate { story ->
            story.id to runCatching { StoryLoader.load(context, story.transcript) }.getOrNull()
        }

        return characterProfiles.mapIndexed { index, profile ->
            val storyMarkdown = markdownMap[profile.storyId]

            val dynamicQuote = if (!storyMarkdown.isNullOrBlank()) {
                extractBestQuoteForCharacter(storyMarkdown, profile)
            } else {
                profile.fallbackQuote
            }

            val cleanAudio = dynamicQuote
                .replace("\"", "")
                .replace("“", "")
                .replace("”", "")
                .trim()
                .let { if (!it.endsWith("Who am I?") && !it.endsWith("Who are we?")) "$it Who am I?" else it }

            val choices = mutableListOf<CharacterOption>()
            choices.add(profile.character)
            choices.addAll(profile.defaultDistractors.take(3))
            val shuffledChoices = choices.shuffled()
            val correctIdx = shuffledChoices.indexOf(profile.character).coerceAtLeast(0)

            CharacterQuestQuestion(
                id = AudioCacheKey.fromText(
                    prefix = "char_quest_dyn_${profile.storyId}_$index",
                    text = cleanAudio
                ),
                storyTitle = profile.storyTitle,
                contextClue = profile.contextClue,
                quoteOrTrait = if (!dynamicQuote.startsWith("\"")) "\"$dynamicQuote\"" else dynamicQuote,
                audioText = cleanAudio,
                characters = shuffledChoices,
                correctIndex = correctIdx,
                explanation = profile.explanation
            )
        }
    }

    private fun extractBestQuoteForCharacter(markdown: String, profile: CharacterProfile): String {
        val clean = cleanMarkdownText(markdown)
        val sentences = extractSentences(clean)

        // 1. Check for dialogue quotes containing keywords
        val dialogues = dialogueQuotes.findAll(clean).map { it.groupValues[1].trim() }.filter { it.length in 25..160 }.toList()
        for (dialogue in dialogues) {
            if (profile.keywords.any { k -> dialogue.contains(k, ignoreCase = true) }) {
                return "\"$dialogue\""
            }
        }

        // 2. Check for narration sentences containing character keywords
        val matchingSentence = sentences.firstOrNull { sentence ->
            sentence.length in 35..180 && profile.keywords.any { k -> sentence.contains(k, ignoreCase = true) }
        }

        return matchingSentence ?: profile.fallbackQuote
    }

    private fun extractSentences(cleanText: String): List<String> {
        val iterator = BreakIterator.getSentenceInstance(Locale.US)
        iterator.setText(cleanText)
        val list = mutableListOf<String>()
        var start = iterator.first()
        var end = iterator.next()
        while (end != BreakIterator.DONE) {
            val sentence = cleanText.substring(start, end).trim()
            if (sentence.isNotBlank()) list.add(sentence)
            start = end
            end = iterator.next()
        }
        return list
    }

    private fun cleanMarkdownText(markdown: String): String {
        return markdown
            .replace("\r\n", "\n")
            .split(blankLine)
            .asSequence()
            .map { paragraph ->
                if (paragraph.trimStart().startsWith("#")) ""
                else paragraph
                    .lineSequence()
                    .joinToString(" ") { it.trim() }
                    .replace(brokenWord, "")
                    .replace(stageDirection, "")
                    .replace(whitespace, " ")
                    .trim()
            }
            .filter { it.isNotBlank() }
            .joinToString("\n\n")
    }

    private fun buildStaticQuestions(): List<CharacterQuestQuestion> {
        return characterProfiles.mapIndexed { index, profile ->
            val choices = mutableListOf<CharacterOption>()
            choices.add(profile.character)
            choices.addAll(profile.defaultDistractors.take(3))
            val shuffledChoices = choices.shuffled()
            val correctIdx = shuffledChoices.indexOf(profile.character).coerceAtLeast(0)

            val cleanAudio = "${profile.fallbackQuote.replace("\"", "").replace("“", "").replace("”", "").trim()} Who am I?"

            CharacterQuestQuestion(
                id = AudioCacheKey.fromText(
                    prefix = "char_quest_stat_${profile.storyId}_$index",
                    text = cleanAudio
                ),
                storyTitle = profile.storyTitle,
                contextClue = profile.contextClue,
                quoteOrTrait = profile.fallbackQuote,
                audioText = cleanAudio,
                characters = shuffledChoices,
                correctIndex = correctIdx,
                explanation = profile.explanation
            )
        }
    }
}
