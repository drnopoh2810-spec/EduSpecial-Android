package com.eduspecial.presentation.profile;

import com.eduspecial.data.repository.AuthRepository;
import com.eduspecial.data.repository.FlashcardRepository;
import com.eduspecial.domain.usecase.ScheduleStudyReminderUseCase;
import com.eduspecial.domain.usecase.UpdateDisplayNameUseCase;
import com.eduspecial.domain.usecase.UploadAvatarUseCase;
import com.eduspecial.utils.UserPreferencesDataStore;
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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<FlashcardRepository> flashcardRepositoryProvider;

  private final Provider<UserPreferencesDataStore> prefsProvider;

  private final Provider<UpdateDisplayNameUseCase> updateDisplayNameUseCaseProvider;

  private final Provider<UploadAvatarUseCase> uploadAvatarUseCaseProvider;

  private final Provider<ScheduleStudyReminderUseCase> scheduleStudyReminderUseCaseProvider;

  public ProfileViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<FlashcardRepository> flashcardRepositoryProvider,
      Provider<UserPreferencesDataStore> prefsProvider,
      Provider<UpdateDisplayNameUseCase> updateDisplayNameUseCaseProvider,
      Provider<UploadAvatarUseCase> uploadAvatarUseCaseProvider,
      Provider<ScheduleStudyReminderUseCase> scheduleStudyReminderUseCaseProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.flashcardRepositoryProvider = flashcardRepositoryProvider;
    this.prefsProvider = prefsProvider;
    this.updateDisplayNameUseCaseProvider = updateDisplayNameUseCaseProvider;
    this.uploadAvatarUseCaseProvider = uploadAvatarUseCaseProvider;
    this.scheduleStudyReminderUseCaseProvider = scheduleStudyReminderUseCaseProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(authRepositoryProvider.get(), flashcardRepositoryProvider.get(), prefsProvider.get(), updateDisplayNameUseCaseProvider.get(), uploadAvatarUseCaseProvider.get(), scheduleStudyReminderUseCaseProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<FlashcardRepository> flashcardRepositoryProvider,
      Provider<UserPreferencesDataStore> prefsProvider,
      Provider<UpdateDisplayNameUseCase> updateDisplayNameUseCaseProvider,
      Provider<UploadAvatarUseCase> uploadAvatarUseCaseProvider,
      Provider<ScheduleStudyReminderUseCase> scheduleStudyReminderUseCaseProvider) {
    return new ProfileViewModel_Factory(authRepositoryProvider, flashcardRepositoryProvider, prefsProvider, updateDisplayNameUseCaseProvider, uploadAvatarUseCaseProvider, scheduleStudyReminderUseCaseProvider);
  }

  public static ProfileViewModel newInstance(AuthRepository authRepository,
      FlashcardRepository flashcardRepository, UserPreferencesDataStore prefs,
      UpdateDisplayNameUseCase updateDisplayNameUseCase, UploadAvatarUseCase uploadAvatarUseCase,
      ScheduleStudyReminderUseCase scheduleStudyReminderUseCase) {
    return new ProfileViewModel(authRepository, flashcardRepository, prefs, updateDisplayNameUseCase, uploadAvatarUseCase, scheduleStudyReminderUseCase);
  }
}
