package com.windrr.resourcerendertest.activity

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.windrr.resourcerendertest.ui.theme.ResourceRenderTestTheme

class MainActivity : ComponentActivity() {

    private val resourceUrl =
        "https://cdn-learn.adafruit.com/assets/assets/000/120/180/medium800/circuitpython_sample-nyan-pyportal.gif"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResourceRenderTestTheme {
                Scaffold { innerPadding ->
                    val scrollState = rememberScrollState()
                    val isCoilChecked = remember { mutableStateOf(true) }
                    val context = LocalContext.current
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Glide")
                            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                            Switch(
                                checked = isCoilChecked.value,
                                onCheckedChange = { isCoilChecked.value = it })
                            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                            Text("Coil")
                        }

                        Spacer(modifier = Modifier.padding(16.dp))

                        Button(onClick = {
                            val intent = Intent(context, GridImagesActivity::class.java).apply {
                                putExtra("isCoil", isCoilChecked.value)
                            }
                            context.startActivity(intent)
                        }) {
                            Text("성능 테스트 화면으로 이동")
                        }

                        Spacer(modifier = Modifier.padding(16.dp))

                        GlideWidgetImage()

                        Spacer(modifier = Modifier.padding(16.dp))

                        CoilWidgetImage()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun GlideWidgetImage() {
        GlideImage(
            model = resourceUrl,
            contentDescription = "Glide",
            modifier = Modifier.size(width = 180.dp, height = 360.dp),
            contentScale = ContentScale.Crop
        )
    }

    @Composable
    fun CoilWidgetImage() {
        val context = LocalContext.current

        val imageLoader = ImageLoader.Builder(context).components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(coil.decode.GifDecoder.Factory())
                }
            }.build()

        AsyncImage(
            model = resourceUrl,
            contentDescription = "Coil",
            imageLoader = imageLoader,
            modifier = Modifier.size(width = 180.dp, height = 360.dp),
            contentScale = ContentScale.Crop
        )
    }
}

