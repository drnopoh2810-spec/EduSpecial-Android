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
public final class RecordReviewUseCase_Factory implements Factory<RecordReviewUseCase> {
  private final Provider<AnalyticsRepository> repositoryProvider;

  public RecordReviewUseCase_Factory(Provider<AnalyticsRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RecordReviewUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static RecordReviewUseCase_Factory create(
      Provider<AnalyticsRepository> repositoryProvider) {
    return new RecordReviewUseCase_Factory(repositoryProvider);
  }

  public static RecordReviewUseCase newInstance(AnalyticsRepository repository) {
    return new RecordReviewUseCase(repository);
  }
}
