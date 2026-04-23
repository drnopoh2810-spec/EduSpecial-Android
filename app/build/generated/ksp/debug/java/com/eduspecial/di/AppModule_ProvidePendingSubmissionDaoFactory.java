package com.eduspecial.di;

import com.eduspecial.data.local.EduSpecialDatabase;
import com.eduspecial.data.local.dao.PendingSubmissionDao;
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
public final class AppModule_ProvidePendingSubmissionDaoFactory implements Factory<PendingSubmissionDao> {
  private final Provider<EduSpecialDatabase> dbProvider;

  public AppModule_ProvidePendingSubmissionDaoFactory(Provider<EduSpecialDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PendingSubmissionDao get() {
    return providePendingSubmissionDao(dbProvider.get());
  }

  public static AppModule_ProvidePendingSubmissionDaoFactory create(
      Provider<EduSpecialDatabase> dbProvider) {
    return new AppModule_ProvidePendingSubmissionDaoFactory(dbProvider);
  }

  public static PendingSubmissionDao providePendingSubmissionDao(EduSpecialDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePendingSubmissionDao(db));
  }
}
