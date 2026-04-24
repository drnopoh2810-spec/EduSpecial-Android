package com.eduspecial.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.eduspecial.data.local.dao.FlashcardDao
import com.eduspecial.data.local.entities.FlashcardEntity
import com.eduspecial.utils.CircuitBreaker
import com.eduspecial.utils.CircuitOpenException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class FlashcardRemoteMediator(
    private val category: String?,
    private val firestore: FirebaseFirestore,
    private val flashcardDao: FlashcardDao,
    private val circuitBreaker: CircuitBreaker
) : RemoteMediator<Int, FlashcardEntity>() {

    private val col = firestore.collection("flashcards")
    private var lastDocumentSnapshot: com.google.firebase.firestore.DocumentSnapshot? = null

    override suspend fun initialize() = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FlashcardEntity>
    ): MediatorResult {

        return try {
            val query = when (loadType) {
                LoadType.REFRESH -> {
                    lastDocumentSnapshot = null
                    buildQuery(state.config.pageSize.toLong())
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastSnap = lastDocumentSnapshot
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    buildQuery(state.config.pageSize.toLong()).startAfter(lastSnap)
                }
            }

            val snap = circuitBreaker.execute { query.get().await() }

            if (loadType == LoadType.REFRESH) {
                if (category != null) flashcardDao.deleteByCategoryIfNotPending(category)
                else flashcardDao.deleteAllNotPending()
            }

            val entities = snap.documents.mapNotNull { doc ->
                try {
                    FlashcardEntity(
                        id = doc.id,
                        term = doc.getString("term") ?: return@mapNotNull null,
                        definition = doc.getString("definition") ?: return@mapNotNull null,
                        category = doc.getString("category") ?: "ABA_THERAPY",
                        mediaUrl = doc.getString("mediaUrl"),
                        mediaType = doc.getString("mediaType") ?: "NONE",
                        contributor = doc.getString("contributor") ?: "",
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                        reviewState = doc.getString("reviewState") ?: "NEW",
                        isPendingSync = false
                    )
                } catch (_: Exception) { null }
            }

            if (snap.documents.isNotEmpty()) {
                lastDocumentSnapshot = snap.documents.last()
            }

            flashcardDao.insertAll(entities)

            MediatorResult.Success(endOfPaginationReached = entities.size < state.config.pageSize)

        } catch (e: CircuitOpenException) {
            MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private fun buildQuery(limit: Long): Query {
        var q = col.orderBy("createdAt", Query.Direction.DESCENDING).limit(limit)
        if (category != null) q = col.whereEqualTo("category", category)
            .orderBy("createdAt", Query.Direction.DESCENDING).limit(limit)
        return q
    }
}
