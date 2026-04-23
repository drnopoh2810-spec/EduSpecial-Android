package com.eduspecial.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.eduspecial.data.local.dao.FlashcardDao
import com.eduspecial.data.paging.FlashcardRemoteMediator
import com.eduspecial.domain.model.Flashcard
import com.eduspecial.utils.CircuitBreaker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashcardPagingRepository @Inject constructor(
    private val flashcardDao: FlashcardDao,
    private val firestore: FirebaseFirestore,
    private val circuitBreaker: CircuitBreaker
) {
    companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = 5
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getFlashcardsPaged(category: String? = null): Flow<PagingData<Flashcard>> {
        val pagingSourceFactory = if (category != null) {
            { flashcardDao.getFlashcardsPagedByCategory(category) }
        } else {
            { flashcardDao.getFlashcardsPaged() }
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE * 2
            ),
            remoteMediator = FlashcardRemoteMediator(
                category = category,
                firestore = firestore,
                flashcardDao = flashcardDao,
                circuitBreaker = circuitBreaker
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }
}
