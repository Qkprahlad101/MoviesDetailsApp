package com.example.movieslistapp.ui.screen.utils

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.example.movieslistapp.ui.viewModel.MoviesViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun TrailerButton(
    viewModel: MoviesViewModel,
    movieTitle: String,
    year: String? = null,
    onTrailerFound: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                Log.d("TrailerButton", "Fetching trailer for: $movieTitle ($year)")
                val trailerUrl = viewModel.getTrailerForMovie(movieTitle, year).first()
                Log.d("TrailerButton", "Trailer URL: $trailerUrl")
                trailerUrl?.let { onTrailerFound(it) } ?: run {
                    Log.d("TrailerButton", "No trailer found for: $movieTitle")
                }
            }
        }
    ) {
        Text("🎬 Watch Trailer")
    }
}