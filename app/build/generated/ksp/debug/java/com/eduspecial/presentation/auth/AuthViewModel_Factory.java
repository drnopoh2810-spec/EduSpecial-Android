package com.eduspecial.presentation.auth;

import com.eduspecial.data.remote.secure.RuntimeConfigProvider;
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<RuntimeConfigProvider> runtimeConfigProvider;

  public AuthViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<RuntimeConfigProvider> runtimeConfigProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.runtimeConfigProvider = runtimeConfigProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(authRepositoryProvider.get(), runtimeConfigProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<RuntimeConfigProvider> runtimeConfigProvider) {
    return new AuthViewModel_Factory(authRepositoryProvider, runtimeConfigProvider);
  }

  public static AuthViewModel newInstance(AuthRepository authRepository,
      RuntimeConfigProvider runtimeConfigProvider) {
    return new AuthViewModel(authRepository, runtimeConfigProvider);
  }
}
