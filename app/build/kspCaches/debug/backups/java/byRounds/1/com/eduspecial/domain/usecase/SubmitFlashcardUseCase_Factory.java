package com.eduspecial.domain.usecase;

import com.eduspecial.data.repository.FlashcardRepository;
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
public final class SubmitFlashcardUseCase_Factory implements Factory<SubmitFlashcardUseCase> {
  private final Provider<FlashcardRepository> repositoryProvider;

  public SubmitFlashcardUseCase_Factory(Provider<FlashcardRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SubmitFlashcardUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SubmitFlashcardUseCase_Factory create(
      Provider<FlashcardRepository> repositoryProvider) {
    return new SubmitFlashcardUseCase_Factory(repositoryProvider);
  }

  public static SubmitFlashcardUseCase newInstance(FlashcardRepository repository) {
    return new SubmitFlashcardUseCase(repository);
  }
}
