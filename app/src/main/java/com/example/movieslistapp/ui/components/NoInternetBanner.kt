package com.example.movieslistapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movieslistapp.ui.theme.Error
import com.example.movieslistapp.ui.theme.GlassOverlay
import com.example.movieslistapp.utils.NetworkObserver

@Composable
fun NoInternetBanner(
    status: NetworkObserver.Status,
    showOnRefresh: Boolean = false,
    duration: Int = 4000,
    modifier: Modifier = Modifier
) {
    var showBanner by remember { mutableStateOf(false) }
    
    // Show banner when internet is lost or during refresh
    LaunchedEffect(status, showOnRefresh) {
        if (status == NetworkObserver.Status.Lost || 
            showOnRefresh) {
            showBanner = true
            kotlinx.coroutines.delay(duration.toLong())
            showBanner = false
        }
    }
    
    AnimatedVisibility(
        visible = showBanner,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Surface(
            color = Error.copy(alpha = 0.9f),
            modifier = modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Error.copy(alpha = 0.8f),
                                GlassOverlay.copy(alpha = 0.3f)
                            )
                        )
                    )
            ) {
                Text(
                    text = "No internet: Turn on internet for new movies",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun RefreshNoInternetBanner(
    isRefreshing: Boolean,
    networkStatus: NetworkObserver.Status,
    modifier: Modifier = Modifier
) {
    var showBanner by remember { mutableStateOf(false) }
    
    // Show banner only when refreshing AND no internet
    LaunchedEffect(isRefreshing, networkStatus) {
        if (isRefreshing && (networkStatus == NetworkObserver.Status.Lost)) {
            showBanner = true
            kotlinx.coroutines.delay(3000) // 3 seconds for refresh
            showBanner = false
        }
    }
    
    AnimatedVisibility(
        visible = showBanner,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Surface(
            color = Error.copy(alpha = 0.9f),
            modifier = modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Error.copy(alpha = 0.8f),
                                GlassOverlay.copy(alpha = 0.3f)
                            )
                        )
                    )
            ) {
                Text(
                    text = "No internet: Cannot refresh movies",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun HomeScreenNoInternetBanner(
    networkStatus: NetworkObserver.Status,
    isRefreshing: Boolean = false,
    modifier: Modifier = Modifier
) {
    NoInternetBanner(
        status = networkStatus,
        showOnRefresh = isRefreshing && (networkStatus == NetworkObserver.Status.Lost),
        duration = if (isRefreshing) 3000 else 4000,
        modifier = modifier.fillMaxWidth()
    )
}
