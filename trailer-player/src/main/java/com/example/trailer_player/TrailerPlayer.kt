package com.example.trailer_player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun TrailerPlayer(
    youtubeVideoId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var isFullScreen by remember { mutableStateOf(false) }
    var fullScreenView by remember { mutableStateOf<View?>(null) }
    var exitFullscreenLambda by remember { mutableStateOf<(() -> Unit)?>(null) }
    
    val youtubePlayerView = remember {
        YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false
            lifecycleOwner.lifecycle.addObserver(this)
            
            val listener = object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(youtubeVideoId, 0f)
                }
            }
            
            // Fixed: Passing context to Builder
            val options = IFramePlayerOptions.Builder(context)
                .controls(1)
                .fullscreen(1)
                .build()
            
            initialize(listener, options)

            addFullscreenListener(object : FullscreenListener {
                override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                    isFullScreen = true
                    fullScreenView = fullscreenView
                    exitFullscreenLambda = exitFullscreen
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }

                override fun onExitFullscreen() {
                    isFullScreen = false
                    fullScreenView = null
                    exitFullscreenLambda = null
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            })
        }
    }

    // Main UI Player
    AndroidView(
        factory = {
            (youtubePlayerView.parent as? ViewGroup)?.removeView(youtubePlayerView)
            youtubePlayerView
        },
        modifier = modifier
    )

    // Full Screen Player
    if (isFullScreen && fullScreenView != null) {
        Dialog(
            onDismissRequest = { 
                exitFullscreenLambda?.invoke()
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                BackHandler {
                    exitFullscreenLambda?.invoke()
                }
                
                AndroidView(
                    factory = {
                        (fullScreenView?.parent as? ViewGroup)?.removeView(fullScreenView)
                        fullScreenView!!
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    DisposableEffect(youtubeVideoId) {
        youtubePlayerView.getYouTubePlayerWhenReady(object : com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(youtubeVideoId, 0f)
            }
        })

        onDispose {
            if (isFullScreen) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
