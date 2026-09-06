package com.esupplemental.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.esupplemental.data.converter.GameConverters
import com.esupplemental.data.converter.MediaConverters
import com.esupplemental.data.converter.NoteConverters
import com.esupplemental.data.converter.SongActivityConverters
import com.esupplemental.data.converter.StoryActivityConverters
import com.esupplemental.data.local.dao.*
import com.esupplemental.data.local.entity.*

@Database(
    entities = [
        UserEntity::class, MediaItemEntity::class, NoteEntity::class,
        SongActivityEntity::class, StoryActivityEntity::class, UserStatsEntity::class,
        QuizHistoryEntity::class, GameItemEntity::class, AudioStoryEntity::class
    ],
    version = 19,
    exportSchema = false          // set to true and provide a schema dir in prod
)
@TypeConverters(NoteConverters::class, MediaConverters::class, SongActivityConverters::class,
    StoryActivityConverters::class, GameConverters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS games_new (
                        userId TEXT NOT NULL,
                        id TEXT NOT NULL,
                        iconKey TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        skillTags TEXT NOT NULL,
                        difficulty TEXT NOT NULL,
                        stars INTEGER NOT NULL,
                        isLocked INTEGER NOT NULL,
                        xpReward INTEGER NOT NULL,
                        PRIMARY KEY(userId, id)
                    )""".trimIndent()
                )
                db.execSQL("DROP TABLE games")
                db.execSQL("ALTER TABLE games_new RENAME TO games")
            }
        }

        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """UPDATE user_stats SET
                        quizzesAverage = COALESCE((
                            SELECT AVG(CASE WHEN q.total > 0
                                THEN CAST(q.score AS REAL) / q.total ELSE 0 END)
                            FROM quiz_history q WHERE q.userId = user_stats.userId
                        ), 0),
                        overallScore = COALESCE((
                            SELECT SUM(q.score) FROM quiz_history q
                            WHERE q.userId = user_stats.userId
                        ), 0),
                        songsCompleted = (
                            SELECT COUNT(*) FROM quiz_history q
                            WHERE q.userId = user_stats.userId AND q.type = 'SONG'
                        ),
                        storiesCompleted = (
                            SELECT COUNT(*) FROM quiz_history q
                            WHERE q.userId = user_stats.userId AND q.type = 'STORY'
                        ),
                        level = 1 + ((
                            SELECT COUNT(*) FROM quiz_history q
                            WHERE q.userId = user_stats.userId
                        ) / 20),
                        levelProgress = ((
                            SELECT COUNT(*) FROM quiz_history q
                            WHERE q.userId = user_stats.userId
                        ) % 20) / 20.0
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_media_items_type_title " +
                        "ON media_items(type, title)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_notes_userId_createdAt " +
                        "ON notes(userId, createdAt)"
                )
            }
        }
    }

    abstract fun userDao(): UserDao
    abstract fun homeDao(): HomeDao
    abstract fun mediaDao(): MediaDao
    abstract fun quizDao(): QuizDao
    abstract fun progressDao(): ProgressDao
    abstract fun gameDao(): GameDao
    abstract fun noteDao(): NoteDao
}
