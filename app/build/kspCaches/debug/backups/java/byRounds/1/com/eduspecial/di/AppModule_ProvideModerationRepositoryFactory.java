package com.eduspecial.di;

import com.eduspecial.data.remote.moderation.ContentModerationService;
import com.eduspecial.data.repository.ModerationRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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
public final class AppModule_ProvideModerationRepositoryFactory implements Factory<ModerationRepository> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  private final Provider<ContentModerationService> contentModerationServiceProvider;

  public AppModule_ProvideModerationRepositoryFactory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider,
      Provider<ContentModerationService> contentModerationServiceProvider) {
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
    this.contentModerationServiceProvider = contentModerationServiceProvider;
  }

  @Override
  public ModerationRepository get() {
    return provideModerationRepository(firestoreProvider.get(), authProvider.get(), contentModerationServiceProvider.get());
  }

  public static AppModule_ProvideModerationRepositoryFactory create(
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider,
      Provider<ContentModerationService> contentModerationServiceProvider) {
    return new AppModule_ProvideModerationRepositoryFactory(firestoreProvider, authProvider, contentModerationServiceProvider);
  }

  public static ModerationRepository provideModerationRepository(FirebaseFirestore firestore,
      FirebaseAuth auth, ContentModerationService contentModerationService) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideModerationRepository(firestore, auth, contentModerationService));
  }
}
