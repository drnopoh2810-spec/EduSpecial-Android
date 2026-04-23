package com.eduspecial.data.repository;

import com.eduspecial.data.remote.config.RemoteConfigManager;
import com.eduspecial.data.remote.secure.RuntimeConfigProvider;
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
public final class ConfigRepository_Factory implements Factory<ConfigRepository> {
  private final Provider<RuntimeConfigProvider> runtimeConfigProvider;

  private final Provider<RemoteConfigManager> remoteConfigManagerProvider;

  public ConfigRepository_Factory(Provider<RuntimeConfigProvider> runtimeConfigProvider,
      Provider<RemoteConfigManager> remoteConfigManagerProvider) {
    this.runtimeConfigProvider = runtimeConfigProvider;
    this.remoteConfigManagerProvider = remoteConfigManagerProvider;
  }

  @Override
  public ConfigRepository get() {
    return newInstance(runtimeConfigProvider.get(), remoteConfigManagerProvider.get());
  }

  public static ConfigRepository_Factory create(
      Provider<RuntimeConfigProvider> runtimeConfigProvider,
      Provider<RemoteConfigManager> remoteConfigManagerProvider) {
    return new ConfigRepository_Factory(runtimeConfigProvider, remoteConfigManagerProvider);
  }

  public static ConfigRepository newInstance(RuntimeConfigProvider runtimeConfigProvider,
      RemoteConfigManager remoteConfigManager) {
    return new ConfigRepository(runtimeConfigProvider, remoteConfigManager);
  }
}
