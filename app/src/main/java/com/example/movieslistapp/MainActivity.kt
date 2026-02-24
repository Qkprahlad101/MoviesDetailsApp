package com.example.movieslistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieslistapp.ui.screen.MovieDetailsScreen
import com.example.movieslistapp.ui.screen.MoviesListScreen
import com.example.movieslistapp.ui.screen.Screen
import com.example.movieslistapp.ui.theme.MoviesListAppTheme
import com.example.movieslistapp.ui.viewModel.MoviesViewModel
import com.example.movieslistapp.utils.NetworkObserver
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoviesListAppTheme {
                val context = LocalContext.current
                val networkObserver = remember { NetworkObserver(context) }
                val networkStatus by produceState(initialValue = NetworkObserver.Status.Available) {
                    networkObserver.observe.collect { value = it }
                }
                
                val navController = rememberNavController()
                val viewModel: MoviesViewModel = koinViewModel()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                                MaterialTheme.colorScheme.surface
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                            // Sticky No Internet Popup
                            NoInternetBanner(networkStatus)

                            NavHost(
                                navController = navController,
                                startDestination = Screen.MoviesList.route,
                                modifier = Modifier.weight(1f)
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
    }
}

@Composable
fun NoInternetBanner(status: NetworkObserver.Status) {
    AnimatedVisibility(
        visible = status == NetworkObserver.Status.Lost || status == NetworkObserver.Status.Unavailable,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Surface(
            color = Color.Red.copy(alpha = 0.9f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "No internet: Turn on internet for new movies",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
