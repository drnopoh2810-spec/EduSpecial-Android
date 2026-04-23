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
public final class CheckDuplicateQuestionUseCase_Factory implements Factory<CheckDuplicateQuestionUseCase> {
  private final Provider<QARepository> repositoryProvider;

  public CheckDuplicateQuestionUseCase_Factory(Provider<QARepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CheckDuplicateQuestionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static CheckDuplicateQuestionUseCase_Factory create(
      Provider<QARepository> repositoryProvider) {
    return new CheckDuplicateQuestionUseCase_Factory(repositoryProvider);
  }

  public static CheckDuplicateQuestionUseCase newInstance(QARepository repository) {
    return new CheckDuplicateQuestionUseCase(repository);
  }
}
