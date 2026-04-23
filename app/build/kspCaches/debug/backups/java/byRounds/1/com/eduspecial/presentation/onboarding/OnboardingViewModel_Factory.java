package com.eduspecial.presentation.onboarding;

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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<UserPreferencesDataStore> prefsProvider;

  public OnboardingViewModel_Factory(Provider<UserPreferencesDataStore> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(prefsProvider.get());
  }

  public static OnboardingViewModel_Factory create(
      Provider<UserPreferencesDataStore> prefsProvider) {
    return new OnboardingViewModel_Factory(prefsProvider);
  }

  public static OnboardingViewModel newInstance(UserPreferencesDataStore prefs) {
    return new OnboardingViewModel(prefs);
  }
}
