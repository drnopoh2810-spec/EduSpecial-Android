package com.eduspecial.presentation.flashcards;

import com.eduspecial.data.repository.AuthRepository;
import com.eduspecial.data.repository.BookmarkRepository;
import com.eduspecial.data.repository.FlashcardPagingRepository;
import com.eduspecial.data.repository.FlashcardRepository;
import com.eduspecial.domain.usecase.EditFlashcardUseCase;
import com.eduspecial.domain.usecase.ToggleBookmarkUseCase;
import com.eduspecial.utils.TtsManager;
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
public final class FlashcardsViewModel_Factory implements Factory<FlashcardsViewModel> {
  private final Provider<FlashcardRepository> repositoryProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<BookmarkRepository> bookmarkRepositoryProvider;

  private final Provider<EditFlashcardUseCase> editFlashcardUseCaseProvider;

  private final Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider;

  private final Provider<FlashcardPagingRepository> pagingRepositoryProvider;

  private final Provider<TtsManager> ttsManagerProvider;

  public FlashcardsViewModel_Factory(Provider<FlashcardRepository> repositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<BookmarkRepository> bookmarkRepositoryProvider,
      Provider<EditFlashcardUseCase> editFlashcardUseCaseProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider,
      Provider<FlashcardPagingRepository> pagingRepositoryProvider,
      Provider<TtsManager> ttsManagerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.bookmarkRepositoryProvider = bookmarkRepositoryProvider;
    this.editFlashcardUseCaseProvider = editFlashcardUseCaseProvider;
    this.toggleBookmarkUseCaseProvider = toggleBookmarkUseCaseProvider;
    this.pagingRepositoryProvider = pagingRepositoryProvider;
    this.ttsManagerProvider = ttsManagerProvider;
  }

  @Override
  public FlashcardsViewModel get() {
    return newInstance(repositoryProvider.get(), authRepositoryProvider.get(), bookmarkRepositoryProvider.get(), editFlashcardUseCaseProvider.get(), toggleBookmarkUseCaseProvider.get(), pagingRepositoryProvider.get(), ttsManagerProvider.get());
  }

  public static FlashcardsViewModel_Factory create(Provider<FlashcardRepository> repositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<BookmarkRepository> bookmarkRepositoryProvider,
      Provider<EditFlashcardUseCase> editFlashcardUseCaseProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider,
      Provider<FlashcardPagingRepository> pagingRepositoryProvider,
      Provider<TtsManager> ttsManagerProvider) {
    return new FlashcardsViewModel_Factory(repositoryProvider, authRepositoryProvider, bookmarkRepositoryProvider, editFlashcardUseCaseProvider, toggleBookmarkUseCaseProvider, pagingRepositoryProvider, ttsManagerProvider);
  }

  public static FlashcardsViewModel newInstance(FlashcardRepository repository,
      AuthRepository authRepository, BookmarkRepository bookmarkRepository,
      EditFlashcardUseCase editFlashcardUseCase, ToggleBookmarkUseCase toggleBookmarkUseCase,
      FlashcardPagingRepository pagingRepository, TtsManager ttsManager) {
    return new FlashcardsViewModel(repository, authRepository, bookmarkRepository, editFlashcardUseCase, toggleBookmarkUseCase, pagingRepository, ttsManager);
  }
}
