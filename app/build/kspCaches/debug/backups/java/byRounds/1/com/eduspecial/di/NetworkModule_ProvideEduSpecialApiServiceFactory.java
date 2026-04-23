package com.eduspecial.di;

import com.eduspecial.data.remote.api.EduSpecialApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideEduSpecialApiServiceFactory implements Factory<EduSpecialApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideEduSpecialApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public EduSpecialApiService get() {
    return provideEduSpecialApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideEduSpecialApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideEduSpecialApiServiceFactory(retrofitProvider);
  }

  public static EduSpecialApiService provideEduSpecialApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideEduSpecialApiService(retrofit));
  }
}
