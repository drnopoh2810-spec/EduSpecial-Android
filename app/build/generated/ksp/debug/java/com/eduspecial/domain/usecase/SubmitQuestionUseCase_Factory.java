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
public final class SubmitQuestionUseCase_Factory implements Factory<SubmitQuestionUseCase> {
  private final Provider<QARepository> repositoryProvider;

  public SubmitQuestionUseCase_Factory(Provider<QARepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SubmitQuestionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SubmitQuestionUseCase_Factory create(Provider<QARepository> repositoryProvider) {
    return new SubmitQuestionUseCase_Factory(repositoryProvider);
  }

  public static SubmitQuestionUseCase newInstance(QARepository repository) {
    return new SubmitQuestionUseCase(repository);
  }
}
