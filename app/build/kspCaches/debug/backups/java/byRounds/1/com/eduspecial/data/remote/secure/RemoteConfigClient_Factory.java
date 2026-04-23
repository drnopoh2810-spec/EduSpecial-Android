package com.eduspecial.data.remote.secure;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class RemoteConfigClient_Factory implements Factory<RemoteConfigClient> {
  @Override
  public RemoteConfigClient get() {
    return newInstance();
  }

  public static RemoteConfigClient_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RemoteConfigClient newInstance() {
    return new RemoteConfigClient();
  }

  private static final class InstanceHolder {
    private static final RemoteConfigClient_Factory INSTANCE = new RemoteConfigClient_Factory();
  }
}
