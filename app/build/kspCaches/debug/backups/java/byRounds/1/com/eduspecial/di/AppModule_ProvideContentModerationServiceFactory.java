package com.eduspecial.di;

import com.eduspecial.data.remote.moderation.ContentModerationService;
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
public final class AppModule_ProvideContentModerationServiceFactory implements Factory<ContentModerationService> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  public AppModule_ProvideContentModerationServiceFactory(
      Provider<FirebaseFirestore> firestoreProvider) {
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public ContentModerationService get() {
    return provideContentModerationService(firestoreProvider.get());
  }

  public static AppModule_ProvideContentModerationServiceFactory create(
      Provider<FirebaseFirestore> firestoreProvider) {
    return new AppModule_ProvideContentModerationServiceFactory(firestoreProvider);
  }

  public static ContentModerationService provideContentModerationService(
      FirebaseFirestore firestore) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideContentModerationService(firestore));
  }
}
