package com.eduspecial.di;

import com.eduspecial.data.remote.search.AlgoliaSearchService;
import com.eduspecial.data.repository.ConfigRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideAlgoliaSearchServiceFactory implements Factory<AlgoliaSearchService> {
  private final Provider<ConfigRepository> configRepositoryProvider;

  public AppModule_ProvideAlgoliaSearchServiceFactory(
      Provider<ConfigRepository> configRepositoryProvider) {
    this.configRepositoryProvider = configRepositoryProvider;
  }

  @Override
  public AlgoliaSearchService get() {
    return provideAlgoliaSearchService(configRepositoryProvider.get());
  }

  public static AppModule_ProvideAlgoliaSearchServiceFactory create(
      Provider<ConfigRepository> configRepositoryProvider) {
    return new AppModule_ProvideAlgoliaSearchServiceFactory(configRepositoryProvider);
  }

  public static AlgoliaSearchService provideAlgoliaSearchService(
      ConfigRepository configRepository) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAlgoliaSearchService(configRepository));
  }
}
