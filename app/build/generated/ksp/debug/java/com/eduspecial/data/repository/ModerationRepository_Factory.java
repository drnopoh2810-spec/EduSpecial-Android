package com.eduspecial.data.repository;

import com.eduspecial.data.remote.moderation.ContentModerationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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
public final class ModerationRepository_Factory implements Factory<ModerationRepository> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  private final Provider<ContentModerationService> contentModerationServiceProvider;

  public ModerationRepository_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider,
      Provider<ContentModerationService> contentModerationServiceProvider) {
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
    this.contentModerationServiceProvider = contentModerationServiceProvider;
  }

  @Override
  public ModerationRepository get() {
    return newInstance(firestoreProvider.get(), authProvider.get(), contentModerationServiceProvider.get());
  }

  public static ModerationRepository_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider,
      Provider<ContentModerationService> contentModerationServiceProvider) {
    return new ModerationRepository_Factory(firestoreProvider, authProvider, contentModerationServiceProvider);
  }

  public static ModerationRepository newInstance(FirebaseFirestore firestore, FirebaseAuth auth,
      ContentModerationService contentModerationService) {
    return new ModerationRepository(firestore, auth, contentModerationService);
  }
}
