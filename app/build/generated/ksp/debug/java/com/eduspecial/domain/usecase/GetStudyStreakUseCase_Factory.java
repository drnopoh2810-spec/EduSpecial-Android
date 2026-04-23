package com.eduspecial.domain.usecase;

import com.eduspecial.data.repository.AnalyticsRepository;
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
public final class GetStudyStreakUseCase_Factory implements Factory<GetStudyStreakUseCase> {
  private final Provider<AnalyticsRepository> repositoryProvider;

  public GetStudyStreakUseCase_Factory(Provider<AnalyticsRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetStudyStreakUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetStudyStreakUseCase_Factory create(
      Provider<AnalyticsRepository> repositoryProvider) {
    return new GetStudyStreakUseCase_Factory(repositoryProvider);
  }

  public static GetStudyStreakUseCase newInstance(AnalyticsRepository repository) {
    return new GetStudyStreakUseCase(repository);
  }
}
