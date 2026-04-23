package com.eduspecial.update;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class UpdateViewModel_Factory implements Factory<UpdateViewModel> {
  private final Provider<UpdateRepository> repositoryProvider;

  private final Provider<Context> contextProvider;

  public UpdateViewModel_Factory(Provider<UpdateRepository> repositoryProvider,
      Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public UpdateViewModel get() {
    return newInstance(repositoryProvider.get(), contextProvider.get());
  }

  public static UpdateViewModel_Factory create(Provider<UpdateRepository> repositoryProvider,
      Provider<Context> contextProvider) {
    return new UpdateViewModel_Factory(repositoryProvider, contextProvider);
  }

  public static UpdateViewModel newInstance(UpdateRepository repository, Context context) {
    return new UpdateViewModel(repository, context);
  }
}
