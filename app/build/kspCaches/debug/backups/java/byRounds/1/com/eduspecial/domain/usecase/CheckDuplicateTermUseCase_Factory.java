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
public final class CheckDuplicateTermUseCase_Factory implements Factory<CheckDuplicateTermUseCase> {
  private final Provider<FlashcardRepository> repositoryProvider;

  public CheckDuplicateTermUseCase_Factory(Provider<FlashcardRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CheckDuplicateTermUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static CheckDuplicateTermUseCase_Factory create(
      Provider<FlashcardRepository> repositoryProvider) {
    return new CheckDuplicateTermUseCase_Factory(repositoryProvider);
  }

  public static CheckDuplicateTermUseCase newInstance(FlashcardRepository repository) {
    return new CheckDuplicateTermUseCase(repository);
  }
}
