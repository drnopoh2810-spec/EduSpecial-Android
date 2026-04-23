package com.eduspecial.domain.usecase;

import com.eduspecial.utils.NotificationScheduler;
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
public final class ScheduleStudyReminderUseCase_Factory implements Factory<ScheduleStudyReminderUseCase> {
  private final Provider<NotificationScheduler> schedulerProvider;

  public ScheduleStudyReminderUseCase_Factory(Provider<NotificationScheduler> schedulerProvider) {
    this.schedulerProvider = schedulerProvider;
  }

  @Override
  public ScheduleStudyReminderUseCase get() {
    return newInstance(schedulerProvider.get());
  }

  public static ScheduleStudyReminderUseCase_Factory create(
      Provider<NotificationScheduler> schedulerProvider) {
    return new ScheduleStudyReminderUseCase_Factory(schedulerProvider);
  }

  public static ScheduleStudyReminderUseCase newInstance(NotificationScheduler scheduler) {
    return new ScheduleStudyReminderUseCase(scheduler);
  }
}
