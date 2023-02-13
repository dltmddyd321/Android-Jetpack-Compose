package com.example.checkinternet

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.example.checkinternet.ui.theme.CheckInternetTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : ComponentActivity() {

    private lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityObserver = NetworkConnectivityObserver(applicationContext)

        connectivityObserver.observe().onEach {
            println("Status is $it")
        }.launchIn(lifecycleScope)

        setContent {
            CheckInternetTheme {
                val status by connectivityObserver.observe().collectAsState(
                    initial = ConnectivityObserver.Status.Unavailable
                )
                Row {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Network status: $status")
                    }
                    EmojiView(emoji = "🥷")
                }
            }
        }
    }

    @Composable
    fun EmojiView(
        emoji: String,
    ) {
        AndroidView(
            factory = { context ->
                AppCompatTextView(context).apply {
                    text = emoji ?: "😟"
                    textSize = 48.0F
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }
            },
            update = {
                it.apply {
                    text = emoji ?: "😟"
                }
            },
        )
    }
}