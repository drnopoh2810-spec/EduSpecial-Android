package com.eduspecial.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class NotificationRepository_Factory implements Factory<NotificationRepository> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  private final Provider<FirebaseMessaging> messagingProvider;

  public NotificationRepository_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider, Provider<FirebaseMessaging> messagingProvider) {
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
    this.messagingProvider = messagingProvider;
  }

  @Override
  public NotificationRepository get() {
    return newInstance(firestoreProvider.get(), authProvider.get(), messagingProvider.get());
  }

  public static NotificationRepository_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider, Provider<FirebaseMessaging> messagingProvider) {
    return new NotificationRepository_Factory(firestoreProvider, authProvider, messagingProvider);
  }

  public static NotificationRepository newInstance(FirebaseFirestore firestore, FirebaseAuth auth,
      FirebaseMessaging messaging) {
    return new NotificationRepository(firestore, auth, messaging);
  }
}
