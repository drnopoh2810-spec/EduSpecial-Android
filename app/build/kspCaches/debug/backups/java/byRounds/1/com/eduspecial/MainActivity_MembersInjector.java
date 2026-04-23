package com.eduspecial;

import com.eduspecial.utils.UserPreferencesDataStore;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<UserPreferencesDataStore> prefsProvider;

  public MainActivity_MembersInjector(Provider<UserPreferencesDataStore> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<UserPreferencesDataStore> prefsProvider) {
    return new MainActivity_MembersInjector(prefsProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectPrefs(instance, prefsProvider.get());
  }

  @InjectedFieldSignature("com.eduspecial.MainActivity.prefs")
  public static void injectPrefs(MainActivity instance, UserPreferencesDataStore prefs) {
    instance.prefs = prefs;
  }
}
