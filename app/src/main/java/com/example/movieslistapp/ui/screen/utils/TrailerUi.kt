package com.example.movieslistapp.ui.screen.utils

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.example.movieslistapp.ui.viewModel.MoviesViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun TrailerButton(
    movieTitle: String,
    year: String? = null,
    onTrailerFound: (String) -> Unit
) {
    val viewModel: MoviesViewModel = koinViewModel()
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                val trailerUrl = viewModel.getTrailerForMovie(movieTitle, year).first()
                trailerUrl?.let { onTrailerFound(it) }
            }
        }
    ) {
        Text("🎬 Watch Trailer")
    }
}