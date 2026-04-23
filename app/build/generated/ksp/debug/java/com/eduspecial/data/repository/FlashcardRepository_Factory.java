package com.eduspecial.data.repository;

import com.eduspecial.data.local.dao.FlashcardDao;
import com.eduspecial.data.local.dao.PendingSubmissionDao;
import com.eduspecial.data.remote.search.AlgoliaSearchService;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class FlashcardRepository_Factory implements Factory<FlashcardRepository> {
  private final Provider<FlashcardDao> flashcardDaoProvider;

  private final Provider<PendingSubmissionDao> pendingDaoProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<LeaderboardRepository> leaderboardRepositoryProvider;

  private final Provider<AlgoliaSearchService> algoliaSearchServiceProvider;

  private final Provider<ModerationRepository> moderationRepositoryProvider;

  public FlashcardRepository_Factory(Provider<FlashcardDao> flashcardDaoProvider,
      Provider<PendingSubmissionDao> pendingDaoProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<LeaderboardRepository> leaderboardRepositoryProvider,
      Provider<AlgoliaSearchService> algoliaSearchServiceProvider,
      Provider<ModerationRepository> moderationRepositoryProvider) {
    this.flashcardDaoProvider = flashcardDaoProvider;
    this.pendingDaoProvider = pendingDaoProvider;
    this.firestoreProvider = firestoreProvider;
    this.leaderboardRepositoryProvider = leaderboardRepositoryProvider;
    this.algoliaSearchServiceProvider = algoliaSearchServiceProvider;
    this.moderationRepositoryProvider = moderationRepositoryProvider;
  }

  @Override
  public FlashcardRepository get() {
    return newInstance(flashcardDaoProvider.get(), pendingDaoProvider.get(), firestoreProvider.get(), leaderboardRepositoryProvider.get(), algoliaSearchServiceProvider.get(), moderationRepositoryProvider.get());
  }

  public static FlashcardRepository_Factory create(Provider<FlashcardDao> flashcardDaoProvider,
      Provider<PendingSubmissionDao> pendingDaoProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<LeaderboardRepository> leaderboardRepositoryProvider,
      Provider<AlgoliaSearchService> algoliaSearchServiceProvider,
      Provider<ModerationRepository> moderationRepositoryProvider) {
    return new FlashcardRepository_Factory(flashcardDaoProvider, pendingDaoProvider, firestoreProvider, leaderboardRepositoryProvider, algoliaSearchServiceProvider, moderationRepositoryProvider);
  }

  public static FlashcardRepository newInstance(FlashcardDao flashcardDao,
      PendingSubmissionDao pendingDao, FirebaseFirestore firestore,
      LeaderboardRepository leaderboardRepository, AlgoliaSearchService algoliaSearchService,
      ModerationRepository moderationRepository) {
    return new FlashcardRepository(flashcardDao, pendingDao, firestore, leaderboardRepository, algoliaSearchService, moderationRepository);
  }
}
