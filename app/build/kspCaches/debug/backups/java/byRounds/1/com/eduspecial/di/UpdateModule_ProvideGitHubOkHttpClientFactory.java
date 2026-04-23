package com.eduspecial.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class UpdateModule_ProvideGitHubOkHttpClientFactory implements Factory<OkHttpClient> {
  @Override
  public OkHttpClient get() {
    return provideGitHubOkHttpClient();
  }

  public static UpdateModule_ProvideGitHubOkHttpClientFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static OkHttpClient provideGitHubOkHttpClient() {
    return Preconditions.checkNotNullFromProvides(UpdateModule.INSTANCE.provideGitHubOkHttpClient());
  }

  private static final class InstanceHolder {
    private static final UpdateModule_ProvideGitHubOkHttpClientFactory INSTANCE = new UpdateModule_ProvideGitHubOkHttpClientFactory();
  }
}
