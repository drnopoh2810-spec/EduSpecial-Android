package com.eduspecial.presentation.flashcards;

import com.eduspecial.data.repository.FlashcardRepository;
import com.eduspecial.domain.usecase.RecordReviewUseCase;
import com.eduspecial.utils.TtsManager;
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
public final class StudyViewModel_Factory implements Factory<StudyViewModel> {
  private final Provider<FlashcardRepository> repositoryProvider;

  private final Provider<RecordReviewUseCase> recordReviewUseCaseProvider;

  private final Provider<TtsManager> ttsManagerProvider;

  public StudyViewModel_Factory(Provider<FlashcardRepository> repositoryProvider,
      Provider<RecordReviewUseCase> recordReviewUseCaseProvider,
      Provider<TtsManager> ttsManagerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.recordReviewUseCaseProvider = recordReviewUseCaseProvider;
    this.ttsManagerProvider = ttsManagerProvider;
  }

  @Override
  public StudyViewModel get() {
    return newInstance(repositoryProvider.get(), recordReviewUseCaseProvider.get(), ttsManagerProvider.get());
  }

  public static StudyViewModel_Factory create(Provider<FlashcardRepository> repositoryProvider,
      Provider<RecordReviewUseCase> recordReviewUseCaseProvider,
      Provider<TtsManager> ttsManagerProvider) {
    return new StudyViewModel_Factory(repositoryProvider, recordReviewUseCaseProvider, ttsManagerProvider);
  }

  public static StudyViewModel newInstance(FlashcardRepository repository,
      RecordReviewUseCase recordReviewUseCase, TtsManager ttsManager) {
    return new StudyViewModel(repository, recordReviewUseCase, ttsManager);
  }
}
