package com.eduspecial.di;

import com.eduspecial.data.local.EduSpecialDatabase;
import com.eduspecial.data.local.dao.FlashcardDao;
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
public final class AppModule_ProvideFlashcardDaoFactory implements Factory<FlashcardDao> {
  private final Provider<EduSpecialDatabase> dbProvider;

  public AppModule_ProvideFlashcardDaoFactory(Provider<EduSpecialDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FlashcardDao get() {
    return provideFlashcardDao(dbProvider.get());
  }

  public static AppModule_ProvideFlashcardDaoFactory create(
      Provider<EduSpecialDatabase> dbProvider) {
    return new AppModule_ProvideFlashcardDaoFactory(dbProvider);
  }

  public static FlashcardDao provideFlashcardDao(EduSpecialDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFlashcardDao(db));
  }
}
