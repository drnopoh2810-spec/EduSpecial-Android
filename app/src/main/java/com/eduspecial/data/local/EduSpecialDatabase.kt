package com.eduspecial.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eduspecial.data.local.dao.AnalyticsDao
import com.eduspecial.data.local.dao.BookmarkDao
import com.eduspecial.data.local.dao.FlashcardDao
import com.eduspecial.data.local.dao.PendingSubmissionDao
import com.eduspecial.data.local.dao.QADao
import com.eduspecial.data.local.entities.BookmarkEntity
import com.eduspecial.data.local.entities.DailyReviewLogEntity
import com.eduspecial.data.local.entities.FlashcardEntity
import com.eduspecial.data.local.entities.PendingSubmissionEntity
import com.eduspecial.data.local.entities.QAAnswerEntity
import com.eduspecial.data.local.entities.QAQuestionEntity

@Database(
    entities = [
        FlashcardEntity::class,
        QAQuestionEntity::class,
        QAAnswerEntity::class,
        PendingSubmissionEntity::class,
        BookmarkEntity::class,
        DailyReviewLogEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class EduSpecialDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
    abstract fun qaDao(): QADao
    abstract fun pendingSubmissionDao(): PendingSubmissionDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun analyticsDao(): AnalyticsDao

    companion object {
        const val DATABASE_NAME = "eduspecial_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS bookmarks (
                        id TEXT NOT NULL PRIMARY KEY,
                        itemId TEXT NOT NULL,
                        itemType TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """)
                database.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_bookmarks_itemId_itemType 
                    ON bookmarks (itemId, itemType)
                """)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_review_logs (
                        dayEpoch INTEGER NOT NULL PRIMARY KEY,
                        reviewCount INTEGER NOT NULL DEFAULT 0,
                        archivedCount INTEGER NOT NULL DEFAULT 0
                    )
                """)
            }
        }
    }
}
