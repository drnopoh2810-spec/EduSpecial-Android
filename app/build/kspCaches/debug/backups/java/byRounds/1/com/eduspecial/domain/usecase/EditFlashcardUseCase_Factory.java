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
public final class EditFlashcardUseCase_Factory implements Factory<EditFlashcardUseCase> {
  private final Provider<FlashcardRepository> repositoryProvider;

  public EditFlashcardUseCase_Factory(Provider<FlashcardRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public EditFlashcardUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static EditFlashcardUseCase_Factory create(
      Provider<FlashcardRepository> repositoryProvider) {
    return new EditFlashcardUseCase_Factory(repositoryProvider);
  }

  public static EditFlashcardUseCase newInstance(FlashcardRepository repository) {
    return new EditFlashcardUseCase(repository);
  }
}
