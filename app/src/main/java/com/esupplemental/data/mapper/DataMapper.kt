package com.esupplemental.data.mapper

import com.esupplemental.data.local.entity.*
import com.esupplemental.data.model.*
import com.esupplemental.domain.model.game.GameItem
import com.esupplemental.data.model.game.StoryEvent
import com.esupplemental.data.remote.model.WordTiming
import com.esupplemental.domain.utils.Utility
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataMapper {
    fun UserStatsEntity.toDomainModel(): UserStats {
        return UserStats(
            userName = this.userName,
            level = this.level,
            levelProgress = this.levelProgress,
            songsCompleted = this.songsCompleted,
            storiesCompleted = this.storiesCompleted,
            quizzesAverage = this.quizzesAverage,
            overallScore = this.overallScore
        )
    }

    fun UserStats.toEntity(userId: String): UserStatsEntity {
        return UserStatsEntity(
            userId = userId, // Domain model lacks ID, so we pass it here
            userName = this.userName,
            level = this.level,
            levelProgress = this.levelProgress,
            songsCompleted = this.songsCompleted,
            storiesCompleted = this.storiesCompleted,
            quizzesAverage = this.quizzesAverage,
            overallScore = this.overallScore
        )
    }

    fun MediaItem.toEntity(): MediaItemEntity {
        return MediaItemEntity(
            id = this.id,
            title = this.title,
            singer = this.singer,
            category = this.category,
            type = this.type,
            durationSeconds = this.durationSeconds,
            thumbnailRes = this.thumbnailRes ?: 0,
            thumbnailUrl = this.thumbnailUrl,
            audioRes = this.audioRes ?: 0, // Map audio resource
            audioUrl = this.audioUrl,
            transcript = this.transcript,
            moral = this.moral
        )
    }

    fun MediaItemEntity.toDomainModel(): MediaItem {
        return MediaItem(
            id = this.id,
            title = this.title,
            singer = this.singer,
            category = this.category,
            type = this.type,
            durationSeconds = this.durationSeconds,
            thumbnailRes = if (this.thumbnailRes == 0) null else this.thumbnailRes,
            thumbnailUrl = this.thumbnailUrl,
            audioRes = if (this.audioRes == 0) null else this.audioRes, // Map back to Domain
            audioUrl = this.audioUrl,
            transcript = this.transcript,
            moral = this.moral
        )
    }

    fun AudioStoryEntity.toDomainModel(gson: Gson = Gson()): AudioStory {
        val wordTimingType = object : TypeToken<List<WordTiming>>() {}.type
        return AudioStory(
            id = this.id,
            mediaId = this.mediaId,
            remoteId = this.remoteId,
            title = this.title,
            url = this.url,
            fileName = this.fileName,
            voiceId = this.voiceId,
            modelId = this.modelId,
            characterCount = this.characterCount,
            fileSize = this.fileSize,
            durationSeconds = this.durationSeconds,
            remoteCreatedAt = this.remoteCreatedAt,
            words = gson.fromJson(this.wordsJson ?: "[]", wordTimingType)
        )
    }

    fun AudioStory.toEntity(gson: Gson = Gson()): AudioStoryEntity {
        return AudioStoryEntity(
            mediaId = this.mediaId,
            remoteId = this.remoteId,
            title = this.title,
            url = this.url,
            fileName = this.fileName,
            voiceId = this.voiceId,
            modelId = this.modelId,
            characterCount = this.characterCount,
            fileSize = this.fileSize,
            durationSeconds = this.durationSeconds,
            remoteCreatedAt = this.remoteCreatedAt,
            wordsJson = gson.toJson(this.words)
        )
    }

    // Entity to Domain
    fun NoteEntity.toDomainModel(): Note {
        return Note(
            id = this.id,
            title = this.title,
            mainIdea = this.mainIdea,
            keyDetails = this.keyDetails,
            summary = this.summary,
            keywords = this.keywords,
            createdAt = this.createdAt
        )
    }

    // Domain to Entity
    fun Note.toEntity(userId: String): NoteEntity {
        return NoteEntity(
            id = this.id,
            userId = userId,
            title = this.title,
            mainIdea = this.mainIdea,
            keyDetails = this.keyDetails,
            summary = this.summary,
            keywords = this.keywords,
            createdAt = this.createdAt
        )
    }

    // Entity to Domain
    fun SongActivityEntity.toDomainModel(gson: Gson = Gson()): SongActivity {
        val fillBlanksType = object : TypeToken<List<FillBlankItem>>() {}.type
        return SongActivity(
            mediaId = this.mediaId,
            fillBlanks = gson.fromJson(this.fillBlanksJson, fillBlanksType),
            messageQuestion = gson.fromJson(this.messageQuestionJson, MultipleChoiceItem::class.java)
        )
    }

    // Domain to Entity
    fun SongActivity.toEntity(gson: Gson = Gson()): SongActivityEntity {
        return SongActivityEntity(
            mediaId = this.mediaId,
            fillBlanksJson = gson.toJson(this.fillBlanks) ,
            messageQuestionJson = gson.toJson(this.messageQuestion)
        )
    }

    // Entity to Domain
    fun StoryActivityEntity.toDomainModel(gson: Gson = Gson()): StoryActivity {
        val eventType = object : TypeToken<List<StoryEvent>>() {}.type
        val multipleChoiceType = object : TypeToken<List<MultipleChoiceQuestion>>() {}.type
        val questionType = object : TypeToken<List<OpenEndedQuestion>>() {}.type

        return StoryActivity(
            mediaId = this.mediaId,
            reorderEvents = gson.fromJson(this.reorderEventsJson, eventType),
            multipleChoiceQuestions = gson.fromJson(this.multipleChoiceQuestions, multipleChoiceType),
            openEnded = gson.fromJson(this.openEndedJson, questionType)
        )
    }

    // Domain to Entity
    fun StoryActivity.toEntity(gson: Gson = Gson()): StoryActivityEntity {
        return StoryActivityEntity(
            mediaId = this.mediaId,
            reorderEventsJson = gson.toJson(this.reorderEvents),
            multipleChoiceQuestions = gson.toJson(this.multipleChoiceQuestions),
            openEndedJson = gson.toJson(this.openEnded)
        )
    }

    // Entity to Domain
    fun UserEntity.toDomainModel(): User {
        return User(
            id = this.id,
            name = this.name,
            email = this.email,
            passwordHash = this.passwordHash,
            createdAt = this.createdAt
        )
    }

    // Domain to Entity
    fun User.toEntity(): UserEntity {
        return UserEntity(
            id = this.id,
            name = this.name,
            email = this.email,
            passwordHash = this.passwordHash,
            createdAt = this.createdAt
        )
    }

    fun GameItemEntity.toDomain(): GameItem {
        return GameItem(
            id = id,
            icon = Utility.getIconForKey(iconKey),
            title = title,
            description = description,
            skillTags = skillTags,
            difficulty = difficulty,
            stars = stars,
            isLocked = isLocked,
            xpReward = xpReward
        )
    }

    fun GameItem.toEntity(userId: String): GameItemEntity {
        return GameItemEntity(
            userId = userId,
            id = id,
            iconKey = id, // Uses the game ID as the iconKey string
            title = title,
            description = description,
            skillTags = skillTags,
            difficulty = difficulty,
            stars = stars,
            isLocked = isLocked,
            xpReward = xpReward
        )
    }
}
