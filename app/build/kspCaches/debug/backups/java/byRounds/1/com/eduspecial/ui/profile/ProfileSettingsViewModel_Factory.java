package com.eduspecial.ui.profile;

import com.eduspecial.data.manager.RoleManager;
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
public final class ProfileSettingsViewModel_Factory implements Factory<ProfileSettingsViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<RoleManager> roleManagerProvider;

  public ProfileSettingsViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<RoleManager> roleManagerProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.roleManagerProvider = roleManagerProvider;
  }

  @Override
  public ProfileSettingsViewModel get() {
    return newInstance(authRepositoryProvider.get(), roleManagerProvider.get());
  }

  public static ProfileSettingsViewModel_Factory create(
      Provider<AuthRepository> authRepositoryProvider, Provider<RoleManager> roleManagerProvider) {
    return new ProfileSettingsViewModel_Factory(authRepositoryProvider, roleManagerProvider);
  }

  public static ProfileSettingsViewModel newInstance(AuthRepository authRepository,
      RoleManager roleManager) {
    return new ProfileSettingsViewModel(authRepository, roleManager);
  }
}
