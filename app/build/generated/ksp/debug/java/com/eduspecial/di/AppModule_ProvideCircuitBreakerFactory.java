package com.eduspecial.di;

import com.eduspecial.utils.CircuitBreaker;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideCircuitBreakerFactory implements Factory<CircuitBreaker> {
  @Override
  public CircuitBreaker get() {
    return provideCircuitBreaker();
  }

  public static AppModule_ProvideCircuitBreakerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CircuitBreaker provideCircuitBreaker() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCircuitBreaker());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideCircuitBreakerFactory INSTANCE = new AppModule_ProvideCircuitBreakerFactory();
  }
}
