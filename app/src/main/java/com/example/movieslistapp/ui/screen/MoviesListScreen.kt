package com.example.movieslistapp.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.ui.PaginationHelper
import com.example.movieslistapp.ui.viewModel.MoviesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MoviesListScreen(
    viewModel: MoviesViewModel = koinViewModel(),
    modifier: Modifier
) {

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    var query = remember { mutableStateOf("") }
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        stickyHeader {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                OutlinedTextField(
                    value = query.value,
                    onValueChange = {
                        query.value = it
                        viewModel.getSearchMovieResult(it.toString().trim())
                    },
                    label = { Text("Search a Movie..") }
                )
            }
        }

        items(
            items = state.value.movies,
            key = { it.imdbID }
        ) { movie ->
            MovieItem(movie = movie)
        }

        if (state.value.isLoading) {
            item {
                CircularProgressIndicator()
            }
        }

        if (state.value.error != null) {
            item {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.value.error.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp
                        )
                    )
                }

            }
        }
    }

    PaginationHelper(
        listState = listState,
        onLoadMore = { viewModel.getSearchMovieResult(viewModel.movieQuery) }
    )
}

@Composable
fun MovieItem(movie: Movie) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = movie.title)
    }
}

