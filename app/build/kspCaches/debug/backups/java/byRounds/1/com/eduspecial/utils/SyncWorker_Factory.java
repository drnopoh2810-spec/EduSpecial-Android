package com.eduspecial.utils;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.eduspecial.data.local.dao.PendingSubmissionDao;
import com.eduspecial.data.repository.BookmarkRepository;
import com.eduspecial.data.repository.FlashcardRepository;
import com.eduspecial.data.repository.QARepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SyncWorker_Factory {
  private final Provider<FlashcardRepository> flashcardRepositoryProvider;

  private final Provider<QARepository> qaRepositoryProvider;

  private final Provider<BookmarkRepository> bookmarkRepositoryProvider;

  private final Provider<PendingSubmissionDao> pendingDaoProvider;

  private final Provider<UserPreferencesDataStore> prefsProvider;

  private final Provider<NetworkMonitor> networkMonitorProvider;

  private final Provider<CircuitBreaker> circuitBreakerProvider;

  public SyncWorker_Factory(Provider<FlashcardRepository> flashcardRepositoryProvider,
      Provider<QARepository> qaRepositoryProvider,
      Provider<BookmarkRepository> bookmarkRepositoryProvider,
      Provider<PendingSubmissionDao> pendingDaoProvider,
      Provider<UserPreferencesDataStore> prefsProvider,
      Provider<NetworkMonitor> networkMonitorProvider,
      Provider<CircuitBreaker> circuitBreakerProvider) {
    this.flashcardRepositoryProvider = flashcardRepositoryProvider;
    this.qaRepositoryProvider = qaRepositoryProvider;
    this.bookmarkRepositoryProvider = bookmarkRepositoryProvider;
    this.pendingDaoProvider = pendingDaoProvider;
    this.prefsProvider = prefsProvider;
    this.networkMonitorProvider = networkMonitorProvider;
    this.circuitBreakerProvider = circuitBreakerProvider;
  }

  public SyncWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, flashcardRepositoryProvider.get(), qaRepositoryProvider.get(), bookmarkRepositoryProvider.get(), pendingDaoProvider.get(), prefsProvider.get(), networkMonitorProvider.get(), circuitBreakerProvider.get());
  }

  public static SyncWorker_Factory create(Provider<FlashcardRepository> flashcardRepositoryProvider,
      Provider<QARepository> qaRepositoryProvider,
      Provider<BookmarkRepository> bookmarkRepositoryProvider,
      Provider<PendingSubmissionDao> pendingDaoProvider,
      Provider<UserPreferencesDataStore> prefsProvider,
      Provider<NetworkMonitor> networkMonitorProvider,
      Provider<CircuitBreaker> circuitBreakerProvider) {
    return new SyncWorker_Factory(flashcardRepositoryProvider, qaRepositoryProvider, bookmarkRepositoryProvider, pendingDaoProvider, prefsProvider, networkMonitorProvider, circuitBreakerProvider);
  }

  public static SyncWorker newInstance(Context context, WorkerParameters workerParams,
      FlashcardRepository flashcardRepository, QARepository qaRepository,
      BookmarkRepository bookmarkRepository, PendingSubmissionDao pendingDao,
      UserPreferencesDataStore prefs, NetworkMonitor networkMonitor,
      CircuitBreaker circuitBreaker) {
    return new SyncWorker(context, workerParams, flashcardRepository, qaRepository, bookmarkRepository, pendingDao, prefs, networkMonitor, circuitBreaker);
  }
}
