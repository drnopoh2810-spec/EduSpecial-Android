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
public final class AcceptAnswerUseCase_Factory implements Factory<AcceptAnswerUseCase> {
  private final Provider<QARepository> repositoryProvider;

  public AcceptAnswerUseCase_Factory(Provider<QARepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AcceptAnswerUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static AcceptAnswerUseCase_Factory create(Provider<QARepository> repositoryProvider) {
    return new AcceptAnswerUseCase_Factory(repositoryProvider);
  }

  public static AcceptAnswerUseCase newInstance(QARepository repository) {
    return new AcceptAnswerUseCase(repository);
  }
}
