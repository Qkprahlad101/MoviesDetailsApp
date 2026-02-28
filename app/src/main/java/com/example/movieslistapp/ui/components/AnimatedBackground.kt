package com.example.movieslistapp.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")
    
    val animatedOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset1"
    )
    
    val animatedOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset2"
    )
    
    val animatedOffset3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset3"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E).copy(alpha = 0.8f),
                            Color(0xFF16213E).copy(alpha = 0.6f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = 0.3f + 0.2f * sin(Math.toRadians(animatedOffset1.toDouble())).toFloat(),
                            y = 0.3f + 0.2f * cos(Math.toRadians(animatedOffset1.toDouble())).toFloat()
                        ),
                        radius = 800f
                    )
                )
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0F3460).copy(alpha = 0.6f),
                            Color(0xFF533483).copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = 0.7f + 0.15f * sin(Math.toRadians(animatedOffset2.toDouble())).toFloat(),
                            y = 0.6f + 0.15f * cos(Math.toRadians(animatedOffset2.toDouble())).toFloat()
                        ),
                        radius = 600f
                    )
                )
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE94560).copy(alpha = 0.1f),
                            Color.Transparent,
                            Color(0xFF0F3460).copy(alpha = 0.1f)
                        ),
                        start = Offset(
                            x = sin(Math.toRadians(animatedOffset3.toDouble())).toFloat(),
                            y = cos(Math.toRadians(animatedOffset3.toDouble())).toFloat()
                        ),
                        end = Offset(
                            x = sin(Math.toRadians((animatedOffset3 + 180).toDouble())).toFloat(),
                            y = cos(Math.toRadians((animatedOffset3 + 180).toDouble())).toFloat()
                        )
                    )
                )
        )
    }
}

@Composable
fun FuturisticGradientOverlay(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient_animation")
    
    val animatedGradient by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = androidx.compose.animation.core.EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF1A1A2E).copy(alpha = 0.1f * animatedGradient),
                        Color(0xFF16213E).copy(alpha = 0.2f * animatedGradient),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    )
}
