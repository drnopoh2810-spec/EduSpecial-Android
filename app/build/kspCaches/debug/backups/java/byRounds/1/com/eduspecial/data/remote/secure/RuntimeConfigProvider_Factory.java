package com.eduspecial.data.remote.secure;

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
public final class RuntimeConfigProvider_Factory implements Factory<RuntimeConfigProvider> {
  private final Provider<RemoteConfigClient> clientProvider;

  private final Provider<RemoteConfigCache> cacheProvider;

  public RuntimeConfigProvider_Factory(Provider<RemoteConfigClient> clientProvider,
      Provider<RemoteConfigCache> cacheProvider) {
    this.clientProvider = clientProvider;
    this.cacheProvider = cacheProvider;
  }

  @Override
  public RuntimeConfigProvider get() {
    return newInstance(clientProvider.get(), cacheProvider.get());
  }

  public static RuntimeConfigProvider_Factory create(Provider<RemoteConfigClient> clientProvider,
      Provider<RemoteConfigCache> cacheProvider) {
    return new RuntimeConfigProvider_Factory(clientProvider, cacheProvider);
  }

  public static RuntimeConfigProvider newInstance(RemoteConfigClient client,
      RemoteConfigCache cache) {
    return new RuntimeConfigProvider(client, cache);
  }
}
