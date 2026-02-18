package com.example.movieslistapp.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.ui.PaginationHelper
import com.example.movieslistapp.ui.UiState
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

    var selectedMovie = remember { mutableStateOf<Movie?>(null) }
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
                        viewModel.getSearchMovieResult(it.trim())
                    },
                    label = { Text("Search a Movie..") }
                )
            }
        }

        items(
            items = state.value.movies,
            key = { it.imdbID }
        ) { movie ->
            MovieItem(movie = movie, { selectedMovie.value = it })
        }

        if (state.value.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

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

    selectedMovie.value?.let {
        MovieDetailsScreen(
            it,
            { selectedMovie.value = null },
            { viewModel.getMovieDetails(it.imdbID) },
            state
        )
    }

    PaginationHelper(
        listState = listState,
        onLoadMore = { viewModel.getSearchMovieResult(viewModel.movieQuery) }
    )
}

@Composable
fun MovieItem(movie: Movie, selectedMovie: (Movie) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = movie.Poster,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp, 150.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = movie.Title, style = MaterialTheme.typography.titleLarge, maxLines = 2)
                Text(text = "Year: ${movie.Year}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                SuggestionChip(onClick = { selectedMovie(movie) }, label = { Text("Details") })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movie: Movie,
    dismiss: () -> Unit,
    getMovieDetails: () -> Unit,
    state: State<UiState>
) {
    BackHandler(onBack = dismiss)

    LaunchedEffect(movie.imdbID) {
        getMovieDetails()
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.value.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        if (state.value.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.value.error.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        state.value.movieDetails?.let { details ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = details.Poster,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp, 180.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = details.Title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${details.Year} • ${details.Rated ?: "N/A"} • ${details.Runtime ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = details.Genre ?: "N/A",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                details.Plot?.let { plot ->
                    Text(
                        text = "Plot",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plot,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                DetailSection("Cast & Crew") {
                    DetailItem("Director", details.Director)
                    DetailItem("Writer", details.Writer)
                    DetailItem("Actors", details.Actors)
                }

                DetailSection("Additional Information") {
                    DetailItem("Released", details.Released)
                    DetailItem("Language", details.Language)
                    DetailItem("Country", details.Country)
                    DetailItem("Awards", details.Awards)
                }

                // Ratings
                details.Ratings?.let { ratings ->
                    DetailSection("Ratings") {
                        ratings.forEach { rating ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = rating.Source,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = rating.Value,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (details.imdbRating != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "IMDb Rating",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${details.imdbRating}/10 (${details.imdbVotes ?: "0"} votes)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Box Office
                if (!details.BoxOffice.isNullOrBlank() && details.BoxOffice != "N/A") {
                    DetailSection("Box Office") {
                        Text(
                            text = details.BoxOffice,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun DetailSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String?
) {
    if (!value.isNullOrBlank() && value != "N/A") {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
