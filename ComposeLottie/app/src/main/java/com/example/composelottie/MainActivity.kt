package com.example.composelottie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.composelottie.ui.theme.ComposeLottieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLottieTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Loader()
                }
            }
        }
    }
}

@Composable
fun Loader() {
    //Lottie JSON 파일 처리
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("soccerlottie.json"))

    var isAnimationPlaying by remember { mutableStateOf(true) }

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        clipSpec = LottieClipSpec.Progress(0f, 0.6f),
        isPlaying = isAnimationPlaying
    )
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier
                .size(400.dp)
                .background(Color.Cyan)
        )
        Button(onClick = {
            isAnimationPlaying = !isAnimationPlaying
        }) {
            Text(text = "재생 / 일시정지")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeLottieTheme {
        Greeting("Android")
    }
}