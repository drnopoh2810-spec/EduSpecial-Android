package com.eduspecial.domain.usecase;

import com.eduspecial.data.repository.BookmarkRepository;
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
public final class GetBookmarksUseCase_Factory implements Factory<GetBookmarksUseCase> {
  private final Provider<BookmarkRepository> repositoryProvider;

  public GetBookmarksUseCase_Factory(Provider<BookmarkRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetBookmarksUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetBookmarksUseCase_Factory create(
      Provider<BookmarkRepository> repositoryProvider) {
    return new GetBookmarksUseCase_Factory(repositoryProvider);
  }

  public static GetBookmarksUseCase newInstance(BookmarkRepository repository) {
    return new GetBookmarksUseCase(repository);
  }
}
