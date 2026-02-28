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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FuturisticLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "futuristic_loading")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier.size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(64.dp)
                .scale(scale)
        ) {
            drawFuturisticLoader(rotation, color)
        }
    }
}

private fun DrawScope.drawFuturisticLoader(rotation: Float, color: Color) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = size.minDimension / 2 - 8.dp.toPx()
    
    // Outer rotating ring
    drawArc(
        brush = Brush.linearGradient(
            colors = listOf(
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.8f),
                color.copy(alpha = 0.3f)
            )
        ),
        startAngle = rotation,
        sweepAngle = 120f,
        useCenter = false,
        topLeft = center - Offset(radius, radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
    )
    
    // Inner rotating ring (opposite direction)
    drawArc(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFE94560).copy(alpha = 0.3f),
                Color(0xFFE94560).copy(alpha = 0.8f),
                Color(0xFFE94560).copy(alpha = 0.3f)
            )
        ),
        startAngle = -rotation * 1.5f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = center - Offset(radius * 0.7f, radius * 0.7f),
        size = Size(radius * 1.4f, radius * 1.4f),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )
    
    // Center dot
    drawCircle(
        color = color,
        radius = 4.dp.toPx(),
        center = center
    )
    
    // Orbiting dots
    for (i in 0 until 3) {
        val angle = Math.toRadians((rotation + i * 120).toDouble())
        val dotRadius = radius * 0.8f
        val dotX = center.x + (cos(angle) * dotRadius).toFloat()
        val dotY = center.y + (sin(angle) * dotRadius).toFloat()
        
        drawCircle(
            color = Color(0xFF533483).copy(alpha = 0.6f),
            radius = 2.dp.toPx(),
            center = Offset(dotX, dotY)
        )
    }
}

@Composable
fun PulsatingLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsating_loading")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.scale(scale),
            color = color.copy(alpha = alpha),
            strokeWidth = 3.dp
        )
    }
}

@Composable
fun ParticleLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particle_loading")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(80.dp)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 16.dp.toPx()
            
            for (i in 0 until 8) {
                val angle = Math.toRadians((rotation + i * 45).toDouble())
                val particleRadius = radius * (0.5f + 0.5f * sin((rotation * 2 + i * 90) * PI.toFloat() / 180f))
                val particleX = center.x + (cos((rotation + i * 45) * PI.toFloat() / 180f) * particleRadius)
                val particleY = center.y + (sin((rotation + i * 45) * PI.toFloat() / 180f) * particleRadius)
                
                val particleAlpha = (0.3f + 0.7f * sin((rotation * 3 + i * 120) * PI.toFloat() / 180f))
                
                drawCircle(
                    color = color.copy(alpha = particleAlpha.coerceIn(0f, 1f)),
                    radius = 3.dp.toPx(),
                    center = Offset(particleX, particleY)
                )
            }
            
            // Center core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color,
                        color.copy(alpha = 0.5f)
                    )
                ),
                radius = 8.dp.toPx(),
                center = center
            )
        }
    }
}
