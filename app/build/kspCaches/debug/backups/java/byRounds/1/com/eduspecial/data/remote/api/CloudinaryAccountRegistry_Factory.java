package com.eduspecial.data.remote.api;

import com.eduspecial.data.repository.ConfigRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CloudinaryAccountRegistry_Factory implements Factory<CloudinaryAccountRegistry> {
  private final Provider<ConfigRepository> configRepositoryProvider;

  public CloudinaryAccountRegistry_Factory(Provider<ConfigRepository> configRepositoryProvider) {
    this.configRepositoryProvider = configRepositoryProvider;
  }

  @Override
  public CloudinaryAccountRegistry get() {
    return newInstance(configRepositoryProvider.get());
  }

  public static CloudinaryAccountRegistry_Factory create(
      Provider<ConfigRepository> configRepositoryProvider) {
    return new CloudinaryAccountRegistry_Factory(configRepositoryProvider);
  }

  public static CloudinaryAccountRegistry newInstance(ConfigRepository configRepository) {
    return new CloudinaryAccountRegistry(configRepository);
  }
}
