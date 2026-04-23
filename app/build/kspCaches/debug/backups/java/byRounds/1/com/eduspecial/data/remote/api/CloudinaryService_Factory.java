package com.eduspecial.data.remote.api;

import android.content.Context;
import com.eduspecial.data.repository.ConfigRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CloudinaryService_Factory implements Factory<CloudinaryService> {
  private final Provider<Context> contextProvider;

  private final Provider<ConfigRepository> configRepositoryProvider;

  public CloudinaryService_Factory(Provider<Context> contextProvider,
      Provider<ConfigRepository> configRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.configRepositoryProvider = configRepositoryProvider;
  }

  @Override
  public CloudinaryService get() {
    return newInstance(contextProvider.get(), configRepositoryProvider.get());
  }

  public static CloudinaryService_Factory create(Provider<Context> contextProvider,
      Provider<ConfigRepository> configRepositoryProvider) {
    return new CloudinaryService_Factory(contextProvider, configRepositoryProvider);
  }

  public static CloudinaryService newInstance(Context context, ConfigRepository configRepository) {
    return new CloudinaryService(context, configRepository);
  }
}
