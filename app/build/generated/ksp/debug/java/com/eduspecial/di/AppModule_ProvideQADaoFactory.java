package com.eduspecial.di;

import com.eduspecial.data.local.EduSpecialDatabase;
import com.eduspecial.data.local.dao.QADao;
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
public final class AppModule_ProvideQADaoFactory implements Factory<QADao> {
  private final Provider<EduSpecialDatabase> dbProvider;

  public AppModule_ProvideQADaoFactory(Provider<EduSpecialDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public QADao get() {
    return provideQADao(dbProvider.get());
  }

  public static AppModule_ProvideQADaoFactory create(Provider<EduSpecialDatabase> dbProvider) {
    return new AppModule_ProvideQADaoFactory(dbProvider);
  }

  public static QADao provideQADao(EduSpecialDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideQADao(db));
  }
}
