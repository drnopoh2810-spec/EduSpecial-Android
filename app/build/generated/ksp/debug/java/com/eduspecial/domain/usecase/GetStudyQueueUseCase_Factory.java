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
public final class GetStudyQueueUseCase_Factory implements Factory<GetStudyQueueUseCase> {
  private final Provider<FlashcardRepository> repositoryProvider;

  public GetStudyQueueUseCase_Factory(Provider<FlashcardRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetStudyQueueUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetStudyQueueUseCase_Factory create(
      Provider<FlashcardRepository> repositoryProvider) {
    return new GetStudyQueueUseCase_Factory(repositoryProvider);
  }

  public static GetStudyQueueUseCase newInstance(FlashcardRepository repository) {
    return new GetStudyQueueUseCase(repository);
  }
}
