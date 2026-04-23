package com.eduspecial.di;

import com.eduspecial.data.local.dao.FlashcardDao;
import com.eduspecial.data.repository.FlashcardPagingRepository;
import com.eduspecial.utils.CircuitBreaker;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideFlashcardPagingRepositoryFactory implements Factory<FlashcardPagingRepository> {
  private final Provider<FlashcardDao> flashcardDaoProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<CircuitBreaker> circuitBreakerProvider;

  public AppModule_ProvideFlashcardPagingRepositoryFactory(
      Provider<FlashcardDao> flashcardDaoProvider, Provider<FirebaseFirestore> firestoreProvider,
      Provider<CircuitBreaker> circuitBreakerProvider) {
    this.flashcardDaoProvider = flashcardDaoProvider;
    this.firestoreProvider = firestoreProvider;
    this.circuitBreakerProvider = circuitBreakerProvider;
  }

  @Override
  public FlashcardPagingRepository get() {
    return provideFlashcardPagingRepository(flashcardDaoProvider.get(), firestoreProvider.get(), circuitBreakerProvider.get());
  }

  public static AppModule_ProvideFlashcardPagingRepositoryFactory create(
      Provider<FlashcardDao> flashcardDaoProvider, Provider<FirebaseFirestore> firestoreProvider,
      Provider<CircuitBreaker> circuitBreakerProvider) {
    return new AppModule_ProvideFlashcardPagingRepositoryFactory(flashcardDaoProvider, firestoreProvider, circuitBreakerProvider);
  }

  public static FlashcardPagingRepository provideFlashcardPagingRepository(
      FlashcardDao flashcardDao, FirebaseFirestore firestore, CircuitBreaker circuitBreaker) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFlashcardPagingRepository(flashcardDao, firestore, circuitBreaker));
  }
}
