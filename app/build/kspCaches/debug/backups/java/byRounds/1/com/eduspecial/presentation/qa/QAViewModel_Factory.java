package com.eduspecial.presentation.qa;

import com.eduspecial.data.repository.AuthRepository;
import com.eduspecial.data.repository.BookmarkRepository;
import com.eduspecial.data.repository.QARepository;
import com.eduspecial.domain.usecase.AcceptAnswerUseCase;
import com.eduspecial.domain.usecase.EditAnswerUseCase;
import com.eduspecial.domain.usecase.EditQuestionUseCase;
import com.eduspecial.domain.usecase.ToggleBookmarkUseCase;
import com.eduspecial.domain.usecase.UpvoteAnswerUseCase;
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
public final class QAViewModel_Factory implements Factory<QAViewModel> {
  private final Provider<QARepository> repositoryProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<BookmarkRepository> bookmarkRepositoryProvider;

  private final Provider<EditQuestionUseCase> editQuestionUseCaseProvider;

  private final Provider<EditAnswerUseCase> editAnswerUseCaseProvider;

  private final Provider<AcceptAnswerUseCase> acceptAnswerUseCaseProvider;

  private final Provider<UpvoteAnswerUseCase> upvoteAnswerUseCaseProvider;

  private final Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider;

  public QAViewModel_Factory(Provider<QARepository> repositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<BookmarkRepository> bookmarkRepositoryProvider,
      Provider<EditQuestionUseCase> editQuestionUseCaseProvider,
      Provider<EditAnswerUseCase> editAnswerUseCaseProvider,
      Provider<AcceptAnswerUseCase> acceptAnswerUseCaseProvider,
      Provider<UpvoteAnswerUseCase> upvoteAnswerUseCaseProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider) {
    this.repositoryProvider = repositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.bookmarkRepositoryProvider = bookmarkRepositoryProvider;
    this.editQuestionUseCaseProvider = editQuestionUseCaseProvider;
    this.editAnswerUseCaseProvider = editAnswerUseCaseProvider;
    this.acceptAnswerUseCaseProvider = acceptAnswerUseCaseProvider;
    this.upvoteAnswerUseCaseProvider = upvoteAnswerUseCaseProvider;
    this.toggleBookmarkUseCaseProvider = toggleBookmarkUseCaseProvider;
  }

  @Override
  public QAViewModel get() {
    return newInstance(repositoryProvider.get(), authRepositoryProvider.get(), bookmarkRepositoryProvider.get(), editQuestionUseCaseProvider.get(), editAnswerUseCaseProvider.get(), acceptAnswerUseCaseProvider.get(), upvoteAnswerUseCaseProvider.get(), toggleBookmarkUseCaseProvider.get());
  }

  public static QAViewModel_Factory create(Provider<QARepository> repositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<BookmarkRepository> bookmarkRepositoryProvider,
      Provider<EditQuestionUseCase> editQuestionUseCaseProvider,
      Provider<EditAnswerUseCase> editAnswerUseCaseProvider,
      Provider<AcceptAnswerUseCase> acceptAnswerUseCaseProvider,
      Provider<UpvoteAnswerUseCase> upvoteAnswerUseCaseProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider) {
    return new QAViewModel_Factory(repositoryProvider, authRepositoryProvider, bookmarkRepositoryProvider, editQuestionUseCaseProvider, editAnswerUseCaseProvider, acceptAnswerUseCaseProvider, upvoteAnswerUseCaseProvider, toggleBookmarkUseCaseProvider);
  }

  public static QAViewModel newInstance(QARepository repository, AuthRepository authRepository,
      BookmarkRepository bookmarkRepository, EditQuestionUseCase editQuestionUseCase,
      EditAnswerUseCase editAnswerUseCase, AcceptAnswerUseCase acceptAnswerUseCase,
      UpvoteAnswerUseCase upvoteAnswerUseCase, ToggleBookmarkUseCase toggleBookmarkUseCase) {
    return new QAViewModel(repository, authRepository, bookmarkRepository, editQuestionUseCase, editAnswerUseCase, acceptAnswerUseCase, upvoteAnswerUseCase, toggleBookmarkUseCase);
  }
}
