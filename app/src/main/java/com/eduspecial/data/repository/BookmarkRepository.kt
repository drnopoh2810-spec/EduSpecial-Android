package com.eduspecial.data.repository

import com.eduspecial.data.local.dao.BookmarkDao
import com.eduspecial.data.local.entities.BookmarkEntity
import com.eduspecial.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao
) {
    fun getAllBookmarks(): Flow<BookmarkCollection> {
        val flashcardIds = bookmarkDao.getBookmarksByType("FLASHCARD").map { list ->
            list.map { it.itemId }
        }
        val questionIds = bookmarkDao.getBookmarksByType("QUESTION").map { list ->
            list.map { it.itemId }
        }
        // Return a BookmarkCollection with just IDs — the ViewModel will join with actual data
        return combine(flashcardIds, questionIds) { fIds, qIds ->
            BookmarkCollection(
                flashcards = fIds.map { id ->
                    Flashcard(
                        id = id, term = "", definition = "",
                        category = FlashcardCategory.ABA_THERAPY,
                        contributor = ""
                    )
                },
                questions = qIds.map { id ->
                    QAQuestion(
                        id = id, question = "",
                        category = FlashcardCategory.ABA_THERAPY,
                        contributor = ""
                    )
                }
            )
        }
    }

    fun getBookmarkedFlashcardIds(): Flow<Set<String>> =
        bookmarkDao.getBookmarksByType("FLASHCARD").map { list -> list.map { it.itemId }.toSet() }

    fun getBookmarkedQuestionIds(): Flow<Set<String>> =
        bookmarkDao.getBookmarksByType("QUESTION").map { list -> list.map { it.itemId }.toSet() }

    fun isBookmarked(itemId: String, type: BookmarkType): Flow<Boolean> =
        bookmarkDao.isBookmarked(itemId, type.name)

    suspend fun toggle(itemId: String, type: BookmarkType): Boolean {
        val currentlyBookmarked = bookmarkDao.isBookmarked(itemId, type.name).first()
        return if (currentlyBookmarked) {
            bookmarkDao.delete(itemId, type.name)
            false
        } else {
            bookmarkDao.insert(BookmarkEntity(itemId = itemId, itemType = type.name))
            true
        }
    }

    suspend fun removeOrphans(validIds: List<String>) {
        if (validIds.isNotEmpty()) {
            // Get all bookmarked IDs and delete those not in validIds
            val allBookmarks = bookmarkDao.getAllBookmarks().first()
            val orphanIds = allBookmarks.map { it.itemId }.filter { it !in validIds }
            if (orphanIds.isNotEmpty()) {
                bookmarkDao.deleteOrphans(orphanIds)
            }
        }
    }
}
