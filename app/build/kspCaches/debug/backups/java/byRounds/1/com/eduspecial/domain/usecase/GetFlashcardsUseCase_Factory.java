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
public final class GetFlashcardsUseCase_Factory implements Factory<GetFlashcardsUseCase> {
  private final Provider<FlashcardRepository> repositoryProvider;

  public GetFlashcardsUseCase_Factory(Provider<FlashcardRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetFlashcardsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetFlashcardsUseCase_Factory create(
      Provider<FlashcardRepository> repositoryProvider) {
    return new GetFlashcardsUseCase_Factory(repositoryProvider);
  }

  public static GetFlashcardsUseCase newInstance(FlashcardRepository repository) {
    return new GetFlashcardsUseCase(repository);
  }
}
