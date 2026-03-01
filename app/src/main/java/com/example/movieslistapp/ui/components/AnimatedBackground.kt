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
import com.example.movieslistapp.ui.theme.AuroraCyan
import com.example.movieslistapp.ui.theme.CosmicBlue
import com.example.movieslistapp.ui.theme.DeepSpace
import com.example.movieslistapp.ui.theme.NebulaPurple
import com.example.movieslistapp.ui.theme.PlasmaPink

@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier
) {
    AIGlassBackground(modifier = modifier)
}

@Composable
fun FuturisticGradientOverlay(
    modifier: Modifier = Modifier
) {
    AIGlassOverlay(modifier = modifier)
}
