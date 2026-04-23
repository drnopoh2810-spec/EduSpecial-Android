package com.eduspecial.presentation.bookmarks;

import com.eduspecial.domain.usecase.GetBookmarksUseCase;
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
public final class BookmarksViewModel_Factory implements Factory<BookmarksViewModel> {
  private final Provider<GetBookmarksUseCase> getBookmarksProvider;

  public BookmarksViewModel_Factory(Provider<GetBookmarksUseCase> getBookmarksProvider) {
    this.getBookmarksProvider = getBookmarksProvider;
  }

  @Override
  public BookmarksViewModel get() {
    return newInstance(getBookmarksProvider.get());
  }

  public static BookmarksViewModel_Factory create(
      Provider<GetBookmarksUseCase> getBookmarksProvider) {
    return new BookmarksViewModel_Factory(getBookmarksProvider);
  }

  public static BookmarksViewModel newInstance(GetBookmarksUseCase getBookmarks) {
    return new BookmarksViewModel(getBookmarks);
  }
}
