package com.example.movieslistapp.ui.screen

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.ui.PaginationHelper
import com.example.movieslistapp.ui.screen.shimmer.ShimmerBrush
import com.example.movieslistapp.ui.screen.shimmer.ShimmerMovieItem
import com.example.movieslistapp.ui.screen.utils.MovieImagePlaceholder
import com.example.movieslistapp.ui.viewModel.MoviesViewModel
import com.example.movieslistapp.utils.SortOption
import com.example.movieslistapp.utils.SortOrder
import com.example.movieslistapp.utils.Utils.getFilterDisplayName
import com.example.movieslistapp.utils.sortMovies


@Composable
fun MoviesListScreen(
    viewModel: MoviesViewModel,
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val query by viewModel.movieQuery.collectAsStateWithLifecycle()

    var filterDropDownExpanded by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf<SortOption>(SortOption.NONE) }
    var sortOrder by remember { mutableStateOf<SortOrder>(SortOrder.ASC) }

    val carouselGenres by viewModel.carouselGenres.collectAsStateWithLifecycle()
    val carouselMovies by viewModel.carouselMovies.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()


    var isCarouselLoading by remember { mutableStateOf(true)}
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Handle back button press to show exit dialog
    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = "Are you leaving?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Do you really have to?? \uD83D\uDE14",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { (context as? Activity)?.finish() }
                ) {
                    Text(
                        text = "Gotta Go!",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text(
                        text = "Kidding!! \uD83D\uDE06",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }

    // Add this LaunchedEffect to load carousel data
    LaunchedEffect(Unit) {
        if (carouselGenres.isEmpty()) {
            isCarouselLoading = true
            viewModel.loadCarouselData(true)
            isCarouselLoading = false
        } else {
            isCarouselLoading = false
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
                            value = query,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
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
            if (state.value.movies.isEmpty() && !state.value.isLoading && query.isEmpty()) {
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
                            onMovieClick(movieDetails.imdbID)
                        },
                        onPullToRefresh = {viewModel.refreshCarousel() },
                        isRefreshing = isRefreshing
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

            // Case for search with no results
            if (state.value.movies.isEmpty() && !state.value.isLoading && query.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "🔍",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No such movies found for \"${query}\"",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please try again with a different keyword.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
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
                            onMovieClick(it.imdbID)
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

    PaginationHelper(
        listState = listState,
        onLoadMore = { viewModel.getSearchMovieResult(query) }
    )

}
@Composable
fun MovieItem(movie: Movie, onMovieClick: () -> Unit) {
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
                SuggestionChip(onClick = { onMovieClick() }, label = { Text("Details") })
            }
        }
    }
}
