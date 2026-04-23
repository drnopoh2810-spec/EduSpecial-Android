package com.eduspecial.data.remote.secure;

import android.content.Context;
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
public final class RemoteConfigCache_Factory implements Factory<RemoteConfigCache> {
  private final Provider<Context> contextProvider;

  public RemoteConfigCache_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public RemoteConfigCache get() {
    return newInstance(contextProvider.get());
  }

  public static RemoteConfigCache_Factory create(Provider<Context> contextProvider) {
    return new RemoteConfigCache_Factory(contextProvider);
  }

  public static RemoteConfigCache newInstance(Context context) {
    return new RemoteConfigCache(context);
  }
}
