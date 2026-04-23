package com.eduspecial.domain.usecase;

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
public final class UpvoteAnswerUseCase_Factory implements Factory<UpvoteAnswerUseCase> {
  private final Provider<QARepository> repositoryProvider;

  public UpvoteAnswerUseCase_Factory(Provider<QARepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpvoteAnswerUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpvoteAnswerUseCase_Factory create(Provider<QARepository> repositoryProvider) {
    return new UpvoteAnswerUseCase_Factory(repositoryProvider);
  }

  public static UpvoteAnswerUseCase newInstance(QARepository repository) {
    return new UpvoteAnswerUseCase(repository);
  }
}
