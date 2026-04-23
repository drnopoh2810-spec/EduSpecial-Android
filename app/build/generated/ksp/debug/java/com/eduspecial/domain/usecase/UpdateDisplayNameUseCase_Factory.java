package com.eduspecial.domain.usecase;

import com.eduspecial.data.repository.AuthRepository;
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
public final class UpdateDisplayNameUseCase_Factory implements Factory<UpdateDisplayNameUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public UpdateDisplayNameUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdateDisplayNameUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdateDisplayNameUseCase_Factory create(
      Provider<AuthRepository> repositoryProvider) {
    return new UpdateDisplayNameUseCase_Factory(repositoryProvider);
  }

  public static UpdateDisplayNameUseCase newInstance(AuthRepository repository) {
    return new UpdateDisplayNameUseCase(repository);
  }
}
