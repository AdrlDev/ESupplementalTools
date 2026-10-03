package com.esupplemental.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.esupplemental.data.model.MediaType
import com.esupplemental.presentation.ui.screens.auth.LoginScreen
import com.esupplemental.presentation.ui.screens.auth.RegisterScreen
import com.esupplemental.presentation.ui.screens.exercise.StoryExerciseScreen
import com.esupplemental.presentation.ui.screens.exercise.SongExerciseScreen
import com.esupplemental.presentation.ui.screens.game.GameScreen
import com.esupplemental.presentation.ui.screens.game.easy.character_quest.CharacterQuestScreen
import com.esupplemental.presentation.ui.screens.game.easy.word_master.ListenSlapScreen
import com.esupplemental.presentation.ui.screens.game.easy.story_order.StoryOrderScreen
import com.esupplemental.presentation.ui.screens.game.hard.follow_directions.FollowDirectionsScreen
import com.esupplemental.presentation.ui.screens.game.hard.speed_typer.SpeedTyperScreen
import com.esupplemental.presentation.ui.screens.game.hard.story_recall.StoryRecallScreen
import com.esupplemental.presentation.ui.screens.game.moderate.disappearing_text.DisappearingTextScreen
import com.esupplemental.presentation.ui.screens.game.moderate.two_truths_lie.TwoTruthsLieScreen
import com.esupplemental.presentation.ui.screens.game.moderate.minimal_pairs.MinimalPairsScreen
import com.esupplemental.domain.model.game.GameId
import com.esupplemental.presentation.ui.screens.home.HomeScreen
import com.esupplemental.presentation.ui.screens.library.LibraryScreen
import com.esupplemental.presentation.ui.screens.notes.NoteEditorScreen
import com.esupplemental.presentation.ui.screens.notes.NoteListScreen
import com.esupplemental.presentation.ui.screens.player.MediaListScreen
import com.esupplemental.presentation.ui.screens.player.PlayerScreen
import com.esupplemental.presentation.ui.screens.profile.*
import com.esupplemental.presentation.ui.screens.progress.ProgressScreen
import com.esupplemental.presentation.ui.screens.progress.QuizResultScreen
import com.esupplemental.presentation.viewmodel.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String
) {

    // Scoped ViewModels shared across nav
    val noteViewModel: NoteViewModel = koinViewModel()

    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeOut()
        }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    // After registration, you'd typically save the UserEntity
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // ── Bottom nav destinations ───────────────────────────────────────────

        composable(Screen.Home.route) {
            HomeScreen(
                onSongsClick = { navController.navigate("media_list/song") },
                onStoriesClick = { navController.navigate("media_list/story") },
                onPoemsClick = { navController.navigate("media_list/poem") },
                onNoteToolClick = {
                    navController.navigate(Screen.NoteList.route)
                },
                onGameClick = {
                    // Navigate to first song exercise as demo quiz
                    navController.navigate(Screen.Game.route)
                }
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen()
        }

        composable(Screen.Game.route) {
            GameScreen(
                onPlayGame = { gameId ->
                    navController.navigate(Screen.GamePlayer.createRoute(gameId))
                }
            )
        }

        composable(
            route = Screen.GamePlayer.route,
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType },
                navArgument("mediaId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStack ->
            val gameId = backStack.arguments?.getString("gameId") ?: return@composable
            val mediaId = backStack.arguments?.getString("mediaId")
            val currentGame = GameId.fromIdOrDefault(gameId)
            when (currentGame) {
                GameId.LISTEN_SLAP -> ListenSlapScreen(
                    gameId = gameId,
                    mediaId = mediaId,
                    onBack = { navController.popBackStack() }
                )

                GameId.STORY_ORDER -> StoryOrderScreen(
                    gameId = gameId,
                    mediaId = mediaId,
                    onBack = { navController.popBackStack() }
                )

                GameId.CHARACTER_QUEST -> CharacterQuestScreen(
                    gameId = gameId,
                    mediaId = mediaId,
                    onBack = { navController.popBackStack() }
                )
                GameId.DIS_APPEARING_TEXT -> DisappearingTextScreen(
                    gameId = gameId,
                    mediaId = mediaId,
                    onBack = { navController.popBackStack() }
                )
                GameId.TWO_TRUTHS_LIE -> TwoTruthsLieScreen(
                    gameId = gameId,
                    mediaId = mediaId,
                    onBack = { navController.popBackStack() }
                )
                GameId.MINIMAL_PAIRS -> MinimalPairsScreen(
                    gameId = gameId,
                    mediaId = mediaId,
                    onBack = { navController.popBackStack() }
                )
                GameId.SPEED_TYPER -> SpeedTyperScreen(
                    gameId = gameId,
                    mediaId = mediaId,
                    onBack = { navController.popBackStack() }
                )
                GameId.FOLLOW_DIRECTIONS -> FollowDirectionsScreen(
                    gameId = gameId,
                    mediaId = mediaId,
                    onBack = { navController.popBackStack() }
                )
                GameId.STORY_RECALL -> StoryRecallScreen(
                    gameId = gameId,
                    mediaId = mediaId,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Library.route) {
            LibraryScreen(
                noteViewModel = noteViewModel,
                onSongClick = { navController.navigate(Screen.SongPlayer.createRoute(it)) },
                onStoryClick = { navController.navigate(Screen.StoryPlayer.createRoute(it)) },
                onPoemClick = { navController.navigate(Screen.PoemPlayer.createRoute(it)) },
                onNoteClick = { navController.navigate(Screen.NoteTaking.createRoute(it)) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onLoggedOut = {
                    navController.navigate(Screen.Login.route) {
                        // Clear the entire navigation stack
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToGeneralSettings = {
                    navController.navigate(Screen.GeneralSettings.route)
                },
                onNavigateToProfileSettings = {
                    navController.navigate(Screen.ProfileSettings.route)
                }
            )
        }

        composable(Screen.GeneralSettings.route) {
            GeneralSettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.ProfileSettings.route) {
            ProfileSettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.NoteList.route) {
            NoteListScreen(
                viewModel = noteViewModel,
                onOpenNote = { navController.navigate(Screen.NoteTaking.createRoute(it)) },
                onNewNote = { navController.navigate(Screen.NoteTaking.createRoute("new")) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Media list screens ────────────────────────────────────────────────

        composable("media_list/{type}") { backStack ->
            val typeArg = backStack.arguments?.getString("type") ?: "song"
            val type = when (typeArg) {
                "song" -> MediaType.SONG
                "poem" -> MediaType.POEM
                else -> MediaType.STORY
            }

            MediaListScreen(
                type = type,
                onItemClick = {
                    val route = when (type) {
                        MediaType.SONG -> Screen.SongPlayer.createRoute(it)
                        MediaType.POEM -> Screen.PoemPlayer.createRoute(it)
                        MediaType.STORY -> Screen.StoryPlayer.createRoute(it)
                    }
                    navController.navigate(route)
                },
                onBack = { navController.popBackStack() } // Pass the missing parameter here
            )
        }

        // ── Song player + exercise ────────────────────────────────────────────

        composable(
            route = Screen.SongPlayer.route,
            arguments = listOf(navArgument("mediaId") { type = NavType.StringType }),
            enterTransition = { fadeIn(tween(180)) },
            exitTransition = { fadeOut(tween(180)) },
            popEnterTransition = { fadeIn(tween(180)) },
            popExitTransition = { fadeOut(tween(180)) }
        ) { backStack ->
            val mediaId = backStack.arguments?.getString("mediaId") ?: return@composable
            PlayerScreen(
                mediaId = mediaId,
                onStartExercise = { navController.navigate(Screen.SongExercise.createRoute(it)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SongExercise.route,
            arguments = listOf(navArgument("mediaId") { type = NavType.StringType })
        ) { backStack ->
            val mediaId = backStack.arguments?.getString("mediaId") ?: return@composable
            SongExerciseScreen(
                mediaId = mediaId,
                onViewResults = { score, total, resultId ->
                    navController.navigate(
                        Screen.QuizResult.createRoute(score, total, resultId, mediaId, MediaType.SONG)
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Story player + exercise ───────────────────────────────────────────

        composable(
            route = Screen.StoryPlayer.route,
            arguments = listOf(navArgument("mediaId") { type = NavType.StringType }),
            enterTransition = { fadeIn(tween(180)) },
            exitTransition = { fadeOut(tween(180)) },
            popEnterTransition = { fadeIn(tween(180)) },
            popExitTransition = { fadeOut(tween(180)) }
        ) { backStack ->
            val mediaId = backStack.arguments?.getString("mediaId") ?: return@composable
            PlayerScreen(
                mediaId = mediaId,
                onStartExercise = { navController.navigate(Screen.StoryExercise.createRoute(it)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PoemPlayer.route,
            arguments = listOf(navArgument("mediaId") { type = NavType.StringType }),
            enterTransition = { fadeIn(tween(180)) },
            exitTransition = { fadeOut(tween(180)) },
            popEnterTransition = { fadeIn(tween(180)) },
            popExitTransition = { fadeOut(tween(180)) }
        ) { backStack ->
            val mediaId = backStack.arguments?.getString("mediaId") ?: return@composable
            PlayerScreen(
                mediaId = mediaId,
                onStartExercise = { navController.navigate(Screen.PoemExercise.createRoute(it)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.StoryExercise.route,
            arguments = listOf(navArgument("mediaId") { type = NavType.StringType })
        ) { backStack ->
            val mediaId = backStack.arguments?.getString("mediaId") ?: return@composable
            StoryExerciseScreen(
                mediaId = mediaId,
                onViewResults = { score, total, resultId ->
                    navController.navigate(
                        Screen.QuizResult.createRoute(
                            score,
                            total,
                            resultId,
                            mediaId
                        )
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PoemExercise.route,
            arguments = listOf(navArgument("mediaId") { type = NavType.StringType })
        ) { backStack ->
            val mediaId = backStack.arguments?.getString("mediaId") ?: return@composable
            StoryExerciseScreen(
                mediaId = mediaId,
                onViewResults = { score, total, resultId ->
                    navController.navigate(Screen.QuizResult.createRoute(score, total, resultId, mediaId, MediaType.POEM))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Note-taking ───────────────────────────────────────────────────────

        composable(
            route = Screen.NoteTaking.route,
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStack ->
            val noteId = backStack.arguments?.getString("noteId") ?: "new"
            NoteEditorScreen(
                noteId = noteId,
                viewModel = noteViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Quiz result ───────────────────────────────────────────────────────

        composable(
            route = Screen.QuizResult.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("resultId") { type = NavType.LongType },
                navArgument("mediaId") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType; defaultValue = MediaType.STORY.name }
            )
        ) { backStack ->
            val score = backStack.arguments?.getInt("score") ?: 0
            val total = backStack.arguments?.getInt("total") ?: 0
            val resultId = backStack.arguments?.getLong("resultId") ?: -1L
            val mediaId = backStack.arguments?.getString("mediaId") ?: return@composable
            val exerciseType = backStack.arguments?.getString("type") ?: MediaType.STORY.name

            QuizResultScreen(
                score = score,
                total = total,
                resultId = resultId,
                onRetry = {
                    val retryRoute = when (exerciseType) {
                        MediaType.SONG.name -> Screen.SongExercise.createRoute(mediaId)
                        MediaType.POEM.name -> Screen.PoemExercise.createRoute(mediaId)
                        else -> Screen.StoryExercise.createRoute(mediaId)
                    }
                    navController.navigate(retryRoute) {
                        popUpTo(navController.currentDestination?.route ?: Screen.QuizResult.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
