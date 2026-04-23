package com.eduspecial.di;

import com.google.firebase.messaging.FirebaseMessaging;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideFirebaseMessagingFactory implements Factory<FirebaseMessaging> {
  @Override
  public FirebaseMessaging get() {
    return provideFirebaseMessaging();
  }

  public static AppModule_ProvideFirebaseMessagingFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseMessaging provideFirebaseMessaging() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFirebaseMessaging());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideFirebaseMessagingFactory INSTANCE = new AppModule_ProvideFirebaseMessagingFactory();
  }
}
