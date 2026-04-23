package com.eduspecial.di;

import com.eduspecial.data.repository.LeaderboardRepository;
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
public final class AppModule_ProvideLeaderboardRepositoryFactory implements Factory<LeaderboardRepository> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  public AppModule_ProvideLeaderboardRepositoryFactory(
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider) {
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
  }

  @Override
  public LeaderboardRepository get() {
    return provideLeaderboardRepository(firestoreProvider.get(), authProvider.get());
  }

  public static AppModule_ProvideLeaderboardRepositoryFactory create(
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider) {
    return new AppModule_ProvideLeaderboardRepositoryFactory(firestoreProvider, authProvider);
  }

  public static LeaderboardRepository provideLeaderboardRepository(FirebaseFirestore firestore,
      FirebaseAuth auth) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideLeaderboardRepository(firestore, auth));
  }
}
