package com.eduspecial.data.repository;

import com.eduspecial.data.local.dao.QADao;
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
public final class QARepository_Factory implements Factory<QARepository> {
  private final Provider<QADao> qaDaoProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<LeaderboardRepository> leaderboardRepositoryProvider;

  private final Provider<AlgoliaSearchService> algoliaSearchServiceProvider;

  private final Provider<ModerationRepository> moderationRepositoryProvider;

  public QARepository_Factory(Provider<QADao> qaDaoProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<LeaderboardRepository> leaderboardRepositoryProvider,
      Provider<AlgoliaSearchService> algoliaSearchServiceProvider,
      Provider<ModerationRepository> moderationRepositoryProvider) {
    this.qaDaoProvider = qaDaoProvider;
    this.firestoreProvider = firestoreProvider;
    this.leaderboardRepositoryProvider = leaderboardRepositoryProvider;
    this.algoliaSearchServiceProvider = algoliaSearchServiceProvider;
    this.moderationRepositoryProvider = moderationRepositoryProvider;
  }

  @Override
  public QARepository get() {
    return newInstance(qaDaoProvider.get(), firestoreProvider.get(), leaderboardRepositoryProvider.get(), algoliaSearchServiceProvider.get(), moderationRepositoryProvider.get());
  }

  public static QARepository_Factory create(Provider<QADao> qaDaoProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<LeaderboardRepository> leaderboardRepositoryProvider,
      Provider<AlgoliaSearchService> algoliaSearchServiceProvider,
      Provider<ModerationRepository> moderationRepositoryProvider) {
    return new QARepository_Factory(qaDaoProvider, firestoreProvider, leaderboardRepositoryProvider, algoliaSearchServiceProvider, moderationRepositoryProvider);
  }

  public static QARepository newInstance(QADao qaDao, FirebaseFirestore firestore,
      LeaderboardRepository leaderboardRepository, AlgoliaSearchService algoliaSearchService,
      ModerationRepository moderationRepository) {
    return new QARepository(qaDao, firestore, leaderboardRepository, algoliaSearchService, moderationRepository);
  }
}
