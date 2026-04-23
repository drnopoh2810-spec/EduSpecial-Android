package com.eduspecial.di;

import android.content.Context;
import com.eduspecial.data.remote.api.CloudinaryService;
import com.eduspecial.data.repository.ConfigRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideCloudinaryServiceFactory implements Factory<CloudinaryService> {
  private final Provider<Context> contextProvider;

  private final Provider<ConfigRepository> configRepositoryProvider;

  public AppModule_ProvideCloudinaryServiceFactory(Provider<Context> contextProvider,
      Provider<ConfigRepository> configRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.configRepositoryProvider = configRepositoryProvider;
  }

  @Override
  public CloudinaryService get() {
    return provideCloudinaryService(contextProvider.get(), configRepositoryProvider.get());
  }

  public static AppModule_ProvideCloudinaryServiceFactory create(Provider<Context> contextProvider,
      Provider<ConfigRepository> configRepositoryProvider) {
    return new AppModule_ProvideCloudinaryServiceFactory(contextProvider, configRepositoryProvider);
  }

  public static CloudinaryService provideCloudinaryService(Context context,
      ConfigRepository configRepository) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCloudinaryService(context, configRepository));
  }
}
