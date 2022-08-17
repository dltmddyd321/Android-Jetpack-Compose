package com.example.composenotification

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.composenotification.ui.theme.ComposeNotificationTheme
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val service = CounterNotificationService(applicationContext)
        setContent {
            ComposeNotificationTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Button(onClick = {
                        service.showNotification(Counter.value)
                    }) {
                        Text(text = "Show Notification!!")
                    }
                }
            }
        }
    }
}

@Composable
fun VideoBackground(modifier: Modifier) {
    val mContext = LocalContext.current
    val url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    val mExoPlayer = remember(mContext) {
        ExoPlayer.Builder(mContext).build().apply {
            val mediaItem = MediaItem.Builder().setUri(Uri.parse(url)).build()
            setMediaItem(mediaItem)
            playWhenReady = true
            prepare()
            volume = 0f
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { context ->  
            StyledPlayerView(context).apply {
                player = mExoPlayer
                //컨트롤러 제거
                useController = false
                //화면 채우기
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        })
        //어두운 레이어 얹기
        Box(modifier = Modifier.matchParentSize().alpha(0.5f).background(Color.Black))
    }
}