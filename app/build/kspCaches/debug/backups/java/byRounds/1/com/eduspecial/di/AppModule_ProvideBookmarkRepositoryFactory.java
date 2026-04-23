package com.eduspecial.di;

import com.eduspecial.data.local.dao.BookmarkDao;
import com.eduspecial.data.repository.BookmarkRepository;
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
public final class AppModule_ProvideBookmarkRepositoryFactory implements Factory<BookmarkRepository> {
  private final Provider<BookmarkDao> bookmarkDaoProvider;

  public AppModule_ProvideBookmarkRepositoryFactory(Provider<BookmarkDao> bookmarkDaoProvider) {
    this.bookmarkDaoProvider = bookmarkDaoProvider;
  }

  @Override
  public BookmarkRepository get() {
    return provideBookmarkRepository(bookmarkDaoProvider.get());
  }

  public static AppModule_ProvideBookmarkRepositoryFactory create(
      Provider<BookmarkDao> bookmarkDaoProvider) {
    return new AppModule_ProvideBookmarkRepositoryFactory(bookmarkDaoProvider);
  }

  public static BookmarkRepository provideBookmarkRepository(BookmarkDao bookmarkDao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBookmarkRepository(bookmarkDao));
  }
}
