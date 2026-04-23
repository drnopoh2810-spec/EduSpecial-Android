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
public final class EditAnswerUseCase_Factory implements Factory<EditAnswerUseCase> {
  private final Provider<QARepository> repositoryProvider;

  public EditAnswerUseCase_Factory(Provider<QARepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public EditAnswerUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static EditAnswerUseCase_Factory create(Provider<QARepository> repositoryProvider) {
    return new EditAnswerUseCase_Factory(repositoryProvider);
  }

  public static EditAnswerUseCase newInstance(QARepository repository) {
    return new EditAnswerUseCase(repository);
  }
}
