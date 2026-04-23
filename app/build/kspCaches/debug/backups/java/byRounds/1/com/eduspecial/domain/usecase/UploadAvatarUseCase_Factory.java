package com.eduspecial.domain.usecase;

import com.eduspecial.data.remote.api.CloudinaryService;
import com.eduspecial.data.repository.AuthRepository;
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
public final class UploadAvatarUseCase_Factory implements Factory<UploadAvatarUseCase> {
  private final Provider<CloudinaryService> cloudinaryProvider;

  private final Provider<AuthRepository> repositoryProvider;

  public UploadAvatarUseCase_Factory(Provider<CloudinaryService> cloudinaryProvider,
      Provider<AuthRepository> repositoryProvider) {
    this.cloudinaryProvider = cloudinaryProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UploadAvatarUseCase get() {
    return newInstance(cloudinaryProvider.get(), repositoryProvider.get());
  }

  public static UploadAvatarUseCase_Factory create(Provider<CloudinaryService> cloudinaryProvider,
      Provider<AuthRepository> repositoryProvider) {
    return new UploadAvatarUseCase_Factory(cloudinaryProvider, repositoryProvider);
  }

  public static UploadAvatarUseCase newInstance(CloudinaryService cloudinary,
      AuthRepository repository) {
    return new UploadAvatarUseCase(cloudinary, repository);
  }
}
