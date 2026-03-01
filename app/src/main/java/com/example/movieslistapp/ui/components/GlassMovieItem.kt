package com.example.movieslistapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.ui.theme.AuroraCyan
import com.example.movieslistapp.ui.theme.GlassBorder
import com.example.movieslistapp.ui.theme.GlassHighlight
import com.example.movieslistapp.ui.theme.GlassOverlay
import com.example.movieslistapp.ui.theme.NebulaPurple
import com.example.movieslistapp.ui.theme.PlasmaPink
import com.example.movieslistapp.ui.screen.shimmer.ShimmerBrush
import com.example.movieslistapp.ui.screen.utils.MovieImagePlaceholder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GlassMovieItem(
    movie: Movie,
    onMovieClick: () -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glass_animation")
    
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )
    
    val shimmerFloat by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_float"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true,
                    radius = 12.dp,
                    color = NebulaPurple.copy(alpha = 0.3f)
                )
            ) { onMovieClick() }
            .graphicsLayer {
                alpha = 0.95f
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 16.dp
        ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            GlassOverlay.copy(alpha = 0.7f),
                            GlassBorder.copy(alpha = 0.5f),
                            GlassOverlay.copy(alpha = 0.7f)
                        ),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NebulaPurple.copy(alpha = glowPulse * 0.1f),
                            AuroraCyan.copy(alpha = glowPulse * 0.05f),
                            Color.Transparent
                        ),
                        radius = 300f
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(2.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            GlassOverlay.copy(alpha = 0.3f),
                            GlassBorder.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Glass movie poster container
                Box(
                    modifier = Modifier
                        .size(100.dp, 150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    GlassBorder.copy(alpha = 0.3f),
                                    GlassHighlight.copy(alpha = 0.2f),
                                    GlassBorder.copy(alpha = 0.3f)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(1.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GlassOverlay.copy(alpha = 0.4f),
                                    GlassBorder.copy(alpha = 0.2f)
                                )
                            ),
                            shape = RoundedCornerShape(15.dp)
                        )
                ) {
                    SubcomposeAsyncImage(
                        model = movie.Poster,
                        contentDescription = movie.Title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                            .graphicsLayer {
                                alpha = 0.95f
                            },
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
                                    .clip(RoundedCornerShape(14.dp)),
                                showIcon = true
                            )
                        }
                    )
                    
                    // Glass overlay effect on poster
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        GlassOverlay.copy(alpha = 0.3f),
                                        GlassBorder.copy(alpha = 0.4f)
                                    )
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = movie.Title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        modifier = Modifier.graphicsLayer {
                            alpha = 0.95f
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Glass info container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        GlassOverlay.copy(alpha = 0.2f),
                                        GlassBorder.copy(alpha = 0.1f),
                                        GlassOverlay.copy(alpha = 0.2f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Year: ${movie.Year}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Glass action button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        NebulaPurple.copy(alpha = 0.2f),
                                        AuroraCyan.copy(alpha = 0.3f),
                                        PlasmaPink.copy(alpha = 0.2f)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    bounded = true,
                                    radius = 8.dp,
                                    color = AuroraCyan.copy(alpha = 0.4f)
                                )
                            ) { onMovieClick() }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "View Details",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            // Animated glass border effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                NebulaPurple.copy(alpha = glowPulse * 0.2f),
                                AuroraCyan.copy(alpha = glowPulse * 0.15f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(
                                x = cos(Math.toRadians(shimmerFloat * 180.toDouble())).toFloat(),
                                y = sin(Math.toRadians(shimmerFloat * 180.toDouble())).toFloat()
                            ),
                            end = androidx.compose.ui.geometry.Offset(
                                x = cos(Math.toRadians((shimmerFloat * 180 + 180).toDouble())).toFloat(),
                                y = sin(Math.toRadians((shimmerFloat * 180 + 180).toDouble())).toFloat()
                            )
                        ),
                        shape = RoundedCornerShape(19.dp)
                    )
            )
        }
    }
}

@Composable
fun GlassCarouselMovieItem(
    movie: com.example.movieslistapp.data.model.MovieDetails,
    onMovieClick: (com.example.movieslistapp.data.model.MovieDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "carousel_glass_animation")
    
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "carousel_glow_pulse"
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
                    color = NebulaPurple.copy(alpha = 0.4f)
                )
            ) { onMovieClick(movie) }
            .graphicsLayer {
                alpha = 0.95f
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp,
            pressedElevation = 20.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            GlassOverlay.copy(alpha = 0.8f),
                            GlassBorder.copy(alpha = 0.6f),
                            GlassOverlay.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AuroraCyan.copy(alpha = glowPulse * 0.15f),
                            PlasmaPink.copy(alpha = glowPulse * 0.1f),
                            Color.Transparent
                        ),
                        radius = 150f
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(2.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GlassOverlay.copy(alpha = 0.4f),
                            GlassBorder.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
        ) {
            SubcomposeAsyncImage(
                model = movie.Poster,
                contentDescription = movie.Title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .graphicsLayer {
                        alpha = 0.95f
                    },
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
                            .clip(RoundedCornerShape(12.dp)),
                        showIcon = true
                    )
                }
            )
            
            // Glass overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                GlassOverlay.copy(alpha = 0.4f),
                                GlassBorder.copy(alpha = 0.3f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
            
            // Animated glass border
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                NebulaPurple.copy(alpha = glowPulse * 0.3f),
                                AuroraCyan.copy(alpha = glowPulse * 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(13.dp)
                    )
            )
        }
    }
}
