package com.eduspecial.di;

import com.eduspecial.utils.ApiHealthMonitor;
import com.eduspecial.utils.CircuitBreaker;
import com.eduspecial.utils.NetworkMonitor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideApiHealthMonitorFactory implements Factory<ApiHealthMonitor> {
  private final Provider<CircuitBreaker> circuitBreakerProvider;

  private final Provider<NetworkMonitor> networkMonitorProvider;

  public AppModule_ProvideApiHealthMonitorFactory(Provider<CircuitBreaker> circuitBreakerProvider,
      Provider<NetworkMonitor> networkMonitorProvider) {
    this.circuitBreakerProvider = circuitBreakerProvider;
    this.networkMonitorProvider = networkMonitorProvider;
  }

  @Override
  public ApiHealthMonitor get() {
    return provideApiHealthMonitor(circuitBreakerProvider.get(), networkMonitorProvider.get());
  }

  public static AppModule_ProvideApiHealthMonitorFactory create(
      Provider<CircuitBreaker> circuitBreakerProvider,
      Provider<NetworkMonitor> networkMonitorProvider) {
    return new AppModule_ProvideApiHealthMonitorFactory(circuitBreakerProvider, networkMonitorProvider);
  }

  public static ApiHealthMonitor provideApiHealthMonitor(CircuitBreaker circuitBreaker,
      NetworkMonitor networkMonitor) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideApiHealthMonitor(circuitBreaker, networkMonitor));
  }
}
