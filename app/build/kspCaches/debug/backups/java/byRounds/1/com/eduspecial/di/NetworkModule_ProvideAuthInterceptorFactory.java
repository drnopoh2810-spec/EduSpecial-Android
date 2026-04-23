package com.eduspecial.di;

import com.google.firebase.auth.FirebaseAuth;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.Interceptor;

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
public final class NetworkModule_ProvideAuthInterceptorFactory implements Factory<Interceptor> {
  private final Provider<FirebaseAuth> authProvider;

  public NetworkModule_ProvideAuthInterceptorFactory(Provider<FirebaseAuth> authProvider) {
    this.authProvider = authProvider;
  }

  @Override
  public Interceptor get() {
    return provideAuthInterceptor(authProvider.get());
  }

  public static NetworkModule_ProvideAuthInterceptorFactory create(
      Provider<FirebaseAuth> authProvider) {
    return new NetworkModule_ProvideAuthInterceptorFactory(authProvider);
  }

  public static Interceptor provideAuthInterceptor(FirebaseAuth auth) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAuthInterceptor(auth));
  }
}
