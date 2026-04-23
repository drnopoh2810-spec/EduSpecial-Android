package com.eduspecial.data.remote.config;

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
public final class RemoteConfigManager_Factory implements Factory<RemoteConfigManager> {
  private final Provider<RuntimeConfigProvider> runtimeConfigProvider;

  public RemoteConfigManager_Factory(Provider<RuntimeConfigProvider> runtimeConfigProvider) {
    this.runtimeConfigProvider = runtimeConfigProvider;
  }

  @Override
  public RemoteConfigManager get() {
    return newInstance(runtimeConfigProvider.get());
  }

  public static RemoteConfigManager_Factory create(
      Provider<RuntimeConfigProvider> runtimeConfigProvider) {
    return new RemoteConfigManager_Factory(runtimeConfigProvider);
  }

  public static RemoteConfigManager newInstance(RuntimeConfigProvider runtimeConfigProvider) {
    return new RemoteConfigManager(runtimeConfigProvider);
  }
}
