package com.eduspecial.data.repository;

import com.eduspecial.data.manager.RoleManager;
import com.eduspecial.utils.UserPreferencesDataStore;
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
public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<UserPreferencesDataStore> prefsProvider;

  private final Provider<RoleManager> roleManagerProvider;

  public AuthRepository_Factory(Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<UserPreferencesDataStore> prefsProvider, Provider<RoleManager> roleManagerProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
    this.firestoreProvider = firestoreProvider;
    this.prefsProvider = prefsProvider;
    this.roleManagerProvider = roleManagerProvider;
  }

  @Override
  public AuthRepository get() {
    return newInstance(firebaseAuthProvider.get(), firestoreProvider.get(), prefsProvider.get(), roleManagerProvider.get());
  }

  public static AuthRepository_Factory create(Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<UserPreferencesDataStore> prefsProvider, Provider<RoleManager> roleManagerProvider) {
    return new AuthRepository_Factory(firebaseAuthProvider, firestoreProvider, prefsProvider, roleManagerProvider);
  }

  public static AuthRepository newInstance(FirebaseAuth firebaseAuth, FirebaseFirestore firestore,
      UserPreferencesDataStore prefs, RoleManager roleManager) {
    return new AuthRepository(firebaseAuth, firestore, prefs, roleManager);
  }
}
