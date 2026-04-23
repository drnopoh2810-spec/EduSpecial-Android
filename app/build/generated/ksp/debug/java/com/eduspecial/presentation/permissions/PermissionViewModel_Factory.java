package com.eduspecial.presentation.permissions;

import com.eduspecial.utils.UserPreferencesDataStore;
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
public final class PermissionViewModel_Factory implements Factory<PermissionViewModel> {
  private final Provider<UserPreferencesDataStore> prefsProvider;

  public PermissionViewModel_Factory(Provider<UserPreferencesDataStore> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public PermissionViewModel get() {
    return newInstance(prefsProvider.get());
  }

  public static PermissionViewModel_Factory create(
      Provider<UserPreferencesDataStore> prefsProvider) {
    return new PermissionViewModel_Factory(prefsProvider);
  }

  public static PermissionViewModel newInstance(UserPreferencesDataStore prefs) {
    return new PermissionViewModel(prefs);
  }
}
