package com.eduspecial.di;

import com.eduspecial.data.manager.RoleManager;
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
public final class AppModule_ProvideRoleManagerFactory implements Factory<RoleManager> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  public AppModule_ProvideRoleManagerFactory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider) {
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
  }

  @Override
  public RoleManager get() {
    return provideRoleManager(firestoreProvider.get(), authProvider.get());
  }

  public static AppModule_ProvideRoleManagerFactory create(
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider) {
    return new AppModule_ProvideRoleManagerFactory(firestoreProvider, authProvider);
  }

  public static RoleManager provideRoleManager(FirebaseFirestore firestore, FirebaseAuth auth) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRoleManager(firestore, auth));
  }
}
