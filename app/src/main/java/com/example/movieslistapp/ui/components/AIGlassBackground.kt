package com.example.movieslistapp.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.example.movieslistapp.ui.theme.AuroraCyan
import com.example.movieslistapp.ui.theme.CosmicBlue
import com.example.movieslistapp.ui.theme.DeepSpace
import com.example.movieslistapp.ui.theme.NebulaPurple
import com.example.movieslistapp.ui.theme.PlasmaPink
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AIGlassBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_background_animation")
    
    val animatedOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ai_offset1"
    )
    
    val animatedOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ai_offset2"
    )
    
    val animatedOffset3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(35000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ai_offset3"
    )
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = androidx.compose.animation.core.EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Deep space base layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepSpace)
        )
        
        // AI neural network gradient 1
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NebulaPurple.copy(alpha = pulseAlpha * 0.8f),
                            AuroraCyan.copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = 0.3f + 0.25f * sin(Math.toRadians(animatedOffset1.toDouble())).toFloat(),
                            y = 0.3f + 0.25f * cos(Math.toRadians(animatedOffset1.toDouble())).toFloat()
                        ),
                        radius = 1000f
                    )
                )
        )
        
        // AI neural network gradient 2
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PlasmaPink.copy(alpha = pulseAlpha * 0.6f),
                            NebulaPurple.copy(alpha = pulseAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = 0.7f + 0.2f * sin(Math.toRadians(animatedOffset2.toDouble())).toFloat(),
                            y = 0.6f + 0.2f * cos(Math.toRadians(animatedOffset2.toDouble())).toFloat()
                        ),
                        radius = 800f
                    )
                )
        )
        
        // AI scanning beam effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AuroraCyan.copy(alpha = pulseAlpha * 0.3f),
                            Color.Transparent,
                            PlasmaPink.copy(alpha = pulseAlpha * 0.2f),
                            Color.Transparent,
                            CosmicBlue.copy(alpha = pulseAlpha * 0.3f)
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
        
        // AI particle field overlay
        AIParticleField(
            modifier = Modifier.fillMaxSize(),
            animatedOffset = animatedOffset1
        )
    }
}

@Composable
fun AIParticleField(
    modifier: Modifier = Modifier,
    animatedOffset: Float
) {
    Canvas(modifier = modifier) {
        val particleCount = 50
        val radius = size.minDimension / 2
        
        repeat(particleCount) { index ->
            val angle = (index * 360f / particleCount + animatedOffset) * PI / 180f
            val distance = radius * (0.3f + 0.4f * sin(angle * 3 + animatedOffset * 0.05f))
            
            val x = (size.width / 2 + distance * cos(angle)).toFloat()
            val y = (size.height / 2 + distance * sin(angle)).toFloat()
            
            val particleSize = (2f + 2f * sin(angle * 2 + animatedOffset * 0.1f)).toFloat()
            val alpha = (0.3f + 0.3f * sin(angle * 4 + animatedOffset * 0.08f)).toFloat()
            
            drawCircle(
                color = when (index % 3) {
                    0 -> AuroraCyan.copy(alpha = alpha)
                    1 -> NebulaPurple.copy(alpha = alpha)
                    else -> PlasmaPink.copy(alpha = alpha)
                },
                radius = particleSize,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun AIGlassOverlay(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_overlay_animation")
    
    val animatedGradient by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = androidx.compose.animation.core.EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ai_gradient"
    )
    
    val scanLine by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_line"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        NebulaPurple.copy(alpha = 0.05f * animatedGradient),
                        AuroraCyan.copy(alpha = 0.08f * animatedGradient),
                        PlasmaPink.copy(alpha = 0.05f * animatedGradient),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        // AI scanning line effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            AuroraCyan.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

fun DrawScope.drawAIGrid(
    gridSize: Float = 50f,
    alpha: Float = 0.1f,
    animatedOffset: Float = 0f
) {
    val gridColor = AuroraCyan.copy(alpha = alpha)
    
    // Vertical lines with animation
    for (x in 0..this.size.width.toInt() step gridSize.toInt()) {
        val animatedX = x + (animatedOffset * gridSize % gridSize)
        drawLine(
            color = gridColor,
            start = Offset(animatedX, 0f),
            end = Offset(animatedX, this.size.height),
            strokeWidth = 0.5f
        )
    }
    
    // Horizontal lines with animation
    for (y in 0..this.size.height.toInt() step gridSize.toInt()) {
        val animatedY = y + (animatedOffset * gridSize % gridSize)
        drawLine(
            color = gridColor,
            start = Offset(0f, animatedY),
            end = Offset(this.size.width, animatedY),
            strokeWidth = 0.5f
        )
    }
}
