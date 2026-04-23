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
public final class EditQuestionUseCase_Factory implements Factory<EditQuestionUseCase> {
  private final Provider<QARepository> repositoryProvider;

  public EditQuestionUseCase_Factory(Provider<QARepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public EditQuestionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static EditQuestionUseCase_Factory create(Provider<QARepository> repositoryProvider) {
    return new EditQuestionUseCase_Factory(repositoryProvider);
  }

  public static EditQuestionUseCase newInstance(QARepository repository) {
    return new EditQuestionUseCase(repository);
  }
}
