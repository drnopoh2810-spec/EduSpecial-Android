package com.eduspecial.utils;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = StudyReminderWorker.class
)
public interface StudyReminderWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.eduspecial.utils.StudyReminderWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      StudyReminderWorker_AssistedFactory factory);
}
