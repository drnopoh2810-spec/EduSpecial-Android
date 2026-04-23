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
public final class SubmitAnswerUseCase_Factory implements Factory<SubmitAnswerUseCase> {
  private final Provider<QARepository> repositoryProvider;

  public SubmitAnswerUseCase_Factory(Provider<QARepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SubmitAnswerUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SubmitAnswerUseCase_Factory create(Provider<QARepository> repositoryProvider) {
    return new SubmitAnswerUseCase_Factory(repositoryProvider);
  }

  public static SubmitAnswerUseCase newInstance(QARepository repository) {
    return new SubmitAnswerUseCase(repository);
  }
}
