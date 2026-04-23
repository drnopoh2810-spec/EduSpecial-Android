package com.eduspecial;

import androidx.work.WorkerFactory;
import com.eduspecial.data.remote.search.AlgoliaSearchService;
import com.eduspecial.data.remote.secure.RuntimeConfigProvider;
import com.eduspecial.data.repository.ConfigRepository;
import com.eduspecial.data.repository.NotificationRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class EduSpecialApp_MembersInjector implements MembersInjector<EduSpecialApp> {
  private final Provider<WorkerFactory> workerFactoryProvider;

  private final Provider<RuntimeConfigProvider> runtimeConfigProvider;

  private final Provider<ConfigRepository> configRepositoryProvider;

  private final Provider<AlgoliaSearchService> algoliaSearchServiceProvider;

  private final Provider<NotificationRepository> notificationRepositoryProvider;

  public EduSpecialApp_MembersInjector(Provider<WorkerFactory> workerFactoryProvider,
      Provider<RuntimeConfigProvider> runtimeConfigProvider,
      Provider<ConfigRepository> configRepositoryProvider,
      Provider<AlgoliaSearchService> algoliaSearchServiceProvider,
      Provider<NotificationRepository> notificationRepositoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.runtimeConfigProvider = runtimeConfigProvider;
    this.configRepositoryProvider = configRepositoryProvider;
    this.algoliaSearchServiceProvider = algoliaSearchServiceProvider;
    this.notificationRepositoryProvider = notificationRepositoryProvider;
  }

  public static MembersInjector<EduSpecialApp> create(Provider<WorkerFactory> workerFactoryProvider,
      Provider<RuntimeConfigProvider> runtimeConfigProvider,
      Provider<ConfigRepository> configRepositoryProvider,
      Provider<AlgoliaSearchService> algoliaSearchServiceProvider,
      Provider<NotificationRepository> notificationRepositoryProvider) {
    return new EduSpecialApp_MembersInjector(workerFactoryProvider, runtimeConfigProvider, configRepositoryProvider, algoliaSearchServiceProvider, notificationRepositoryProvider);
  }

  @Override
  public void injectMembers(EduSpecialApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectRuntimeConfigProvider(instance, runtimeConfigProvider.get());
    injectConfigRepository(instance, configRepositoryProvider.get());
    injectAlgoliaSearchService(instance, algoliaSearchServiceProvider.get());
    injectNotificationRepository(instance, notificationRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.eduspecial.EduSpecialApp.workerFactory")
  public static void injectWorkerFactory(EduSpecialApp instance, WorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.eduspecial.EduSpecialApp.runtimeConfigProvider")
  public static void injectRuntimeConfigProvider(EduSpecialApp instance,
      RuntimeConfigProvider runtimeConfigProvider) {
    instance.runtimeConfigProvider = runtimeConfigProvider;
  }

  @InjectedFieldSignature("com.eduspecial.EduSpecialApp.configRepository")
  public static void injectConfigRepository(EduSpecialApp instance,
      ConfigRepository configRepository) {
    instance.configRepository = configRepository;
  }

  @InjectedFieldSignature("com.eduspecial.EduSpecialApp.algoliaSearchService")
  public static void injectAlgoliaSearchService(EduSpecialApp instance,
      AlgoliaSearchService algoliaSearchService) {
    instance.algoliaSearchService = algoliaSearchService;
  }

  @InjectedFieldSignature("com.eduspecial.EduSpecialApp.notificationRepository")
  public static void injectNotificationRepository(EduSpecialApp instance,
      NotificationRepository notificationRepository) {
    instance.notificationRepository = notificationRepository;
  }
}
