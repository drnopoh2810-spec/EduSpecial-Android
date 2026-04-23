package com.eduspecial.presentation.media;

import com.eduspecial.data.remote.api.CloudinaryService;
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
public final class MediaUploadViewModel_Factory implements Factory<MediaUploadViewModel> {
  private final Provider<CloudinaryService> cloudinaryServiceProvider;

  public MediaUploadViewModel_Factory(Provider<CloudinaryService> cloudinaryServiceProvider) {
    this.cloudinaryServiceProvider = cloudinaryServiceProvider;
  }

  @Override
  public MediaUploadViewModel get() {
    return newInstance(cloudinaryServiceProvider.get());
  }

  public static MediaUploadViewModel_Factory create(
      Provider<CloudinaryService> cloudinaryServiceProvider) {
    return new MediaUploadViewModel_Factory(cloudinaryServiceProvider);
  }

  public static MediaUploadViewModel newInstance(CloudinaryService cloudinaryService) {
    return new MediaUploadViewModel(cloudinaryService);
  }
}
