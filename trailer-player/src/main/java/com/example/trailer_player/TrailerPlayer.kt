package com.example.trailer_player

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun TrailerPlayer(
    youtubeVideoId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val youtubePlayerView = remember {
        YouTubePlayerView(context).apply {
            lifecycleOwner.lifecycle.addObserver(this)
            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(youtubeVideoId, 0f)
                }
            })
        }
    }

    AndroidView(
        factory = { youtubePlayerView },
        modifier = modifier.fillMaxSize()
    )

    DisposableEffect(youtubeVideoId) {
        // When videoId changes, we might want to cue the new video if the player is ready
        // But the listener above handles the initial load. 
        // For dynamic changes while the view is active:
        youtubePlayerView.getYouTubePlayerWhenReady(object : com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(youtubeVideoId, 0f)
            }
        })

        onDispose {
            // Lifecycle observer handles the release/pause usually, 
            // but we can be explicit if needed.
        }
    }
}
