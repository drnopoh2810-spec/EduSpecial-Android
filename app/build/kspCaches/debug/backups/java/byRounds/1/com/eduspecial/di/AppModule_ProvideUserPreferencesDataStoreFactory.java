package com.eduspecial.di;

import android.content.Context;
import com.eduspecial.utils.UserPreferencesDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideUserPreferencesDataStoreFactory implements Factory<UserPreferencesDataStore> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideUserPreferencesDataStoreFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public UserPreferencesDataStore get() {
    return provideUserPreferencesDataStore(contextProvider.get());
  }

  public static AppModule_ProvideUserPreferencesDataStoreFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvideUserPreferencesDataStoreFactory(contextProvider);
  }

  public static UserPreferencesDataStore provideUserPreferencesDataStore(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUserPreferencesDataStore(context));
  }
}
