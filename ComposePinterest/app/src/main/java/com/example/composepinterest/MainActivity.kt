package com.example.composepinterest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composepinterest.ui.theme.ComposePinterestTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    companion object {
        const val MIN_CNT = 2
        const val MAX_CNT = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePinterestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val columnCount = remember {
                        mutableStateOf(2)
                    }
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(modifier = Modifier.weight(1f),
                                onClick = {
                                if (columnCount.value < MIN_CNT) { return@Button }
                                columnCount.value = columnCount.value - 1
                            }) {
                                Text(text = "-", fontSize = 20.sp)
                            }
                            Text(modifier = Modifier.weight(1f),
                                text = "${columnCount.value}",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center)
                            Button(modifier = Modifier.weight(1f),
                                onClick = {
                                if (columnCount.value > MAX_CNT - 1) { return@Button }
                                columnCount.value = columnCount.value + 1
                            }) {
                                Text(text = "+", fontSize = 20.sp)
                            }
                        }
                        NonLazyStaggeredVerticalGrid(columnCount)

                        //메모리에서 유실되었다가 스크롤 시 다시 메모리에 데이터 올라간다.
                        MyLazyStaggeredVerticalGrid(columnCount)
                    }
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
fun NonLazyStaggeredVerticalGrid(
    columnCount: MutableState<Int>
) {
    val randomTexts = getRandomStringList(100)

    LazyColumn{
        item {
            StaggeredVerticalGrid(
                modifier = Modifier.padding(4.dp),
                columnCount = columnCount.value
            ) {
                (randomTexts).forEach {
                    TextCard(text = it)
                }
            }
        }
    }
}

@Composable
fun MyLazyStaggeredVerticalGrid(
    columnCount: MutableState<Int>
) {
    val randomTexts = getRandomStringList(100)

    LazyStaggeredGrid(columnCount = columnCount.value) {
        (randomTexts).forEach { text ->
            item {
                TextCard(text = text)
            }
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