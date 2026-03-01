package com.example.movieslistapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.ui.screen.shimmer.ShimmerBrush
import com.example.movieslistapp.ui.screen.utils.MovieImagePlaceholder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedCarouselMovieItem(
    movie: MovieDetails,
    onMovieClick: (MovieDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "carousel_animation")
    
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = modifier
            .width(120.dp)
            .height(180.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true,
                    radius = 8.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            ) { onMovieClick(movie) }
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E).copy(alpha = glowPulse * 0.1f),
                            Color(0xFF16213E).copy(alpha = glowPulse * 0.05f),
                            Color.Transparent
                        ),
                        radius = 100f
                    )
                )
        ) {
            SubcomposeAsyncImage(
                model = movie.Poster,
                contentDescription = movie.Title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
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
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp)),
                        showIcon = true
                    )
                }
            )
            
            // Futuristic overlay effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFFE94560).copy(alpha = 0.1f),
                                Color(0xFF0F3460).copy(alpha = 0.2f)
                            )
                        )
                    )
            )
            
            // Subtle animated border effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF533483).copy(alpha = glowPulse * 0.3f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(
                                x = cos(Math.toRadians(rotation.toDouble())).toFloat(),
                                y = sin(Math.toRadians(rotation.toDouble())).toFloat()
                            ),
                            end = androidx.compose.ui.geometry.Offset(
                                x = cos(Math.toRadians((rotation + 180).toDouble())).toFloat(),
                                y = sin(Math.toRadians((rotation + 180).toDouble())).toFloat()
                            )
                        )
                    )
            )
        }
    }
}
