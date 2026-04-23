package com.eduspecial.update;

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
public final class UpdateRepository_Factory implements Factory<UpdateRepository> {
  private final Provider<GitHubUpdateService> serviceProvider;

  public UpdateRepository_Factory(Provider<GitHubUpdateService> serviceProvider) {
    this.serviceProvider = serviceProvider;
  }

  @Override
  public UpdateRepository get() {
    return newInstance(serviceProvider.get());
  }

  public static UpdateRepository_Factory create(Provider<GitHubUpdateService> serviceProvider) {
    return new UpdateRepository_Factory(serviceProvider);
  }

  public static UpdateRepository newInstance(GitHubUpdateService service) {
    return new UpdateRepository(service);
  }
}
