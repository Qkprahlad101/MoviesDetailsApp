package com.example.movieslistapp.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movieslistapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToMain: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    
    // Smooth pulse for the logo
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Orbital rotation for AI particles
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Fade in for text components
    val alphaAnim = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200)
        )
        delay(2500)
        onNavigateToMain()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1B26), // Dark Center
                        Color(0xFF0D1117), // Deep Navy
                        Color.Black
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // --- 1. AI Cinematic Particles (Orbital) ---
        Box(modifier = Modifier.size(300.dp)) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation + (index * 120))
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .align(Alignment.TopCenter)
                            .background(Color(0xFF7C4DFF).copy(alpha = 0.6f), CircleShape)
                            .scale(scale)
                    )
                }
            }
        }

        // --- 2. Main Content ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main App Icon with soft halo
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(scale * 1.1f)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF7C4DFF).copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                )
                
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Elegant Typography
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MoviesList",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.alpha(alphaAnim.value)
                    )
                    
                    Text(
                        text = " AI",
                        color = Color(0xFF7C4DFF),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .alpha(alphaAnim.value)
                    )

                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF7C4DFF).copy(alpha = 0.8f),
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(24.dp)
                            .alpha(alphaAnim.value)
                            .scale(scale)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                // AI "Searching" or "Curating" subtitle
                Text(
                    text = "Curating your cinematic universe...",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.sp,
                    modifier = Modifier.alpha(alphaAnim.value)
                )
            }
        }
        
        // --- 3. Bottom Brand Footer ---
        Text(
            text = "Intelligence by Gemini",
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .alpha(alphaAnim.value)
        )
    }
}
