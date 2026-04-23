package com.eduspecial.ui.profile;

import com.eduspecial.data.manager.RoleManager;
import com.eduspecial.data.repository.AuthRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SecurityViewModel_Factory implements Factory<SecurityViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<RoleManager> roleManagerProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  public SecurityViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<RoleManager> roleManagerProvider, Provider<FirebaseFirestore> firestoreProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.roleManagerProvider = roleManagerProvider;
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public SecurityViewModel get() {
    return newInstance(authRepositoryProvider.get(), roleManagerProvider.get(), firestoreProvider.get());
  }

  public static SecurityViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<RoleManager> roleManagerProvider, Provider<FirebaseFirestore> firestoreProvider) {
    return new SecurityViewModel_Factory(authRepositoryProvider, roleManagerProvider, firestoreProvider);
  }

  public static SecurityViewModel newInstance(AuthRepository authRepository,
      RoleManager roleManager, FirebaseFirestore firestore) {
    return new SecurityViewModel(authRepository, roleManager, firestore);
  }
}
