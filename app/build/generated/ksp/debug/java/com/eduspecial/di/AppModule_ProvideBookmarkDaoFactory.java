package com.eduspecial.di;

import com.eduspecial.data.local.EduSpecialDatabase;
import com.eduspecial.data.local.dao.BookmarkDao;
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
public final class AppModule_ProvideBookmarkDaoFactory implements Factory<BookmarkDao> {
  private final Provider<EduSpecialDatabase> dbProvider;

  public AppModule_ProvideBookmarkDaoFactory(Provider<EduSpecialDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BookmarkDao get() {
    return provideBookmarkDao(dbProvider.get());
  }

  public static AppModule_ProvideBookmarkDaoFactory create(
      Provider<EduSpecialDatabase> dbProvider) {
    return new AppModule_ProvideBookmarkDaoFactory(dbProvider);
  }

  public static BookmarkDao provideBookmarkDao(EduSpecialDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBookmarkDao(db));
  }
}
