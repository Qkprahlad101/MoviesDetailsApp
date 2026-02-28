package com.example.movieslistapp.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.movieslistapp.ui.viewModel.MoviesViewModel
import com.example.trailer_player.TrailerPlayer
import com.example.trailer_player.YoutubeUtils
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    imdbId: String,
    onBackPressed: () -> Unit,
    viewModel: MoviesViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    val screenHeight = configuration.screenHeightDp.dp
    val maxHeaderHeight = screenHeight * 0.4f
    val minHeaderHeight = 64.dp + 48.dp // Toolbar height + some padding/status bar
    
    val maxHeaderHeightPx = with(density) { maxHeaderHeight.toPx() }
    val minHeaderHeightPx = with(density) { minHeaderHeight.toPx() }
    val scrollDistancePx = maxHeaderHeightPx - minHeaderHeightPx
    
    // Calculate progress (0.0 at expanded, 1.0 at collapsed)
    val collapseProgress = (scrollState.value.toFloat() / scrollDistancePx).coerceIn(0f, 1f)
    
    var isTrailerPlaying by remember { mutableStateOf(false) }
    var isPosterExpanded by remember { mutableStateOf(false) }

    // Navigation Handling
    BackHandler {
        if (isPosterExpanded) {
            isPosterExpanded = false
        } else if (isTrailerPlaying) {
            isTrailerPlaying = false
        } else {
            onBackPressed()
        }
    }

    LaunchedEffect(imdbId) {
        viewModel.incrementOpenCount(imdbId)
        viewModel.getMovieDetails(imdbId)
    }

    val movieDetails = state.movieDetails
    val trailerUrl by remember(movieDetails) {
        if (movieDetails != null && movieDetails.Title.isNotBlank()) {
            viewModel.getTrailerForMovie(imdbId, movieDetails.Title, movieDetails.Year)
        } else {
            emptyFlow()
        }
    }.collectAsState(initial = null)
    
    val videoId = trailerUrl?.let { YoutubeUtils.extractVideoId(it) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        if (state.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        
        movieDetails?.let { details ->
            // Main Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Spacer that matches the header's expanded height
                Spacer(modifier = Modifier.height(maxHeaderHeight))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
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
                                text = "${details.Year} \u2022 ${details.Rated ?: "N/A"} \u2022 ${details.Runtime ?: "N/A"}",
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
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Collapsible Header Toolbar
            val currentHeaderHeight = maxHeaderHeight - (scrollState.value.dp).coerceAtMost(maxHeaderHeight - minHeaderHeight)
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(currentHeaderHeight)
                    .background(Color.Black)
            ) {
                // Background Video/Thumbnail (Fades out as we collapse)
                Box(modifier = Modifier
                    .fillMaxSize()
                    .alpha(1f - (collapseProgress * 1.5f).coerceIn(0f, 1f))
                ) {
                    if (isTrailerPlaying && videoId != null) {
                        TrailerPlayer(
                            youtubeVideoId = videoId,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        if (videoId != null) {
                            SubcomposeAsyncImage(
                                model = "https://img.youtube.com/vi/$videoId/hqdefault.jpg",
                                contentDescription = "Trailer Thumbnail",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { isTrailerPlaying = true },
                                contentScale = ContentScale.Crop,
                                loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.White) } }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                            startY = 0f
                                        )
                                    )
                            )
                            // Play Button
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { isTrailerPlaying = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color.Red,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Trailer",
                                        tint = Color.White,
                                        modifier = Modifier.padding(12.dp).fillMaxSize()
                                    )
                                }
                            }
                        } else {
                            SubcomposeAsyncImage(
                                model = details.Poster,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clickable { isPosterExpanded = true },
                                contentScale = ContentScale.Crop
                            )
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                                Text("Trailer Not Available", color = Color.White)
                            }
                        }

                        // Small Poster at Bottom Left
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .width(100.dp)
                                .height(140.dp)
                                .align(Alignment.BottomStart)
                                .clickable { isPosterExpanded = true },
                            elevation = CardDefaults.cardElevation(8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            SubcomposeAsyncImage(
                                model = details.Poster,
                                contentDescription = "Poster",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                            )
                        }
                    }
                }

                // Collapsed Title (Fades in as we collapse)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 56.dp)
                        .alpha((collapseProgress - 0.5f).coerceIn(0f, 1f) * 2f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = details.Title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Fixed Back Button on Top
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(start = 8.dp, top = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Full Screen Poster View
        AnimatedVisibility(
            visible = isPosterExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { isPosterExpanded = false },
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = movieDetails?.Poster,
                    contentDescription = "Full Screen Poster",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    loading = { CircularProgressIndicator(color = Color.White) }
                )
                
                IconButton(
                    onClick = { isPosterExpanded = false },
                    modifier = Modifier
                        .statusBarsPadding()
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
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
