package com.eduspecial.presentation.navigation;

import com.eduspecial.utils.ApiHealthMonitor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class ApiStatusViewModel_Factory implements Factory<ApiStatusViewModel> {
  private final Provider<ApiHealthMonitor> healthMonitorProvider;

  public ApiStatusViewModel_Factory(Provider<ApiHealthMonitor> healthMonitorProvider) {
    this.healthMonitorProvider = healthMonitorProvider;
  }

  @Override
  public ApiStatusViewModel get() {
    return newInstance(healthMonitorProvider.get());
  }

  public static ApiStatusViewModel_Factory create(
      Provider<ApiHealthMonitor> healthMonitorProvider) {
    return new ApiStatusViewModel_Factory(healthMonitorProvider);
  }

  public static ApiStatusViewModel newInstance(ApiHealthMonitor healthMonitor) {
    return new ApiStatusViewModel(healthMonitor);
  }
}
