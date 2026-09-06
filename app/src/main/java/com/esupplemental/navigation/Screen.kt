package com.esupplemental.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Bottom Nav Destinations
    object Home : Screen("home")
    object Progress : Screen("progress")
    object Library : Screen("library")
    object Profile : Screen("profile")
    object Login : Screen("login")
    object Register : Screen("register")
    object Game : Screen("game")

    object GamePlayer : Screen("game_player/{gameId}?mediaId={mediaId}") {
        fun createRoute(gameId: String, mediaId: String? = null) = 
            "game_player/$gameId" + if (mediaId != null) "?mediaId=$mediaId" else ""
    }

    // Sub-screens (not in bottom nav)
    object SongPlayer : Screen("song_player/{mediaId}") {
        fun createRoute(mediaId: String) = "song_player/$mediaId"
    }
    object StoryPlayer : Screen("story_player/{mediaId}") {
        fun createRoute(mediaId: String) = "story_player/$mediaId"
    }
    object StoryExercise : Screen("story_exercise/{mediaId}") {
        fun createRoute(mediaId: String) = "story_exercise/$mediaId"
    }
    object NoteTaking : Screen("note_taking/{noteId}") {
        fun createRoute(noteId: String = "new") = "note_taking/$noteId"
    }
    object NoteList : Screen("note_list")
    object QuizResult : Screen("quiz_result/{score}/{total}/{resultId}/{mediaId}") {
        fun createRoute(score: Int, total: Int, resultId: Long, mediaId: String): String {
            return "quiz_result/$score/$total/$resultId/$mediaId"
        }
    }
    object GeneralSettings : Screen("general_settings")
    object ProfileSettings : Screen("profile_settings")
}

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Rounded.Home),
    BottomNavItem(Screen.Progress, "Progress", Icons.AutoMirrored.Rounded.ShowChart),
    BottomNavItem(Screen.Library, "Library", Icons.AutoMirrored.Rounded.LibraryBooks),
    BottomNavItem(Screen.Profile, "Profile", Icons.Rounded.Person)
)
