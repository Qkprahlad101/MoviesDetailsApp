package com.example.movieslistapp.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.ui.components.AnimatedCarouselMovieItem
import com.example.movieslistapp.ui.screen.shimmer.ShimmerBrush
import com.example.movieslistapp.ui.screen.utils.MovieImagePlaceholder

// Add these new composables
@Composable
fun CategoryCarouselSection(
    category: String,
    movies: List<MovieDetails>,
    onMovieClick: (MovieDetails) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(movies) { movie ->
                AnimatedCarouselMovieItem(movie, onMovieClick)
            }
        }
    }
}

@Composable
fun CarouselMovieItem(
    movie: MovieDetails,
    onMovieClick: (MovieDetails) -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable { onMovieClick(movie) },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        SubcomposeAsyncImage(
            model = movie.Poster,
            contentDescription = movie.Title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarouselSection(
    carouselGenres: List<String>,
    carouselMovies: Map<String, List<MovieDetails>>,
    onMovieClick: (MovieDetails) -> Unit,
    onPullToRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onPullToRefresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(carouselGenres) { genre ->
                carouselMovies[genre]?.let { movies ->
                    if (movies.isNotEmpty()) {
                        CategoryCarouselSection(genre, movies, onMovieClick)
                    }
                }
            }
        }
    }
}