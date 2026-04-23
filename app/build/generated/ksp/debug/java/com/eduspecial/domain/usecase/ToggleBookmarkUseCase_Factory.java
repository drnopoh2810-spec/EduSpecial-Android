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
public final class ToggleBookmarkUseCase_Factory implements Factory<ToggleBookmarkUseCase> {
  private final Provider<BookmarkRepository> repositoryProvider;

  public ToggleBookmarkUseCase_Factory(Provider<BookmarkRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ToggleBookmarkUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ToggleBookmarkUseCase_Factory create(
      Provider<BookmarkRepository> repositoryProvider) {
    return new ToggleBookmarkUseCase_Factory(repositoryProvider);
  }

  public static ToggleBookmarkUseCase newInstance(BookmarkRepository repository) {
    return new ToggleBookmarkUseCase(repository);
  }
}
