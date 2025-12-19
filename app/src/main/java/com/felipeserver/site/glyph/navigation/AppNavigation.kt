package com.felipeserver.site.glyph.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.felipeserver.site.glyph.ui.screen.MainViewScreen
import com.felipeserver.site.glyph.ui.screen.NoteView

sealed class Screen(val route: String){

    object Main: Screen("mainView")
    object Note: Screen("noteView/{noteId}"){
        /**
         * Creates a route to a specific note screen by replacing the placeholder "{noteId}"
         * with the provided note ID.
         *
         * @param noteId The unique identifier of the note to navigate to.
         * @return The complete navigation route string for the specified note.
         */
        fun withArgs(noteId: String): String{
            return "noteView/$noteId"
        }
    }
    object Settings: Screen("settingsView")

}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainViewScreen(navController = navController)
        }
        composable(
            route = Screen.Note.route,
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            NoteView(
                id = backStackEntry.arguments?.getString("noteId"),
                navController = navController
            )
        }
    }
}
