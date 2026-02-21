package com.example.movieslistapp.ui.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import coil.compose.SubcomposeAsyncImage
import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.ui.PaginationHelper
import com.example.movieslistapp.ui.UiState
import com.example.movieslistapp.ui.screen.shimmer.ShimmerBrush
import com.example.movieslistapp.ui.screen.shimmer.ShimmerMovieItem
import com.example.movieslistapp.ui.screen.utils.MovieImagePlaceholder
import com.example.movieslistapp.ui.viewModel.MoviesViewModel
import com.example.movieslistapp.utils.SortOption
import com.example.movieslistapp.utils.SortOrder
import com.example.movieslistapp.utils.Utils.getFilterDisplayName
import com.example.movieslistapp.utils.sortMovies
import org.koin.androidx.compose.koinViewModel
import kotlin.collections.isNotEmpty


@Composable
fun MoviesListScreen(
    viewModel: MoviesViewModel = koinViewModel(),
    modifier: Modifier
) {

    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var query = remember { mutableStateOf("") }
    var selectedMovie = remember { mutableStateOf<Movie?>(null) }

    var filterDropDownExpanded by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf<SortOption>(SortOption.NONE) }
    var sortOrder by remember { mutableStateOf<SortOrder>(SortOrder.ASC) }

    val carouselGenres by viewModel.carouselGenres.collectAsStateWithLifecycle()
    val carouselMovies by viewModel.carouselMovies.collectAsStateWithLifecycle()


    var isCarouselLoading by remember { mutableStateOf(true)}

    // Add this LaunchedEffect to load carousel data
    LaunchedEffect(Unit) {
        isCarouselLoading = true
        viewModel.loadCarouselData()
        isCarouselLoading = false
    }
    LaunchedEffect(query.value) {
        if (query.value.length > 2) {
            viewModel.getSearchMovieResult(query.value.trim())
        } else {
            viewModel.getSearchMovieResult("")
        }
    }

    val sortedMovies = sortMovies(state.value.movies, sortBy, sortOrder)

    Scaffold(
        modifier = modifier.padding(16.dp),
        topBar = {
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "🎬 Discover Movies",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = query.value,
                            onValueChange = { query.value = it },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (query.value.isNotEmpty()) {
                                    IconButton(onClick = { query.value = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            placeholder = {
                                Text(
                                    text = "Search for your favorite movies...",
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.inversePrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                if (state.value.movies.isNotEmpty()) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SuggestionChip(
                            onClick = { filterDropDownExpanded = !filterDropDownExpanded },
                            label = {
                                Text("Filter: ${getFilterDisplayName(sortBy, sortOrder)}")
                            }
                        )

                        DropdownMenu(
                            expanded = filterDropDownExpanded,
                            onDismissRequest = { filterDropDownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Title (A-Z)") },
                                onClick = {
                                    sortBy = SortOption.TITLE
                                    sortOrder = SortOrder.ASC
                                    filterDropDownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Title (Z-A)") },
                                onClick = {
                                    sortBy = SortOption.TITLE
                                    sortOrder = SortOrder.DESC
                                    filterDropDownExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Year (Oldest First)") },
                                onClick = {
                                    sortBy = SortOption.YEAR
                                    sortOrder = SortOrder.ASC
                                    filterDropDownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Year (Newest First)") },
                                onClick = {
                                    sortBy = SortOption.YEAR
                                    sortOrder = SortOrder.DESC
                                    filterDropDownExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Clear Filter") },
                                onClick = {
                                    sortBy = SortOption.NONE
                                    filterDropDownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.LightGray))
            if (state.value.movies.isEmpty() && !state.value.isLoading && query.value.isEmpty()) {
                if (isCarouselLoading) {
                    // Show shimmer loading state
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(5) { index ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    // Shimmer for category title
                                    Box(
                                        modifier = Modifier
                                            .width(100.dp)
                                            .height(24.dp)
                                            .padding(horizontal = 16.dp, vertical = 4.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ShimmerBrush())
                                    )

                                    // Shimmer for movie items
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        items(10) { index ->
                                            ShimmerMovieItem()
                                        }
                                    }
                            }
                        }
                    }
                } else if (carouselGenres.isNotEmpty()) {
                    // Show actual carousel
                    CarouselSection(
                        carouselGenres = carouselGenres,
                        carouselMovies = carouselMovies,
                        onMovieClick = { movieDetails ->
                            val movie = Movie(
                                imdbID = movieDetails.imdbID,
                                Title = movieDetails.Title,
                                Year = movieDetails.Year,
                                Poster = movieDetails.Poster
                            )
                            selectedMovie.value = movie
                        }
                    )
                } else {
                    // Show empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No movies in database. Start searching!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }


            if (state.value.movies.isNotEmpty()) {

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
                ) {
                    item {
                        DropdownMenu(expanded = filterDropDownExpanded, onDismissRequest = {}) { }
                    }
                    items(sortedMovies, key = { it.imdbID }) {
                        MovieItem(it) {
                            Log.d("test", "MovieItem: selectedMovie.value: ${selectedMovie.value}")
                            selectedMovie.value = it
                        }
                    }
                }
            }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                .clickable { selectedMovie.value = null }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.85f)
                    .align(Alignment.Center)
                    .clickable {},
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                MovieDetailsScreen(
                    it,
                    { selectedMovie.value = null },
                    { viewModel.getMovieDetails(it.imdbID) },
                    state
                )
            }
        }
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
            SubcomposeAsyncImage(
                model = movie.Poster,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp, 150.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .size(100.dp, 150.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .background(ShimmerBrush())
                    )
                },
                error = {
                    MovieImagePlaceholder(
                        modifier = Modifier
                            .size(100.dp, 150.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                        showIcon = true
                    )
                }
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
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
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

                    SubcomposeAsyncImage(
                        model = details.Poster,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp, 180.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .size(100.dp, 150.dp)
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                    .background(ShimmerBrush())
                            )
                        },
                        error = {
                            MovieImagePlaceholder(
                                modifier = Modifier
                                    .size(100.dp, 150.dp)
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                                showIcon = true
                            )
                        }
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
