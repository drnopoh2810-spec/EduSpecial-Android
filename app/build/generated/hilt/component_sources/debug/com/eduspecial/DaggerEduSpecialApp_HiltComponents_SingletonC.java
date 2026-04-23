package com.eduspecial;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.eduspecial.data.local.EduSpecialDatabase;
import com.eduspecial.data.local.dao.AnalyticsDao;
import com.eduspecial.data.local.dao.BookmarkDao;
import com.eduspecial.data.local.dao.FlashcardDao;
import com.eduspecial.data.local.dao.PendingSubmissionDao;
import com.eduspecial.data.local.dao.QADao;
import com.eduspecial.data.manager.RoleManager;
import com.eduspecial.data.remote.api.CloudinaryService;
import com.eduspecial.data.remote.config.RemoteConfigManager;
import com.eduspecial.data.remote.messaging.FCMService;
import com.eduspecial.data.remote.messaging.FCMService_MembersInjector;
import com.eduspecial.data.remote.moderation.ContentModerationService;
import com.eduspecial.data.remote.search.AlgoliaSearchService;
import com.eduspecial.data.remote.secure.RemoteConfigCache;
import com.eduspecial.data.remote.secure.RemoteConfigClient;
import com.eduspecial.data.remote.secure.RuntimeConfigProvider;
import com.eduspecial.data.repository.AnalyticsRepository;
import com.eduspecial.data.repository.AuthRepository;
import com.eduspecial.data.repository.BookmarkRepository;
import com.eduspecial.data.repository.ConfigRepository;
import com.eduspecial.data.repository.FlashcardPagingRepository;
import com.eduspecial.data.repository.FlashcardRepository;
import com.eduspecial.data.repository.LeaderboardRepository;
import com.eduspecial.data.repository.ModerationRepository;
import com.eduspecial.data.repository.NotificationRepository;
import com.eduspecial.data.repository.QARepository;
import com.eduspecial.di.AppModule_ProvideAlgoliaSearchServiceFactory;
import com.eduspecial.di.AppModule_ProvideAnalyticsDaoFactory;
import com.eduspecial.di.AppModule_ProvideAnalyticsRepositoryFactory;
import com.eduspecial.di.AppModule_ProvideApiHealthMonitorFactory;
import com.eduspecial.di.AppModule_ProvideBookmarkDaoFactory;
import com.eduspecial.di.AppModule_ProvideBookmarkRepositoryFactory;
import com.eduspecial.di.AppModule_ProvideCircuitBreakerFactory;
import com.eduspecial.di.AppModule_ProvideCloudinaryServiceFactory;
import com.eduspecial.di.AppModule_ProvideContentModerationServiceFactory;
import com.eduspecial.di.AppModule_ProvideDatabaseFactory;
import com.eduspecial.di.AppModule_ProvideFirebaseAuthFactory;
import com.eduspecial.di.AppModule_ProvideFirebaseMessagingFactory;
import com.eduspecial.di.AppModule_ProvideFirestoreFactory;
import com.eduspecial.di.AppModule_ProvideFlashcardDaoFactory;
import com.eduspecial.di.AppModule_ProvideFlashcardPagingRepositoryFactory;
import com.eduspecial.di.AppModule_ProvideLeaderboardRepositoryFactory;
import com.eduspecial.di.AppModule_ProvideModerationRepositoryFactory;
import com.eduspecial.di.AppModule_ProvideNetworkMonitorFactory;
import com.eduspecial.di.AppModule_ProvideNotificationRepositoryFactory;
import com.eduspecial.di.AppModule_ProvideNotificationSchedulerFactory;
import com.eduspecial.di.AppModule_ProvidePendingSubmissionDaoFactory;
import com.eduspecial.di.AppModule_ProvideQADaoFactory;
import com.eduspecial.di.AppModule_ProvideRoleManagerFactory;
import com.eduspecial.di.AppModule_ProvideTtsManagerFactory;
import com.eduspecial.di.AppModule_ProvideUserPreferencesDataStoreFactory;
import com.eduspecial.di.UpdateModule_ProvideGitHubOkHttpClientFactory;
import com.eduspecial.di.UpdateModule_ProvideGitHubRetrofitFactory;
import com.eduspecial.di.UpdateModule_ProvideGitHubUpdateServiceFactory;
import com.eduspecial.domain.usecase.AcceptAnswerUseCase;
import com.eduspecial.domain.usecase.EditAnswerUseCase;
import com.eduspecial.domain.usecase.EditFlashcardUseCase;
import com.eduspecial.domain.usecase.EditQuestionUseCase;
import com.eduspecial.domain.usecase.GetBookmarksUseCase;
import com.eduspecial.domain.usecase.GetCategoryMasteryUseCase;
import com.eduspecial.domain.usecase.GetStudyStreakUseCase;
import com.eduspecial.domain.usecase.GetWeeklyProgressUseCase;
import com.eduspecial.domain.usecase.RecordReviewUseCase;
import com.eduspecial.domain.usecase.ScheduleStudyReminderUseCase;
import com.eduspecial.domain.usecase.ToggleBookmarkUseCase;
import com.eduspecial.domain.usecase.UpdateDisplayNameUseCase;
import com.eduspecial.domain.usecase.UploadAvatarUseCase;
import com.eduspecial.domain.usecase.UpvoteAnswerUseCase;
import com.eduspecial.presentation.auth.AuthViewModel;
import com.eduspecial.presentation.auth.AuthViewModel_HiltModules;
import com.eduspecial.presentation.bookmarks.BookmarksViewModel;
import com.eduspecial.presentation.bookmarks.BookmarksViewModel_HiltModules;
import com.eduspecial.presentation.flashcards.FlashcardsViewModel;
import com.eduspecial.presentation.flashcards.FlashcardsViewModel_HiltModules;
import com.eduspecial.presentation.flashcards.StudyViewModel;
import com.eduspecial.presentation.flashcards.StudyViewModel_HiltModules;
import com.eduspecial.presentation.home.HomeViewModel;
import com.eduspecial.presentation.home.HomeViewModel_HiltModules;
import com.eduspecial.presentation.leaderboard.LeaderboardViewModel;
import com.eduspecial.presentation.leaderboard.LeaderboardViewModel_HiltModules;
import com.eduspecial.presentation.media.MediaUploadViewModel;
import com.eduspecial.presentation.media.MediaUploadViewModel_HiltModules;
import com.eduspecial.presentation.navigation.ApiStatusViewModel;
import com.eduspecial.presentation.navigation.ApiStatusViewModel_HiltModules;
import com.eduspecial.presentation.onboarding.OnboardingViewModel;
import com.eduspecial.presentation.onboarding.OnboardingViewModel_HiltModules;
import com.eduspecial.presentation.permissions.PermissionViewModel;
import com.eduspecial.presentation.permissions.PermissionViewModel_HiltModules;
import com.eduspecial.presentation.profile.ProfileViewModel;
import com.eduspecial.presentation.profile.ProfileViewModel_HiltModules;
import com.eduspecial.presentation.qa.QAViewModel;
import com.eduspecial.presentation.qa.QAViewModel_HiltModules;
import com.eduspecial.presentation.search.SearchViewModel;
import com.eduspecial.presentation.search.SearchViewModel_HiltModules;
import com.eduspecial.ui.profile.ProfileSettingsViewModel;
import com.eduspecial.ui.profile.ProfileSettingsViewModel_HiltModules;
import com.eduspecial.ui.profile.SecurityViewModel;
import com.eduspecial.ui.profile.SecurityViewModel_HiltModules;
import com.eduspecial.update.GitHubUpdateService;
import com.eduspecial.update.UpdateRepository;
import com.eduspecial.update.UpdateViewModel;
import com.eduspecial.update.UpdateViewModel_HiltModules;
import com.eduspecial.utils.ApiHealthMonitor;
import com.eduspecial.utils.CircuitBreaker;
import com.eduspecial.utils.NetworkMonitor;
import com.eduspecial.utils.NotificationScheduler;
import com.eduspecial.utils.StudyReminderWorker;
import com.eduspecial.utils.StudyReminderWorker_AssistedFactory;
import com.eduspecial.utils.SyncWorker;
import com.eduspecial.utils.SyncWorker_AssistedFactory;
import com.eduspecial.utils.TtsManager;
import com.eduspecial.utils.UserPreferencesDataStore;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class DaggerEduSpecialApp_HiltComponents_SingletonC {
  private DaggerEduSpecialApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public EduSpecialApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements EduSpecialApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public EduSpecialApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements EduSpecialApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public EduSpecialApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements EduSpecialApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public EduSpecialApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements EduSpecialApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public EduSpecialApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements EduSpecialApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public EduSpecialApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements EduSpecialApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public EduSpecialApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements EduSpecialApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public EduSpecialApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends EduSpecialApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends EduSpecialApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends EduSpecialApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends EduSpecialApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(16).put(LazyClassKeyProvider.com_eduspecial_presentation_navigation_ApiStatusViewModel, ApiStatusViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_auth_AuthViewModel, AuthViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_bookmarks_BookmarksViewModel, BookmarksViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_flashcards_FlashcardsViewModel, FlashcardsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_leaderboard_LeaderboardViewModel, LeaderboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_media_MediaUploadViewModel, MediaUploadViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_permissions_PermissionViewModel, PermissionViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_ui_profile_ProfileSettingsViewModel, ProfileSettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_profile_ProfileViewModel, ProfileViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_qa_QAViewModel, QAViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_search_SearchViewModel, SearchViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_ui_profile_SecurityViewModel, SecurityViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_presentation_flashcards_StudyViewModel, StudyViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_eduspecial_update_UpdateViewModel, UpdateViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectPrefs(instance, singletonCImpl.provideUserPreferencesDataStoreProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_eduspecial_presentation_permissions_PermissionViewModel = "com.eduspecial.presentation.permissions.PermissionViewModel";

      static String com_eduspecial_presentation_navigation_ApiStatusViewModel = "com.eduspecial.presentation.navigation.ApiStatusViewModel";

      static String com_eduspecial_presentation_qa_QAViewModel = "com.eduspecial.presentation.qa.QAViewModel";

      static String com_eduspecial_presentation_flashcards_StudyViewModel = "com.eduspecial.presentation.flashcards.StudyViewModel";

      static String com_eduspecial_ui_profile_SecurityViewModel = "com.eduspecial.ui.profile.SecurityViewModel";

      static String com_eduspecial_presentation_media_MediaUploadViewModel = "com.eduspecial.presentation.media.MediaUploadViewModel";

      static String com_eduspecial_presentation_auth_AuthViewModel = "com.eduspecial.presentation.auth.AuthViewModel";

      static String com_eduspecial_presentation_flashcards_FlashcardsViewModel = "com.eduspecial.presentation.flashcards.FlashcardsViewModel";

      static String com_eduspecial_presentation_bookmarks_BookmarksViewModel = "com.eduspecial.presentation.bookmarks.BookmarksViewModel";

      static String com_eduspecial_ui_profile_ProfileSettingsViewModel = "com.eduspecial.ui.profile.ProfileSettingsViewModel";

      static String com_eduspecial_presentation_profile_ProfileViewModel = "com.eduspecial.presentation.profile.ProfileViewModel";

      static String com_eduspecial_update_UpdateViewModel = "com.eduspecial.update.UpdateViewModel";

      static String com_eduspecial_presentation_search_SearchViewModel = "com.eduspecial.presentation.search.SearchViewModel";

      static String com_eduspecial_presentation_home_HomeViewModel = "com.eduspecial.presentation.home.HomeViewModel";

      static String com_eduspecial_presentation_onboarding_OnboardingViewModel = "com.eduspecial.presentation.onboarding.OnboardingViewModel";

      static String com_eduspecial_presentation_leaderboard_LeaderboardViewModel = "com.eduspecial.presentation.leaderboard.LeaderboardViewModel";

      @KeepFieldType
      PermissionViewModel com_eduspecial_presentation_permissions_PermissionViewModel2;

      @KeepFieldType
      ApiStatusViewModel com_eduspecial_presentation_navigation_ApiStatusViewModel2;

      @KeepFieldType
      QAViewModel com_eduspecial_presentation_qa_QAViewModel2;

      @KeepFieldType
      StudyViewModel com_eduspecial_presentation_flashcards_StudyViewModel2;

      @KeepFieldType
      SecurityViewModel com_eduspecial_ui_profile_SecurityViewModel2;

      @KeepFieldType
      MediaUploadViewModel com_eduspecial_presentation_media_MediaUploadViewModel2;

      @KeepFieldType
      AuthViewModel com_eduspecial_presentation_auth_AuthViewModel2;

      @KeepFieldType
      FlashcardsViewModel com_eduspecial_presentation_flashcards_FlashcardsViewModel2;

      @KeepFieldType
      BookmarksViewModel com_eduspecial_presentation_bookmarks_BookmarksViewModel2;

      @KeepFieldType
      ProfileSettingsViewModel com_eduspecial_ui_profile_ProfileSettingsViewModel2;

      @KeepFieldType
      ProfileViewModel com_eduspecial_presentation_profile_ProfileViewModel2;

      @KeepFieldType
      UpdateViewModel com_eduspecial_update_UpdateViewModel2;

      @KeepFieldType
      SearchViewModel com_eduspecial_presentation_search_SearchViewModel2;

      @KeepFieldType
      HomeViewModel com_eduspecial_presentation_home_HomeViewModel2;

      @KeepFieldType
      OnboardingViewModel com_eduspecial_presentation_onboarding_OnboardingViewModel2;

      @KeepFieldType
      LeaderboardViewModel com_eduspecial_presentation_leaderboard_LeaderboardViewModel2;
    }
  }

  private static final class ViewModelCImpl extends EduSpecialApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<ApiStatusViewModel> apiStatusViewModelProvider;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<BookmarksViewModel> bookmarksViewModelProvider;

    private Provider<FlashcardsViewModel> flashcardsViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LeaderboardViewModel> leaderboardViewModelProvider;

    private Provider<MediaUploadViewModel> mediaUploadViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<PermissionViewModel> permissionViewModelProvider;

    private Provider<ProfileSettingsViewModel> profileSettingsViewModelProvider;

    private Provider<ProfileViewModel> profileViewModelProvider;

    private Provider<QAViewModel> qAViewModelProvider;

    private Provider<SearchViewModel> searchViewModelProvider;

    private Provider<SecurityViewModel> securityViewModelProvider;

    private Provider<StudyViewModel> studyViewModelProvider;

    private Provider<UpdateViewModel> updateViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private GetBookmarksUseCase getBookmarksUseCase() {
      return new GetBookmarksUseCase(singletonCImpl.provideBookmarkRepositoryProvider.get());
    }

    private EditFlashcardUseCase editFlashcardUseCase() {
      return new EditFlashcardUseCase(singletonCImpl.flashcardRepositoryProvider.get());
    }

    private ToggleBookmarkUseCase toggleBookmarkUseCase() {
      return new ToggleBookmarkUseCase(singletonCImpl.provideBookmarkRepositoryProvider.get());
    }

    private GetStudyStreakUseCase getStudyStreakUseCase() {
      return new GetStudyStreakUseCase(singletonCImpl.provideAnalyticsRepositoryProvider.get());
    }

    private GetWeeklyProgressUseCase getWeeklyProgressUseCase() {
      return new GetWeeklyProgressUseCase(singletonCImpl.provideAnalyticsRepositoryProvider.get());
    }

    private GetCategoryMasteryUseCase getCategoryMasteryUseCase() {
      return new GetCategoryMasteryUseCase(singletonCImpl.flashcardRepositoryProvider.get());
    }

    private UpdateDisplayNameUseCase updateDisplayNameUseCase() {
      return new UpdateDisplayNameUseCase(singletonCImpl.authRepositoryProvider.get());
    }

    private UploadAvatarUseCase uploadAvatarUseCase() {
      return new UploadAvatarUseCase(singletonCImpl.provideCloudinaryServiceProvider.get(), singletonCImpl.authRepositoryProvider.get());
    }

    private ScheduleStudyReminderUseCase scheduleStudyReminderUseCase() {
      return new ScheduleStudyReminderUseCase(singletonCImpl.provideNotificationSchedulerProvider.get());
    }

    private EditQuestionUseCase editQuestionUseCase() {
      return new EditQuestionUseCase(singletonCImpl.qARepositoryProvider.get());
    }

    private EditAnswerUseCase editAnswerUseCase() {
      return new EditAnswerUseCase(singletonCImpl.qARepositoryProvider.get());
    }

    private AcceptAnswerUseCase acceptAnswerUseCase() {
      return new AcceptAnswerUseCase(singletonCImpl.qARepositoryProvider.get());
    }

    private UpvoteAnswerUseCase upvoteAnswerUseCase() {
      return new UpvoteAnswerUseCase(singletonCImpl.qARepositoryProvider.get());
    }

    private RecordReviewUseCase recordReviewUseCase() {
      return new RecordReviewUseCase(singletonCImpl.provideAnalyticsRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.apiStatusViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.bookmarksViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.flashcardsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.leaderboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.mediaUploadViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.permissionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.profileSettingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.profileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.qAViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
      this.searchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 12);
      this.securityViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 13);
      this.studyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 14);
      this.updateViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 15);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(16).put(LazyClassKeyProvider.com_eduspecial_presentation_navigation_ApiStatusViewModel, ((Provider) apiStatusViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_auth_AuthViewModel, ((Provider) authViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_bookmarks_BookmarksViewModel, ((Provider) bookmarksViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_flashcards_FlashcardsViewModel, ((Provider) flashcardsViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_leaderboard_LeaderboardViewModel, ((Provider) leaderboardViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_media_MediaUploadViewModel, ((Provider) mediaUploadViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_permissions_PermissionViewModel, ((Provider) permissionViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_ui_profile_ProfileSettingsViewModel, ((Provider) profileSettingsViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_profile_ProfileViewModel, ((Provider) profileViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_qa_QAViewModel, ((Provider) qAViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_search_SearchViewModel, ((Provider) searchViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_ui_profile_SecurityViewModel, ((Provider) securityViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_presentation_flashcards_StudyViewModel, ((Provider) studyViewModelProvider)).put(LazyClassKeyProvider.com_eduspecial_update_UpdateViewModel, ((Provider) updateViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_eduspecial_presentation_home_HomeViewModel = "com.eduspecial.presentation.home.HomeViewModel";

      static String com_eduspecial_presentation_qa_QAViewModel = "com.eduspecial.presentation.qa.QAViewModel";

      static String com_eduspecial_presentation_flashcards_StudyViewModel = "com.eduspecial.presentation.flashcards.StudyViewModel";

      static String com_eduspecial_presentation_search_SearchViewModel = "com.eduspecial.presentation.search.SearchViewModel";

      static String com_eduspecial_presentation_navigation_ApiStatusViewModel = "com.eduspecial.presentation.navigation.ApiStatusViewModel";

      static String com_eduspecial_presentation_bookmarks_BookmarksViewModel = "com.eduspecial.presentation.bookmarks.BookmarksViewModel";

      static String com_eduspecial_update_UpdateViewModel = "com.eduspecial.update.UpdateViewModel";

      static String com_eduspecial_presentation_media_MediaUploadViewModel = "com.eduspecial.presentation.media.MediaUploadViewModel";

      static String com_eduspecial_presentation_auth_AuthViewModel = "com.eduspecial.presentation.auth.AuthViewModel";

      static String com_eduspecial_presentation_permissions_PermissionViewModel = "com.eduspecial.presentation.permissions.PermissionViewModel";

      static String com_eduspecial_presentation_profile_ProfileViewModel = "com.eduspecial.presentation.profile.ProfileViewModel";

      static String com_eduspecial_presentation_onboarding_OnboardingViewModel = "com.eduspecial.presentation.onboarding.OnboardingViewModel";

      static String com_eduspecial_ui_profile_ProfileSettingsViewModel = "com.eduspecial.ui.profile.ProfileSettingsViewModel";

      static String com_eduspecial_presentation_flashcards_FlashcardsViewModel = "com.eduspecial.presentation.flashcards.FlashcardsViewModel";

      static String com_eduspecial_presentation_leaderboard_LeaderboardViewModel = "com.eduspecial.presentation.leaderboard.LeaderboardViewModel";

      static String com_eduspecial_ui_profile_SecurityViewModel = "com.eduspecial.ui.profile.SecurityViewModel";

      @KeepFieldType
      HomeViewModel com_eduspecial_presentation_home_HomeViewModel2;

      @KeepFieldType
      QAViewModel com_eduspecial_presentation_qa_QAViewModel2;

      @KeepFieldType
      StudyViewModel com_eduspecial_presentation_flashcards_StudyViewModel2;

      @KeepFieldType
      SearchViewModel com_eduspecial_presentation_search_SearchViewModel2;

      @KeepFieldType
      ApiStatusViewModel com_eduspecial_presentation_navigation_ApiStatusViewModel2;

      @KeepFieldType
      BookmarksViewModel com_eduspecial_presentation_bookmarks_BookmarksViewModel2;

      @KeepFieldType
      UpdateViewModel com_eduspecial_update_UpdateViewModel2;

      @KeepFieldType
      MediaUploadViewModel com_eduspecial_presentation_media_MediaUploadViewModel2;

      @KeepFieldType
      AuthViewModel com_eduspecial_presentation_auth_AuthViewModel2;

      @KeepFieldType
      PermissionViewModel com_eduspecial_presentation_permissions_PermissionViewModel2;

      @KeepFieldType
      ProfileViewModel com_eduspecial_presentation_profile_ProfileViewModel2;

      @KeepFieldType
      OnboardingViewModel com_eduspecial_presentation_onboarding_OnboardingViewModel2;

      @KeepFieldType
      ProfileSettingsViewModel com_eduspecial_ui_profile_ProfileSettingsViewModel2;

      @KeepFieldType
      FlashcardsViewModel com_eduspecial_presentation_flashcards_FlashcardsViewModel2;

      @KeepFieldType
      LeaderboardViewModel com_eduspecial_presentation_leaderboard_LeaderboardViewModel2;

      @KeepFieldType
      SecurityViewModel com_eduspecial_ui_profile_SecurityViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.eduspecial.presentation.navigation.ApiStatusViewModel 
          return (T) new ApiStatusViewModel(singletonCImpl.provideApiHealthMonitorProvider.get());

          case 1: // com.eduspecial.presentation.auth.AuthViewModel 
          return (T) new AuthViewModel(singletonCImpl.authRepositoryProvider.get(), singletonCImpl.runtimeConfigProvider.get());

          case 2: // com.eduspecial.presentation.bookmarks.BookmarksViewModel 
          return (T) new BookmarksViewModel(viewModelCImpl.getBookmarksUseCase());

          case 3: // com.eduspecial.presentation.flashcards.FlashcardsViewModel 
          return (T) new FlashcardsViewModel(singletonCImpl.flashcardRepositoryProvider.get(), singletonCImpl.authRepositoryProvider.get(), singletonCImpl.provideBookmarkRepositoryProvider.get(), viewModelCImpl.editFlashcardUseCase(), viewModelCImpl.toggleBookmarkUseCase(), singletonCImpl.provideFlashcardPagingRepositoryProvider.get(), singletonCImpl.provideTtsManagerProvider.get());

          case 4: // com.eduspecial.presentation.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.flashcardRepositoryProvider.get(), singletonCImpl.provideAnalyticsRepositoryProvider.get(), viewModelCImpl.getStudyStreakUseCase(), viewModelCImpl.getWeeklyProgressUseCase(), viewModelCImpl.getCategoryMasteryUseCase(), singletonCImpl.provideUserPreferencesDataStoreProvider.get());

          case 5: // com.eduspecial.presentation.leaderboard.LeaderboardViewModel 
          return (T) new LeaderboardViewModel(singletonCImpl.provideLeaderboardRepositoryProvider.get());

          case 6: // com.eduspecial.presentation.media.MediaUploadViewModel 
          return (T) new MediaUploadViewModel(singletonCImpl.provideCloudinaryServiceProvider.get());

          case 7: // com.eduspecial.presentation.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.provideUserPreferencesDataStoreProvider.get());

          case 8: // com.eduspecial.presentation.permissions.PermissionViewModel 
          return (T) new PermissionViewModel(singletonCImpl.provideUserPreferencesDataStoreProvider.get());

          case 9: // com.eduspecial.ui.profile.ProfileSettingsViewModel 
          return (T) new ProfileSettingsViewModel(singletonCImpl.authRepositoryProvider.get(), singletonCImpl.provideRoleManagerProvider.get());

          case 10: // com.eduspecial.presentation.profile.ProfileViewModel 
          return (T) new ProfileViewModel(singletonCImpl.authRepositoryProvider.get(), singletonCImpl.flashcardRepositoryProvider.get(), singletonCImpl.provideUserPreferencesDataStoreProvider.get(), viewModelCImpl.updateDisplayNameUseCase(), viewModelCImpl.uploadAvatarUseCase(), viewModelCImpl.scheduleStudyReminderUseCase());

          case 11: // com.eduspecial.presentation.qa.QAViewModel 
          return (T) new QAViewModel(singletonCImpl.qARepositoryProvider.get(), singletonCImpl.authRepositoryProvider.get(), singletonCImpl.provideBookmarkRepositoryProvider.get(), viewModelCImpl.editQuestionUseCase(), viewModelCImpl.editAnswerUseCase(), viewModelCImpl.acceptAnswerUseCase(), viewModelCImpl.upvoteAnswerUseCase(), viewModelCImpl.toggleBookmarkUseCase());

          case 12: // com.eduspecial.presentation.search.SearchViewModel 
          return (T) new SearchViewModel(singletonCImpl.flashcardRepositoryProvider.get(), singletonCImpl.qARepositoryProvider.get(), singletonCImpl.provideAlgoliaSearchServiceProvider.get());

          case 13: // com.eduspecial.ui.profile.SecurityViewModel 
          return (T) new SecurityViewModel(singletonCImpl.authRepositoryProvider.get(), singletonCImpl.provideRoleManagerProvider.get(), singletonCImpl.provideFirestoreProvider.get());

          case 14: // com.eduspecial.presentation.flashcards.StudyViewModel 
          return (T) new StudyViewModel(singletonCImpl.flashcardRepositoryProvider.get(), viewModelCImpl.recordReviewUseCase(), singletonCImpl.provideTtsManagerProvider.get());

          case 15: // com.eduspecial.update.UpdateViewModel 
          return (T) new UpdateViewModel(singletonCImpl.updateRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends EduSpecialApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends EduSpecialApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectFCMService(FCMService fCMService) {
      injectFCMService2(fCMService);
    }

    @CanIgnoreReturnValue
    private FCMService injectFCMService2(FCMService instance) {
      FCMService_MembersInjector.injectNotificationRepository(instance, singletonCImpl.provideNotificationRepositoryProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends EduSpecialApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<EduSpecialDatabase> provideDatabaseProvider;

    private Provider<FirebaseFirestore> provideFirestoreProvider;

    private Provider<FirebaseAuth> provideFirebaseAuthProvider;

    private Provider<LeaderboardRepository> provideLeaderboardRepositoryProvider;

    private Provider<RemoteConfigClient> remoteConfigClientProvider;

    private Provider<RemoteConfigCache> remoteConfigCacheProvider;

    private Provider<RuntimeConfigProvider> runtimeConfigProvider;

    private Provider<RemoteConfigManager> remoteConfigManagerProvider;

    private Provider<ConfigRepository> configRepositoryProvider;

    private Provider<AlgoliaSearchService> provideAlgoliaSearchServiceProvider;

    private Provider<ContentModerationService> provideContentModerationServiceProvider;

    private Provider<ModerationRepository> provideModerationRepositoryProvider;

    private Provider<FlashcardRepository> flashcardRepositoryProvider;

    private Provider<AnalyticsRepository> provideAnalyticsRepositoryProvider;

    private Provider<UserPreferencesDataStore> provideUserPreferencesDataStoreProvider;

    private Provider<StudyReminderWorker_AssistedFactory> studyReminderWorker_AssistedFactoryProvider;

    private Provider<QARepository> qARepositoryProvider;

    private Provider<BookmarkRepository> provideBookmarkRepositoryProvider;

    private Provider<NetworkMonitor> provideNetworkMonitorProvider;

    private Provider<CircuitBreaker> provideCircuitBreakerProvider;

    private Provider<SyncWorker_AssistedFactory> syncWorker_AssistedFactoryProvider;

    private Provider<FirebaseMessaging> provideFirebaseMessagingProvider;

    private Provider<NotificationRepository> provideNotificationRepositoryProvider;

    private Provider<ApiHealthMonitor> provideApiHealthMonitorProvider;

    private Provider<RoleManager> provideRoleManagerProvider;

    private Provider<AuthRepository> authRepositoryProvider;

    private Provider<FlashcardPagingRepository> provideFlashcardPagingRepositoryProvider;

    private Provider<TtsManager> provideTtsManagerProvider;

    private Provider<CloudinaryService> provideCloudinaryServiceProvider;

    private Provider<NotificationScheduler> provideNotificationSchedulerProvider;

    private Provider<OkHttpClient> provideGitHubOkHttpClientProvider;

    private Provider<Retrofit> provideGitHubRetrofitProvider;

    private Provider<GitHubUpdateService> provideGitHubUpdateServiceProvider;

    private Provider<UpdateRepository> updateRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private FlashcardDao flashcardDao() {
      return AppModule_ProvideFlashcardDaoFactory.provideFlashcardDao(provideDatabaseProvider.get());
    }

    private PendingSubmissionDao pendingSubmissionDao() {
      return AppModule_ProvidePendingSubmissionDaoFactory.providePendingSubmissionDao(provideDatabaseProvider.get());
    }

    private AnalyticsDao analyticsDao() {
      return AppModule_ProvideAnalyticsDaoFactory.provideAnalyticsDao(provideDatabaseProvider.get());
    }

    private QADao qADao() {
      return AppModule_ProvideQADaoFactory.provideQADao(provideDatabaseProvider.get());
    }

    private BookmarkDao bookmarkDao() {
      return AppModule_ProvideBookmarkDaoFactory.provideBookmarkDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return ImmutableMap.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>of("com.eduspecial.utils.StudyReminderWorker", ((Provider) studyReminderWorker_AssistedFactoryProvider), "com.eduspecial.utils.SyncWorker", ((Provider) syncWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<EduSpecialDatabase>(singletonCImpl, 2));
      this.provideFirestoreProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseFirestore>(singletonCImpl, 3));
      this.provideFirebaseAuthProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAuth>(singletonCImpl, 5));
      this.provideLeaderboardRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<LeaderboardRepository>(singletonCImpl, 4));
      this.remoteConfigClientProvider = DoubleCheck.provider(new SwitchingProvider<RemoteConfigClient>(singletonCImpl, 9));
      this.remoteConfigCacheProvider = DoubleCheck.provider(new SwitchingProvider<RemoteConfigCache>(singletonCImpl, 10));
      this.runtimeConfigProvider = DoubleCheck.provider(new SwitchingProvider<RuntimeConfigProvider>(singletonCImpl, 8));
      this.remoteConfigManagerProvider = DoubleCheck.provider(new SwitchingProvider<RemoteConfigManager>(singletonCImpl, 11));
      this.configRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ConfigRepository>(singletonCImpl, 7));
      this.provideAlgoliaSearchServiceProvider = DoubleCheck.provider(new SwitchingProvider<AlgoliaSearchService>(singletonCImpl, 6));
      this.provideContentModerationServiceProvider = DoubleCheck.provider(new SwitchingProvider<ContentModerationService>(singletonCImpl, 13));
      this.provideModerationRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ModerationRepository>(singletonCImpl, 12));
      this.flashcardRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FlashcardRepository>(singletonCImpl, 1));
      this.provideAnalyticsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AnalyticsRepository>(singletonCImpl, 14));
      this.provideUserPreferencesDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<UserPreferencesDataStore>(singletonCImpl, 15));
      this.studyReminderWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<StudyReminderWorker_AssistedFactory>(singletonCImpl, 0));
      this.qARepositoryProvider = DoubleCheck.provider(new SwitchingProvider<QARepository>(singletonCImpl, 17));
      this.provideBookmarkRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<BookmarkRepository>(singletonCImpl, 18));
      this.provideNetworkMonitorProvider = DoubleCheck.provider(new SwitchingProvider<NetworkMonitor>(singletonCImpl, 19));
      this.provideCircuitBreakerProvider = DoubleCheck.provider(new SwitchingProvider<CircuitBreaker>(singletonCImpl, 20));
      this.syncWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<SyncWorker_AssistedFactory>(singletonCImpl, 16));
      this.provideFirebaseMessagingProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseMessaging>(singletonCImpl, 22));
      this.provideNotificationRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<NotificationRepository>(singletonCImpl, 21));
      this.provideApiHealthMonitorProvider = DoubleCheck.provider(new SwitchingProvider<ApiHealthMonitor>(singletonCImpl, 23));
      this.provideRoleManagerProvider = DoubleCheck.provider(new SwitchingProvider<RoleManager>(singletonCImpl, 25));
      this.authRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepository>(singletonCImpl, 24));
      this.provideFlashcardPagingRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FlashcardPagingRepository>(singletonCImpl, 26));
      this.provideTtsManagerProvider = DoubleCheck.provider(new SwitchingProvider<TtsManager>(singletonCImpl, 27));
      this.provideCloudinaryServiceProvider = DoubleCheck.provider(new SwitchingProvider<CloudinaryService>(singletonCImpl, 28));
      this.provideNotificationSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<NotificationScheduler>(singletonCImpl, 29));
      this.provideGitHubOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 33));
      this.provideGitHubRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 32));
      this.provideGitHubUpdateServiceProvider = DoubleCheck.provider(new SwitchingProvider<GitHubUpdateService>(singletonCImpl, 31));
      this.updateRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<UpdateRepository>(singletonCImpl, 30));
    }

    @Override
    public void injectEduSpecialApp(EduSpecialApp eduSpecialApp) {
      injectEduSpecialApp2(eduSpecialApp);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private EduSpecialApp injectEduSpecialApp2(EduSpecialApp instance) {
      EduSpecialApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      EduSpecialApp_MembersInjector.injectRuntimeConfigProvider(instance, runtimeConfigProvider.get());
      EduSpecialApp_MembersInjector.injectConfigRepository(instance, configRepositoryProvider.get());
      EduSpecialApp_MembersInjector.injectAlgoliaSearchService(instance, provideAlgoliaSearchServiceProvider.get());
      EduSpecialApp_MembersInjector.injectNotificationRepository(instance, provideNotificationRepositoryProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.eduspecial.utils.StudyReminderWorker_AssistedFactory 
          return (T) new StudyReminderWorker_AssistedFactory() {
            @Override
            public StudyReminderWorker create(Context context, WorkerParameters params) {
              return new StudyReminderWorker(context, params, singletonCImpl.flashcardRepositoryProvider.get(), singletonCImpl.provideAnalyticsRepositoryProvider.get(), singletonCImpl.provideUserPreferencesDataStoreProvider.get());
            }
          };

          case 1: // com.eduspecial.data.repository.FlashcardRepository 
          return (T) new FlashcardRepository(singletonCImpl.flashcardDao(), singletonCImpl.pendingSubmissionDao(), singletonCImpl.provideFirestoreProvider.get(), singletonCImpl.provideLeaderboardRepositoryProvider.get(), singletonCImpl.provideAlgoliaSearchServiceProvider.get(), singletonCImpl.provideModerationRepositoryProvider.get());

          case 2: // com.eduspecial.data.local.EduSpecialDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.google.firebase.firestore.FirebaseFirestore 
          return (T) AppModule_ProvideFirestoreFactory.provideFirestore();

          case 4: // com.eduspecial.data.repository.LeaderboardRepository 
          return (T) AppModule_ProvideLeaderboardRepositoryFactory.provideLeaderboardRepository(singletonCImpl.provideFirestoreProvider.get(), singletonCImpl.provideFirebaseAuthProvider.get());

          case 5: // com.google.firebase.auth.FirebaseAuth 
          return (T) AppModule_ProvideFirebaseAuthFactory.provideFirebaseAuth();

          case 6: // com.eduspecial.data.remote.search.AlgoliaSearchService 
          return (T) AppModule_ProvideAlgoliaSearchServiceFactory.provideAlgoliaSearchService(singletonCImpl.configRepositoryProvider.get());

          case 7: // com.eduspecial.data.repository.ConfigRepository 
          return (T) new ConfigRepository(singletonCImpl.runtimeConfigProvider.get(), singletonCImpl.remoteConfigManagerProvider.get());

          case 8: // com.eduspecial.data.remote.secure.RuntimeConfigProvider 
          return (T) new RuntimeConfigProvider(singletonCImpl.remoteConfigClientProvider.get(), singletonCImpl.remoteConfigCacheProvider.get());

          case 9: // com.eduspecial.data.remote.secure.RemoteConfigClient 
          return (T) new RemoteConfigClient();

          case 10: // com.eduspecial.data.remote.secure.RemoteConfigCache 
          return (T) new RemoteConfigCache(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 11: // com.eduspecial.data.remote.config.RemoteConfigManager 
          return (T) new RemoteConfigManager(singletonCImpl.runtimeConfigProvider.get());

          case 12: // com.eduspecial.data.repository.ModerationRepository 
          return (T) AppModule_ProvideModerationRepositoryFactory.provideModerationRepository(singletonCImpl.provideFirestoreProvider.get(), singletonCImpl.provideFirebaseAuthProvider.get(), singletonCImpl.provideContentModerationServiceProvider.get());

          case 13: // com.eduspecial.data.remote.moderation.ContentModerationService 
          return (T) AppModule_ProvideContentModerationServiceFactory.provideContentModerationService(singletonCImpl.provideFirestoreProvider.get());

          case 14: // com.eduspecial.data.repository.AnalyticsRepository 
          return (T) AppModule_ProvideAnalyticsRepositoryFactory.provideAnalyticsRepository(singletonCImpl.analyticsDao());

          case 15: // com.eduspecial.utils.UserPreferencesDataStore 
          return (T) AppModule_ProvideUserPreferencesDataStoreFactory.provideUserPreferencesDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 16: // com.eduspecial.utils.SyncWorker_AssistedFactory 
          return (T) new SyncWorker_AssistedFactory() {
            @Override
            public SyncWorker create(Context context2, WorkerParameters workerParams) {
              return new SyncWorker(context2, workerParams, singletonCImpl.flashcardRepositoryProvider.get(), singletonCImpl.qARepositoryProvider.get(), singletonCImpl.provideBookmarkRepositoryProvider.get(), singletonCImpl.pendingSubmissionDao(), singletonCImpl.provideUserPreferencesDataStoreProvider.get(), singletonCImpl.provideNetworkMonitorProvider.get(), singletonCImpl.provideCircuitBreakerProvider.get());
            }
          };

          case 17: // com.eduspecial.data.repository.QARepository 
          return (T) new QARepository(singletonCImpl.qADao(), singletonCImpl.provideFirestoreProvider.get(), singletonCImpl.provideLeaderboardRepositoryProvider.get(), singletonCImpl.provideAlgoliaSearchServiceProvider.get(), singletonCImpl.provideModerationRepositoryProvider.get());

          case 18: // com.eduspecial.data.repository.BookmarkRepository 
          return (T) AppModule_ProvideBookmarkRepositoryFactory.provideBookmarkRepository(singletonCImpl.bookmarkDao());

          case 19: // com.eduspecial.utils.NetworkMonitor 
          return (T) AppModule_ProvideNetworkMonitorFactory.provideNetworkMonitor(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 20: // com.eduspecial.utils.CircuitBreaker 
          return (T) AppModule_ProvideCircuitBreakerFactory.provideCircuitBreaker();

          case 21: // com.eduspecial.data.repository.NotificationRepository 
          return (T) AppModule_ProvideNotificationRepositoryFactory.provideNotificationRepository(singletonCImpl.provideFirestoreProvider.get(), singletonCImpl.provideFirebaseAuthProvider.get(), singletonCImpl.provideFirebaseMessagingProvider.get());

          case 22: // com.google.firebase.messaging.FirebaseMessaging 
          return (T) AppModule_ProvideFirebaseMessagingFactory.provideFirebaseMessaging();

          case 23: // com.eduspecial.utils.ApiHealthMonitor 
          return (T) AppModule_ProvideApiHealthMonitorFactory.provideApiHealthMonitor(singletonCImpl.provideCircuitBreakerProvider.get(), singletonCImpl.provideNetworkMonitorProvider.get());

          case 24: // com.eduspecial.data.repository.AuthRepository 
          return (T) new AuthRepository(singletonCImpl.provideFirebaseAuthProvider.get(), singletonCImpl.provideFirestoreProvider.get(), singletonCImpl.provideUserPreferencesDataStoreProvider.get(), singletonCImpl.provideRoleManagerProvider.get());

          case 25: // com.eduspecial.data.manager.RoleManager 
          return (T) AppModule_ProvideRoleManagerFactory.provideRoleManager(singletonCImpl.provideFirestoreProvider.get(), singletonCImpl.provideFirebaseAuthProvider.get());

          case 26: // com.eduspecial.data.repository.FlashcardPagingRepository 
          return (T) AppModule_ProvideFlashcardPagingRepositoryFactory.provideFlashcardPagingRepository(singletonCImpl.flashcardDao(), singletonCImpl.provideFirestoreProvider.get(), singletonCImpl.provideCircuitBreakerProvider.get());

          case 27: // com.eduspecial.utils.TtsManager 
          return (T) AppModule_ProvideTtsManagerFactory.provideTtsManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 28: // com.eduspecial.data.remote.api.CloudinaryService 
          return (T) AppModule_ProvideCloudinaryServiceFactory.provideCloudinaryService(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.configRepositoryProvider.get());

          case 29: // com.eduspecial.utils.NotificationScheduler 
          return (T) AppModule_ProvideNotificationSchedulerFactory.provideNotificationScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 30: // com.eduspecial.update.UpdateRepository 
          return (T) new UpdateRepository(singletonCImpl.provideGitHubUpdateServiceProvider.get());

          case 31: // com.eduspecial.update.GitHubUpdateService 
          return (T) UpdateModule_ProvideGitHubUpdateServiceFactory.provideGitHubUpdateService(singletonCImpl.provideGitHubRetrofitProvider.get());

          case 32: // @javax.inject.Named("github") retrofit2.Retrofit 
          return (T) UpdateModule_ProvideGitHubRetrofitFactory.provideGitHubRetrofit(singletonCImpl.provideGitHubOkHttpClientProvider.get());

          case 33: // @javax.inject.Named("github_okhttp") okhttp3.OkHttpClient 
          return (T) UpdateModule_ProvideGitHubOkHttpClientFactory.provideGitHubOkHttpClient();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
