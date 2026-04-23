package com.eduspecial.di;

import com.eduspecial.data.local.EduSpecialDatabase;
import com.eduspecial.data.local.dao.AnalyticsDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideAnalyticsDaoFactory implements Factory<AnalyticsDao> {
  private final Provider<EduSpecialDatabase> dbProvider;

  public AppModule_ProvideAnalyticsDaoFactory(Provider<EduSpecialDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AnalyticsDao get() {
    return provideAnalyticsDao(dbProvider.get());
  }

  public static AppModule_ProvideAnalyticsDaoFactory create(
      Provider<EduSpecialDatabase> dbProvider) {
    return new AppModule_ProvideAnalyticsDaoFactory(dbProvider);
  }

  public static AnalyticsDao provideAnalyticsDao(EduSpecialDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAnalyticsDao(db));
  }
}
