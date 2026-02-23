package com.example.movieslistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieslistapp.ui.screen.MovieDetailsScreen
import com.example.movieslistapp.ui.screen.MoviesListScreen
import com.example.movieslistapp.ui.screen.Screen
import com.example.movieslistapp.ui.theme.MoviesListAppTheme
import com.example.movieslistapp.ui.viewModel.MoviesViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoviesListAppTheme {
                val navController = rememberNavController()
                val viewModel: MoviesViewModel = koinViewModel()
                
                Scaffold(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.surface
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.MoviesList.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.MoviesList.route) {
                            MoviesListScreen(
                                viewModel = viewModel,
                                onMovieClick = { imdbId ->
                                    navController.navigate(Screen.MovieDetails.createRoute(imdbId))
                                }
                            )
                        }
                        composable(Screen.MovieDetails.route) { backStackEntry ->
                            val imdbId = backStackEntry.arguments?.getString("imdbId") ?: ""
                            MovieDetailsScreen(
                                imdbId = imdbId,
                                onBackPressed = { navController.popBackStack() },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
