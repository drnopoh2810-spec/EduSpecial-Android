package com.eduspecial.presentation.search;

import com.eduspecial.data.remote.search.AlgoliaSearchService;
import com.eduspecial.data.repository.FlashcardRepository;
import com.eduspecial.data.repository.QARepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<FlashcardRepository> flashcardRepositoryProvider;

  private final Provider<QARepository> qaRepositoryProvider;

  private final Provider<AlgoliaSearchService> algoliaSearchServiceProvider;

  public SearchViewModel_Factory(Provider<FlashcardRepository> flashcardRepositoryProvider,
      Provider<QARepository> qaRepositoryProvider,
      Provider<AlgoliaSearchService> algoliaSearchServiceProvider) {
    this.flashcardRepositoryProvider = flashcardRepositoryProvider;
    this.qaRepositoryProvider = qaRepositoryProvider;
    this.algoliaSearchServiceProvider = algoliaSearchServiceProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(flashcardRepositoryProvider.get(), qaRepositoryProvider.get(), algoliaSearchServiceProvider.get());
  }

  public static SearchViewModel_Factory create(
      Provider<FlashcardRepository> flashcardRepositoryProvider,
      Provider<QARepository> qaRepositoryProvider,
      Provider<AlgoliaSearchService> algoliaSearchServiceProvider) {
    return new SearchViewModel_Factory(flashcardRepositoryProvider, qaRepositoryProvider, algoliaSearchServiceProvider);
  }

  public static SearchViewModel newInstance(FlashcardRepository flashcardRepository,
      QARepository qaRepository, AlgoliaSearchService algoliaSearchService) {
    return new SearchViewModel(flashcardRepository, qaRepository, algoliaSearchService);
  }
}
