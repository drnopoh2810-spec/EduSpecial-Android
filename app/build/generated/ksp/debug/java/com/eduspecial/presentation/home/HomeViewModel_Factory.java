package com.eduspecial.presentation.home;

import com.eduspecial.data.repository.AnalyticsRepository;
import com.eduspecial.data.repository.FlashcardRepository;
import com.eduspecial.domain.usecase.GetCategoryMasteryUseCase;
import com.eduspecial.domain.usecase.GetStudyStreakUseCase;
import com.eduspecial.domain.usecase.GetWeeklyProgressUseCase;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<FlashcardRepository> flashcardRepositoryProvider;

  private final Provider<AnalyticsRepository> analyticsRepositoryProvider;

  private final Provider<GetStudyStreakUseCase> getStudyStreakProvider;

  private final Provider<GetWeeklyProgressUseCase> getWeeklyProgressProvider;

  private final Provider<GetCategoryMasteryUseCase> getCategoryMasteryProvider;

  private final Provider<UserPreferencesDataStore> userPreferencesDataStoreProvider;

  public HomeViewModel_Factory(Provider<FlashcardRepository> flashcardRepositoryProvider,
      Provider<AnalyticsRepository> analyticsRepositoryProvider,
      Provider<GetStudyStreakUseCase> getStudyStreakProvider,
      Provider<GetWeeklyProgressUseCase> getWeeklyProgressProvider,
      Provider<GetCategoryMasteryUseCase> getCategoryMasteryProvider,
      Provider<UserPreferencesDataStore> userPreferencesDataStoreProvider) {
    this.flashcardRepositoryProvider = flashcardRepositoryProvider;
    this.analyticsRepositoryProvider = analyticsRepositoryProvider;
    this.getStudyStreakProvider = getStudyStreakProvider;
    this.getWeeklyProgressProvider = getWeeklyProgressProvider;
    this.getCategoryMasteryProvider = getCategoryMasteryProvider;
    this.userPreferencesDataStoreProvider = userPreferencesDataStoreProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(flashcardRepositoryProvider.get(), analyticsRepositoryProvider.get(), getStudyStreakProvider.get(), getWeeklyProgressProvider.get(), getCategoryMasteryProvider.get(), userPreferencesDataStoreProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<FlashcardRepository> flashcardRepositoryProvider,
      Provider<AnalyticsRepository> analyticsRepositoryProvider,
      Provider<GetStudyStreakUseCase> getStudyStreakProvider,
      Provider<GetWeeklyProgressUseCase> getWeeklyProgressProvider,
      Provider<GetCategoryMasteryUseCase> getCategoryMasteryProvider,
      Provider<UserPreferencesDataStore> userPreferencesDataStoreProvider) {
    return new HomeViewModel_Factory(flashcardRepositoryProvider, analyticsRepositoryProvider, getStudyStreakProvider, getWeeklyProgressProvider, getCategoryMasteryProvider, userPreferencesDataStoreProvider);
  }

  public static HomeViewModel newInstance(FlashcardRepository flashcardRepository,
      AnalyticsRepository analyticsRepository, GetStudyStreakUseCase getStudyStreak,
      GetWeeklyProgressUseCase getWeeklyProgress, GetCategoryMasteryUseCase getCategoryMastery,
      UserPreferencesDataStore userPreferencesDataStore) {
    return new HomeViewModel(flashcardRepository, analyticsRepository, getStudyStreak, getWeeklyProgress, getCategoryMastery, userPreferencesDataStore);
  }
}
