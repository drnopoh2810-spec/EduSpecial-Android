package com.eduspecial.presentation.leaderboard;

import com.eduspecial.data.repository.LeaderboardRepository;
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
public final class LeaderboardViewModel_Factory implements Factory<LeaderboardViewModel> {
  private final Provider<LeaderboardRepository> repositoryProvider;

  public LeaderboardViewModel_Factory(Provider<LeaderboardRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public LeaderboardViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static LeaderboardViewModel_Factory create(
      Provider<LeaderboardRepository> repositoryProvider) {
    return new LeaderboardViewModel_Factory(repositoryProvider);
  }

  public static LeaderboardViewModel newInstance(LeaderboardRepository repository) {
    return new LeaderboardViewModel(repository);
  }
}
