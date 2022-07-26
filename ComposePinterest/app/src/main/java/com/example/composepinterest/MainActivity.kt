package com.example.composepinterest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composepinterest.ui.theme.ComposePinterestTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePinterestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun NonLazyStaggeredVerticalGrid() {
    val randomTexts = getRandomStringList(100)

    StaggeredVerticalGrid(
        modifier = Modifier.padding(4.dp)
    ) {
        (randomTexts).forEach {
            TextCard(text = it)
        }
    }
}

@Composable
fun TextCard(
    modifier: Modifier = Modifier,
    text: String
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(4.dp), backgroundColor = Color.Companion.random()) {
        Text(text = text, modifier = Modifier.padding(16.dp))
    }
}

private fun getRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun getRandomStringList(numItems: Int): List<String> {
    return (1..numItems)
        .map {
            val itemLength = Random.nextInt(1, 150)
            getRandomString(itemLength)
        }
}

fun Color.Companion.random() : Color {
    val red = Random.nextInt(256)
    val green = Random.nextInt(256)
    val blue = Random.nextInt(256)
    return Color(red, green, blue)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposePinterestTheme {
        Greeting("Android")
    }
}