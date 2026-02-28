package com.example.movieslistapp.ui.screen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object MoviesList : Screen("movies_list")
    object MovieDetails : Screen("movie_details/{imdbId}") {
        fun createRoute(imdbId: String) = "movie_details/$imdbId"
    }
}
