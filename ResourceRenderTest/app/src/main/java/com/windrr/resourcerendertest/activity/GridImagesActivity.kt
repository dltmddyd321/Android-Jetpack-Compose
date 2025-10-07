package com.windrr.resourcerendertest.activity

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.windrr.resourcerendertest.activity.ui.theme.ResourceRenderTestTheme

class GridImagesActivity : ComponentActivity() {

    private val imageUrls = List(100) { index ->
        "https://picsum.photos/seed/${index + 1}/300/600"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isCoil = intent.getBooleanExtra("isCoil", false)

        enableEdgeToEdge()
        setContent {
            ResourceRenderTestTheme {
                if (isCoil) {
                    CoilPerformanceTestScreen()
                } else {
                    GlidePerformanceTestScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun GlidePerformanceTestScreen() {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(imageUrls) { url ->
                GlideImage(
                    model = url,
                    contentDescription = "Glide Image",
                    modifier = Modifier.size(width = 180.dp, height = 360.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    @Composable
    fun CoilPerformanceTestScreen() {
        val context = LocalContext.current
        val imageLoader = remember {
            ImageLoader.Builder(context)
                .components {
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(imageUrls) { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Coil Image",
                    imageLoader = imageLoader,
                    modifier = Modifier.size(width = 180.dp, height = 360.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}