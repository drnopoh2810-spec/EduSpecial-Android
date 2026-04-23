package com.eduspecial.di

import android.content.Context
import androidx.room.Room
import com.eduspecial.BuildConfig
import com.eduspecial.data.local.EduSpecialDatabase
import com.eduspecial.data.local.dao.AnalyticsDao
import com.eduspecial.data.local.dao.BookmarkDao
import com.eduspecial.data.local.dao.FlashcardDao
import com.eduspecial.data.local.dao.PendingSubmissionDao
import com.eduspecial.data.local.dao.QADao
import com.eduspecial.data.remote.api.CloudinaryService
import com.eduspecial.data.repository.AnalyticsRepository
import com.eduspecial.data.repository.BookmarkRepository
import com.eduspecial.data.repository.FlashcardPagingRepository
import com.eduspecial.data.manager.RoleManager
import com.eduspecial.utils.ApiHealthMonitor
import com.eduspecial.utils.CircuitBreaker
import com.eduspecial.utils.NetworkMonitor
import com.eduspecial.utils.NotificationScheduler
import com.eduspecial.utils.RetryInterceptor
import com.eduspecial.utils.UserPreferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Firestore with persistent disk cache enabled.
     * This means the app works fully offline — Firestore serves from cache
     * when there's no network, and syncs automatically when back online.
     * This is the core of our "no backend needed" architecture.
     */
    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore {
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = firestoreSettings {
            // Persistent cache — survives app restarts, works offline
            setLocalCacheSettings(persistentCacheSettings {
                setSizeBytes(100 * 1024 * 1024L) // 100MB local cache
            })
        }
        return db
    }

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EduSpecialDatabase =
        Room.databaseBuilder(
            context,
            EduSpecialDatabase::class.java,
            EduSpecialDatabase.DATABASE_NAME
        )
            .addMigrations(EduSpecialDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideFlashcardDao(db: EduSpecialDatabase): FlashcardDao = db.flashcardDao()
    @Provides fun provideQADao(db: EduSpecialDatabase): QADao = db.qaDao()
    @Provides fun providePendingSubmissionDao(db: EduSpecialDatabase): PendingSubmissionDao = db.pendingSubmissionDao()
    @Provides fun provideBookmarkDao(db: EduSpecialDatabase): BookmarkDao = db.bookmarkDao()
    @Provides fun provideAnalyticsDao(db: EduSpecialDatabase): AnalyticsDao = db.analyticsDao()

    // RemoteConfigManager + ConfigRepository are auto-provided via @Inject constructors.

    @Provides @Singleton
    fun provideCloudinaryService(
        @ApplicationContext context: Context,
        configRepository: com.eduspecial.data.repository.ConfigRepository
    ): CloudinaryService = CloudinaryService(context, configRepository).also { it.initialize() }

    @Provides @Singleton
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context): UserPreferencesDataStore =
        UserPreferencesDataStore(context)

    @Provides @Singleton
    fun provideBookmarkRepository(bookmarkDao: BookmarkDao): BookmarkRepository =
        BookmarkRepository(bookmarkDao)

    @Provides @Singleton
    fun provideAnalyticsRepository(analyticsDao: AnalyticsDao): AnalyticsRepository =
        AnalyticsRepository(analyticsDao)

    @Provides @Singleton
    fun provideNotificationScheduler(@ApplicationContext context: Context): NotificationScheduler =
        NotificationScheduler(context)

    @Provides @Singleton
    fun provideTtsManager(@ApplicationContext context: Context): com.eduspecial.utils.TtsManager =
        com.eduspecial.utils.TtsManager(context)

    @Provides @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor =
        NetworkMonitor(context)

    @Provides @Singleton
    fun provideCircuitBreaker(): CircuitBreaker = CircuitBreaker()

    @Provides @Singleton
    fun provideApiHealthMonitor(
        circuitBreaker: CircuitBreaker,
        networkMonitor: NetworkMonitor
    ): ApiHealthMonitor = ApiHealthMonitor(circuitBreaker, networkMonitor)

    @Provides @Singleton
    fun provideFlashcardPagingRepository(
        flashcardDao: FlashcardDao,
        firestore: FirebaseFirestore,
        circuitBreaker: CircuitBreaker
    ): FlashcardPagingRepository = FlashcardPagingRepository(flashcardDao, firestore, circuitBreaker)

    @Provides @Singleton
    fun provideLeaderboardRepository(
        firestore: FirebaseFirestore,
        auth: com.google.firebase.auth.FirebaseAuth
    ): com.eduspecial.data.repository.LeaderboardRepository =
        com.eduspecial.data.repository.LeaderboardRepository(firestore, auth)

    @Provides @Singleton
    fun provideAlgoliaSearchService(
        configRepository: com.eduspecial.data.repository.ConfigRepository
    ): com.eduspecial.data.remote.search.AlgoliaSearchService =
        com.eduspecial.data.remote.search.AlgoliaSearchService(configRepository)

    @Provides @Singleton
    fun provideFirebaseMessaging(): com.google.firebase.messaging.FirebaseMessaging =
        com.google.firebase.messaging.FirebaseMessaging.getInstance()

    @Provides @Singleton
    fun provideNotificationRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        messaging: com.google.firebase.messaging.FirebaseMessaging
    ): com.eduspecial.data.repository.NotificationRepository =
        com.eduspecial.data.repository.NotificationRepository(firestore, auth, messaging)

    @Provides @Singleton
    fun provideContentModerationService(
        firestore: FirebaseFirestore
    ): com.eduspecial.data.remote.moderation.ContentModerationService =
        com.eduspecial.data.remote.moderation.ContentModerationService(firestore)

    @Provides @Singleton
    fun provideModerationRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        contentModerationService: com.eduspecial.data.remote.moderation.ContentModerationService
    ): com.eduspecial.data.repository.ModerationRepository =
        com.eduspecial.data.repository.ModerationRepository(firestore, auth, contentModerationService)

    @Provides @Singleton
    fun provideRoleManager(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): RoleManager = RoleManager(firestore, auth)
}
