@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)

package com.example.composeunderstand

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeunderstand.MainActivity.Companion.TAG
import com.example.composeunderstand.ui.theme.ActionBtnBgColor
import com.example.composeunderstand.ui.theme.ComposeUnderstandTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeUnderstandTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Calculator()
                }
            }
        }
    }
}

@Composable
fun Calculator() {

    val numbers = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

    val actions = CalAction.values()

    val buttons = listOf(
        CalAction.Divide, 7, 8, 9, CalAction.Multiply,
        4, 5, 6, CalAction.Minus,
        1, 2, 3, CalAction.Plus, 0
    )

    LazyVerticalGrid(
//        columns = GridCells.Adaptive(120.dp), // 120dp에 맞추어 채우겠다.
        columns = GridCells.Fixed(4), //4칸으로 나누어 맞추겠다.
        horizontalArrangement = Arrangement.spacedBy(8.dp), //가로 간격 조절
        verticalArrangement = Arrangement.spacedBy(8.dp), //세로 간격 조절
        content = {//넣을 아이템에 대해 명시

            item(span = {
                GridItemSpan(2)
            }) {
                ActionButton(action = CalAction.AllClear)
            }

            item(span = {
                GridItemSpan(1)
            }) {
                ActionButton(action = CalAction.Delete)
            }

            items(buttons) { btn ->
                when (btn) {
                    is CalAction -> ActionButton(btn)
                    is Int -> NumberButton(num = btn)
                }
            }

            item(span = {
                GridItemSpan(maxCurrentLineSpan) //여유 공간만큼 다 채운다.
            }) {
                ActionButton(action = CalAction.Calculate)
            }
        })
}

@Composable
fun ActionButton(action: CalAction) {
    androidx.compose.material3.Card(
        onClick = {
            Log.d(TAG, "Card가 클릭!!")
        },
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = ActionBtnBgColor
        )
    ) {
        Text(
            text = action.symbol,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NumberButton(num: Int) {
    androidx.compose.material3.Card(
        onClick = {
            Log.d(TAG, "Card가 클릭!!")
        },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Text(
            text = num.toString(),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SomeTextLabel(name: String, context: Context) {
    Text(
        text = "Hello $name!",
        modifier = Modifier.clickable {
            Toast.makeText(context, "텍수투 클릭!!!", Toast.LENGTH_SHORT).show()
        }
    )
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeUnderstandTheme {
        Greeting("Android")
    }
}

//item(span = {
//    GridItemSpan(2) // 지정한 span 만큼 공간을 차지한다.
//}) {
//    androidx.compose.material3.Card(
//        onClick = {
//            Log.d(TAG, "Card가 클릭!!")
//        },
//        elevation = CardDefaults.cardElevation(8.dp),
//        colors = CardDefaults.cardColors(Color.Magenta)
//    ) {
//        Text(
//            text = "Dummy",
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxSize(),
//            textAlign = TextAlign.Center,
//            fontSize = 30.sp,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}
//
//item(span = {
//    GridItemSpan(1) // 지정한 span 만큼 공간을 차지한다.
//}) {
//    androidx.compose.material3.Card(
//        onClick = {
//            Log.d(TAG, "Card가 클릭!!")
//        },
//        elevation = CardDefaults.cardElevation(8.dp),
//        colors = CardDefaults.cardColors(Color.Magenta)
//    ) {
//        Text(
//            text = "Dummy",
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxSize(),
//            textAlign = TextAlign.Center,
//            fontSize = 30.sp,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}
//
//item(span = {
//    GridItemSpan(1) // 지정한 span 만큼 공간을 차지한다.
//}) {
//    androidx.compose.material3.Card(
//        onClick = {
//            Log.d(TAG, "Card가 클릭!!")
//        },
//        elevation = CardDefaults.cardElevation(8.dp),
//        colors = CardDefaults.cardColors(Color.Magenta)
//    ) {
//        Text(
//            text = "Dummy",
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxSize(),
//            textAlign = TextAlign.Center,
//            fontSize = 30.sp,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}