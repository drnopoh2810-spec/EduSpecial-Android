package com.eduspecial.di;

import com.eduspecial.data.repository.NotificationRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AppModule_ProvideNotificationRepositoryFactory implements Factory<NotificationRepository> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  private final Provider<FirebaseMessaging> messagingProvider;

  public AppModule_ProvideNotificationRepositoryFactory(
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider,
      Provider<FirebaseMessaging> messagingProvider) {
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
    this.messagingProvider = messagingProvider;
  }

  @Override
  public NotificationRepository get() {
    return provideNotificationRepository(firestoreProvider.get(), authProvider.get(), messagingProvider.get());
  }

  public static AppModule_ProvideNotificationRepositoryFactory create(
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider,
      Provider<FirebaseMessaging> messagingProvider) {
    return new AppModule_ProvideNotificationRepositoryFactory(firestoreProvider, authProvider, messagingProvider);
  }

  public static NotificationRepository provideNotificationRepository(FirebaseFirestore firestore,
      FirebaseAuth auth, FirebaseMessaging messaging) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideNotificationRepository(firestore, auth, messaging));
  }
}
