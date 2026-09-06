package com.esupplemental.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.esupplemental.domain.usecases.media.SyncInitialMediaUseCase
import com.esupplemental.domain.worker.AudioPrefetchWorker
import com.esupplemental.data.local.repository.AuthRepository
import com.esupplemental.domain.utils.UserPreferences
import com.esupplemental.navigation.AppNavGraph
import com.esupplemental.navigation.Screen
import com.esupplemental.navigation.bottomNavItems
import com.esupplemental.presentation.ui.screens.SplashScreen
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPrefs: UserPreferences = koinInject()
            val darkModeSetting by userPrefs.darkModeSetting.collectAsStateWithLifecycle(initialValue = "system")
            val themeColorKey by userPrefs.themeColorSetting.collectAsStateWithLifecycle(initialValue = "navy")

            val isDark = when (darkModeSetting) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            ESupplementalTheme(darkTheme = isDark, themeColorKey = themeColorKey) {
                // State to control visibility of the splash screen
                var progress by remember { mutableFloatStateOf(0f) }
                var isSplashFinished by remember { mutableStateOf(false) }

                val syncInitialMediaUseCase: SyncInitialMediaUseCase = koinInject()

                // Simulate initialization logic (e.g., loading Room data or user stats)
                LaunchedEffect(Unit) {
                    syncInitialMediaUseCase() // Ensure data is updated with URLs

                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val prefetchRequest = OneTimeWorkRequestBuilder<AudioPrefetchWorker>()
                        .setConstraints(constraints)
                        .build()
                    WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                        AudioPrefetchWorker.WORK_NAME,
                        ExistingWorkPolicy.KEEP,
                        prefetchRequest
                    )

                    while (progress < 1f) {
                        delay(60.milliseconds) // 50 steps * 60ms = 3 seconds total duration
                        progress += 0.02f
                    }
                }

                if (!isSplashFinished) {
                    SplashScreen(progress = progress, onAnimationFinished = {
                        // Only finish if progress is complete
                        if (progress >= 1f) {
                            isSplashFinished = true
                        }
                    })
                } else {
                    ESupplementalApp()
                }
            }
        }
    }
}

@Composable
fun ESupplementalApp(
    userPrefs: UserPreferences = koinInject(),
    authRepository: AuthRepository = koinInject()
) {
    val navController = rememberNavController()

    var sessionChecked by remember { mutableStateOf(false) }
    var validatedUserId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val session = userPrefs.session.first()
        validatedUserId = session?.takeIf {
            authRepository.getCurrentUser(it.userId).isSuccess
        }?.userId
        if (session != null && validatedUserId == null) userPrefs.clearSession()
        sessionChecked = true
    }
    if (!sessionChecked) return

    val startDest = if (validatedUserId != null) Screen.Home.route else Screen.Login.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Routes where we hide the bottom nav
    val bottomNavRoutes = bottomNavItems.map { it.screen.route }.toSet()
    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { navItem ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == navItem.screen.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(navItem.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = navItem.label
                                )
                            },
                            label = {
                                Text(
                                    text = navItem.label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            navController = navController,
            startDestination = startDest
        )
    }
}
