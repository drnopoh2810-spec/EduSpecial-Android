package com.eduspecial.di;

import com.eduspecial.update.GitHubUpdateService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class UpdateModule_ProvideGitHubUpdateServiceFactory implements Factory<GitHubUpdateService> {
  private final Provider<Retrofit> retrofitProvider;

  public UpdateModule_ProvideGitHubUpdateServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public GitHubUpdateService get() {
    return provideGitHubUpdateService(retrofitProvider.get());
  }

  public static UpdateModule_ProvideGitHubUpdateServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new UpdateModule_ProvideGitHubUpdateServiceFactory(retrofitProvider);
  }

  public static GitHubUpdateService provideGitHubUpdateService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(UpdateModule.INSTANCE.provideGitHubUpdateService(retrofit));
  }
}
