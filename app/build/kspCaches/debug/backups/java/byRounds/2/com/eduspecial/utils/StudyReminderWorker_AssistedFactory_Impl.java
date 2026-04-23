package com.eduspecial.utils;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class StudyReminderWorker_AssistedFactory_Impl implements StudyReminderWorker_AssistedFactory {
  private final StudyReminderWorker_Factory delegateFactory;

  StudyReminderWorker_AssistedFactory_Impl(StudyReminderWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public StudyReminderWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<StudyReminderWorker_AssistedFactory> create(
      StudyReminderWorker_Factory delegateFactory) {
    return InstanceFactory.create(new StudyReminderWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<StudyReminderWorker_AssistedFactory> createFactoryProvider(
      StudyReminderWorker_Factory delegateFactory) {
    return InstanceFactory.create(new StudyReminderWorker_AssistedFactory_Impl(delegateFactory));
  }
}
