package com.eduspecial.data.repository;

import com.eduspecial.data.local.dao.FlashcardDao;
import com.eduspecial.utils.CircuitBreaker;
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
public final class FlashcardPagingRepository_Factory implements Factory<FlashcardPagingRepository> {
  private final Provider<FlashcardDao> flashcardDaoProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<CircuitBreaker> circuitBreakerProvider;

  public FlashcardPagingRepository_Factory(Provider<FlashcardDao> flashcardDaoProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<CircuitBreaker> circuitBreakerProvider) {
    this.flashcardDaoProvider = flashcardDaoProvider;
    this.firestoreProvider = firestoreProvider;
    this.circuitBreakerProvider = circuitBreakerProvider;
  }

  @Override
  public FlashcardPagingRepository get() {
    return newInstance(flashcardDaoProvider.get(), firestoreProvider.get(), circuitBreakerProvider.get());
  }

  public static FlashcardPagingRepository_Factory create(
      Provider<FlashcardDao> flashcardDaoProvider, Provider<FirebaseFirestore> firestoreProvider,
      Provider<CircuitBreaker> circuitBreakerProvider) {
    return new FlashcardPagingRepository_Factory(flashcardDaoProvider, firestoreProvider, circuitBreakerProvider);
  }

  public static FlashcardPagingRepository newInstance(FlashcardDao flashcardDao,
      FirebaseFirestore firestore, CircuitBreaker circuitBreaker) {
    return new FlashcardPagingRepository(flashcardDao, firestore, circuitBreaker);
  }
}
