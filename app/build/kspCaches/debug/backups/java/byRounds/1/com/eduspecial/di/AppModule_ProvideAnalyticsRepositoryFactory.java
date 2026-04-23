package com.eduspecial.di;

import com.eduspecial.data.local.dao.AnalyticsDao;
import com.eduspecial.data.repository.AnalyticsRepository;
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
public final class AppModule_ProvideAnalyticsRepositoryFactory implements Factory<AnalyticsRepository> {
  private final Provider<AnalyticsDao> analyticsDaoProvider;

  public AppModule_ProvideAnalyticsRepositoryFactory(Provider<AnalyticsDao> analyticsDaoProvider) {
    this.analyticsDaoProvider = analyticsDaoProvider;
  }

  @Override
  public AnalyticsRepository get() {
    return provideAnalyticsRepository(analyticsDaoProvider.get());
  }

  public static AppModule_ProvideAnalyticsRepositoryFactory create(
      Provider<AnalyticsDao> analyticsDaoProvider) {
    return new AppModule_ProvideAnalyticsRepositoryFactory(analyticsDaoProvider);
  }

  public static AnalyticsRepository provideAnalyticsRepository(AnalyticsDao analyticsDao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAnalyticsRepository(analyticsDao));
  }
}
