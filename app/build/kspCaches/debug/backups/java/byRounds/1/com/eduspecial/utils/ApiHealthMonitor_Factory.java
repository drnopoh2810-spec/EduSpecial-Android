package com.eduspecial.utils;

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
public final class ApiHealthMonitor_Factory implements Factory<ApiHealthMonitor> {
  private final Provider<CircuitBreaker> circuitBreakerProvider;

  private final Provider<NetworkMonitor> networkMonitorProvider;

  public ApiHealthMonitor_Factory(Provider<CircuitBreaker> circuitBreakerProvider,
      Provider<NetworkMonitor> networkMonitorProvider) {
    this.circuitBreakerProvider = circuitBreakerProvider;
    this.networkMonitorProvider = networkMonitorProvider;
  }

  @Override
  public ApiHealthMonitor get() {
    return newInstance(circuitBreakerProvider.get(), networkMonitorProvider.get());
  }

  public static ApiHealthMonitor_Factory create(Provider<CircuitBreaker> circuitBreakerProvider,
      Provider<NetworkMonitor> networkMonitorProvider) {
    return new ApiHealthMonitor_Factory(circuitBreakerProvider, networkMonitorProvider);
  }

  public static ApiHealthMonitor newInstance(CircuitBreaker circuitBreaker,
      NetworkMonitor networkMonitor) {
    return new ApiHealthMonitor(circuitBreaker, networkMonitor);
  }
}
