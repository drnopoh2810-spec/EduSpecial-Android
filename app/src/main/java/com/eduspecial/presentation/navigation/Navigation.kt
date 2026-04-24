package com.eduspecial.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eduspecial.presentation.auth.AuthScreen
import com.eduspecial.presentation.bookmarks.BookmarksScreen
import com.eduspecial.presentation.flashcards.FlashcardsScreen
import com.eduspecial.presentation.flashcards.StudyScreen
import com.eduspecial.presentation.home.HomeScreen
import com.eduspecial.presentation.leaderboard.LeaderboardScreen
import com.eduspecial.presentation.onboarding.OnboardingScreen
import com.eduspecial.presentation.permissions.PermissionRequestScreen
import com.eduspecial.presentation.profile.ProfileScreen
import com.eduspecial.presentation.qa.QAScreen
import com.eduspecial.presentation.search.SearchScreen
import com.eduspecial.update.UpdateDialogHost
import com.eduspecial.update.UpdateViewModel
import com.eduspecial.utils.ApiHealthMonitor
import com.eduspecial.utils.UserPreferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first

// ─── Transition constants ──────────────────────────────────────────────────────
private const val TRANSITION_DURATION = 300

private val enterTransition = fadeIn(tween(TRANSITION_DURATION)) +
        slideInHorizontally(tween(TRANSITION_DURATION)) { -it / 6 }

private val exitTransition = fadeOut(tween(TRANSITION_DURATION)) +
        slideOutHorizontally(tween(TRANSITION_DURATION)) { it / 6 }

private val popEnterTransition = fadeIn(tween(TRANSITION_DURATION)) +
        slideInHorizontally(tween(TRANSITION_DURATION)) { it / 6 }

private val popExitTransition = fadeOut(tween(TRANSITION_DURATION)) +
        slideOutHorizontally(tween(TRANSITION_DURATION)) { -it / 6 }

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Auth        : Screen("auth",         "تسجيل الدخول", Icons.Default.Lock)
    object Home        : Screen("home",         "الرئيسية",     Icons.Default.Home)
    object Flashcards  : Screen("flashcards",   "البطاقات",     Icons.Default.Style)
    object Study       : Screen("study",        "المراجعة",     Icons.Default.School)
    object QA          : Screen("qa",           "الأسئلة",      Icons.Default.QuestionAnswer)
    object Search      : Screen("search",       "البحث",        Icons.Default.Search)
    object Profile     : Screen("profile",      "حسابي",        Icons.Default.Person)
    object Onboarding  : Screen("onboarding",   "مرحباً",       Icons.Default.AutoAwesome)
    object Bookmarks   : Screen("bookmarks",    "المحفوظات",    Icons.Default.Bookmark)
    object Leaderboard : Screen("leaderboard",  "المتصدرون",    Icons.Default.EmojiEvents)
    object Permissions : Screen("permissions",  "الأذونات",     Icons.Default.Security)
}

// Study is accessed from Flashcards screen, not a standalone bottom nav item
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Flashcards,
    Screen.QA,
    Screen.Search,
    Screen.Profile
)

private val noBottomBarRoutes = setOf(
    Screen.Auth.route,
    Screen.Study.route,
    Screen.Onboarding.route,
    Screen.Leaderboard.route,
    Screen.Bookmarks.route,
    Screen.Permissions.route
)

@Composable
fun EduSpecialNavHost(prefs: UserPreferencesDataStore) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine start destination asynchronously to avoid blocking the main thread.
    // Show a blank loading screen until the check completes (typically < 50ms).
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val permissionsDone = prefs.isPermissionsDone.first()

        // Show permissions screen on very first launch (before auth/onboarding)
        if (!permissionsDone) {
            startDestination = Screen.Permissions.route
            return@LaunchedEffect
        }

        startDestination = if (firebaseUser == null) {
            Screen.Auth.route
        } else {
            val onboardingDone = prefs.isOnboardingDone.first()
            if (onboardingDone) Screen.Home.route else Screen.Onboarding.route
        }
    }

    // Show nothing (or a minimal splash) while resolving the start destination
    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize())
        return
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        // Update check — runs once after the user is authenticated
        val updateViewModel: UpdateViewModel = hiltViewModel()
        LaunchedEffect(startDestination) {
            if (startDestination == Screen.Home.route) {
                updateViewModel.checkForUpdate()
            }
        }
        // Global update dialog — overlays any screen
        UpdateDialogHost(viewModel = updateViewModel)

        Scaffold(
            topBar = {
                // API health banner — shown when backend is degraded or unavailable
                ApiStatusBanner()
            },
            bottomBar = {
                if (currentRoute !in noBottomBarRoutes) {
                    NavigationBar(
                        tonalElevation = 3.dp,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        val currentDestination = navBackStackEntry?.destination
                        bottomNavItems.forEach { screen ->
                            val selected = currentDestination?.hierarchy
                                ?.any { it.route == screen.route } == true
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        screen.icon,
                                        contentDescription = screen.label,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        screen.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                },
                                selected = selected,
                                alwaysShowLabel = true,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor   = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor      = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination!!,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                composable(Screen.Auth.route)        { AuthScreen(navController) }
                composable(Screen.Home.route)        { HomeScreen(navController, innerPadding) }
                composable(Screen.Flashcards.route)  { FlashcardsScreen(navController, innerPadding) }
                composable(Screen.Study.route)       { StudyScreen(navController) }
                composable(Screen.QA.route)          { QAScreen(navController, innerPadding) }
                composable(Screen.Search.route)      { SearchScreen(navController, innerPadding) }
                composable(Screen.Profile.route)     { ProfileScreen(navController, innerPadding) }
                composable(Screen.Onboarding.route)  { OnboardingScreen(navController) }
                composable(Screen.Bookmarks.route)   { BookmarksScreen(navController, innerPadding) }
                composable(Screen.Leaderboard.route) { LeaderboardScreen(navController, innerPadding) }
                composable(Screen.Permissions.route) {
                    // After permissions: go to Auth if not logged in, Onboarding if first time, else Home
                    val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    val nextRoute = if (firebaseUser == null) {
                        Screen.Auth.route
                    } else {
                        // Will be resolved by the normal flow — go to Onboarding check
                        Screen.Onboarding.route
                    }
                    PermissionRequestScreen(
                        navController = navController,
                        nextRoute = nextRoute
                    )
                }
            }
        }
    }
}

/**
 * Shows a colored banner at the top of the screen when the API is degraded or unavailable.
 * Injected via Hilt — no need to pass it down the composable tree.
 */
@Composable
private fun ApiStatusBanner(
    healthMonitor: ApiHealthMonitor = hiltViewModel<ApiStatusViewModel>().healthMonitor
) {
    val status by healthMonitor.status.collectAsState()

    data class BannerConfig(val message: String, val color: Color, val icon: androidx.compose.ui.graphics.vector.ImageVector)

    val config: BannerConfig? = when (status) {
        ApiHealthMonitor.ApiStatus.OFFLINE ->
            BannerConfig("لا يوجد اتصال بالإنترنت — وضع عدم الاتصال", Color(0xFF424242), Icons.Default.WifiOff)
        ApiHealthMonitor.ApiStatus.UNAVAILABLE ->
            BannerConfig("الخادم غير متاح مؤقتاً — البيانات من الذاكرة المحلية", Color(0xFFB71C1C), Icons.Default.CloudOff)
        ApiHealthMonitor.ApiStatus.DEGRADED ->
            BannerConfig("أداء الخادم بطيء — قد يستغرق التحميل وقتاً أطول", Color(0xFFE65100), Icons.Default.SignalWifiStatusbarConnectedNoInternet4)
        ApiHealthMonitor.ApiStatus.HEALTHY -> null
    }

    AnimatedVisibility(
        visible = status != ApiHealthMonitor.ApiStatus.HEALTHY,
        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
    ) {
        config?.let { cfg ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cfg.color)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .semantics { contentDescription = cfg.message },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = cfg.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = cfg.message,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Thin ViewModel just to get ApiHealthMonitor injected into a Composable */
@dagger.hilt.android.lifecycle.HiltViewModel
class ApiStatusViewModel @javax.inject.Inject constructor(
    val healthMonitor: ApiHealthMonitor
) : androidx.lifecycle.ViewModel()
