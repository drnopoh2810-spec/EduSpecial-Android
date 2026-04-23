package com.eduspecial.utils;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.eduspecial.data.repository.AnalyticsRepository;
import com.eduspecial.data.repository.FlashcardRepository;
import dagger.internal.DaggerGenerated;
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
public final class StudyReminderWorker_Factory {
  private final Provider<FlashcardRepository> flashcardRepositoryProvider;

  private final Provider<AnalyticsRepository> analyticsRepositoryProvider;

  private final Provider<UserPreferencesDataStore> prefsProvider;

  public StudyReminderWorker_Factory(Provider<FlashcardRepository> flashcardRepositoryProvider,
      Provider<AnalyticsRepository> analyticsRepositoryProvider,
      Provider<UserPreferencesDataStore> prefsProvider) {
    this.flashcardRepositoryProvider = flashcardRepositoryProvider;
    this.analyticsRepositoryProvider = analyticsRepositoryProvider;
    this.prefsProvider = prefsProvider;
  }

  public StudyReminderWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, flashcardRepositoryProvider.get(), analyticsRepositoryProvider.get(), prefsProvider.get());
  }

  public static StudyReminderWorker_Factory create(
      Provider<FlashcardRepository> flashcardRepositoryProvider,
      Provider<AnalyticsRepository> analyticsRepositoryProvider,
      Provider<UserPreferencesDataStore> prefsProvider) {
    return new StudyReminderWorker_Factory(flashcardRepositoryProvider, analyticsRepositoryProvider, prefsProvider);
  }

  public static StudyReminderWorker newInstance(Context context, WorkerParameters params,
      FlashcardRepository flashcardRepository, AnalyticsRepository analyticsRepository,
      UserPreferencesDataStore prefs) {
    return new StudyReminderWorker(context, params, flashcardRepository, analyticsRepository, prefs);
  }
}
