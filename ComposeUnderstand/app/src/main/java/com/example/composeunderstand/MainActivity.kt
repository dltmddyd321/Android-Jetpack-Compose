@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)

package com.example.composeunderstand

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeunderstand.ui.theme.ActionBtnBgColor
import com.example.composeunderstand.ui.theme.ComposeUnderstandTheme
import com.example.composeunderstand.ui.theme.Purple200
import com.example.composeunderstand.ui.theme.Purple500

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

    //첫 번째 입력
    var firstInput by remember { mutableStateOf("") }

    //두 번째 입력
    var secondInput by remember { mutableStateOf("") }

    //현재 활성화된 Action
    val selectedAction: MutableState<CalAction?> = remember {
        mutableStateOf(null)
    }

    //현재 선택된 Symbol
    val selectedSymbol: String = selectedAction.value?.symbol ?: ""

    LazyVerticalGrid(
//        columns = GridCells.Adaptive(120.dp), // 120dp에 맞추어 채우겠다.
        columns = GridCells.Fixed(4), //4칸으로 나누어 맞추겠다.
        horizontalArrangement = Arrangement.spacedBy(8.dp), //가로 간격 조절
        verticalArrangement = Arrangement.spacedBy(8.dp), //세로 간격 조절
        content = {//넣을 아이템에 대해 명시

            item(span = {
                GridItemSpan(maxLineSpan)
            }) {
                NumberText(
                    firstInput,
                    secondInput,
                    selectedSymbol,
                    modifier = Modifier.fillMaxSize()
                )
            }

            item(span = {
                GridItemSpan(2)
            }) {
                ActionButton(action = CalAction.AllClear,
                    onClicked = {
                        firstInput = ""
                        secondInput = ""
                        selectedAction.value = null
                    })
            }

            item(span = {
                GridItemSpan(1)
            }) {
                ActionButton(action = CalAction.Delete)
            }

            items(buttons) { btn ->
                when (btn) {
                    is CalAction -> ActionButton(btn, selectedAction.value, onClicked = {
                        selectedAction.value = btn
                    })
                    is Int -> NumberButton(num = btn) {
                        firstInput += btn
                    }
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
fun NumberText(
    firstInput: String,
    secondInput: String,
    selectedSymbol: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        Text(
            text = firstInput,
            modifier = Modifier
                .background(Color.Yellow),
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 50.sp,
            maxLines = 1,
            color = Color.Black
        )
        Text(
            text = selectedSymbol,
            modifier = Modifier
                .background(Color.Yellow),
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 50.sp,
            maxLines = 1,
            color = Purple200
        )
        Text(
            text = secondInput,
            modifier = Modifier
                .background(Color.Yellow),
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 50.sp,
            maxLines = 1,
            color = Color.Black
        )
    }
}

@Composable
fun ActionButton(
    action: CalAction,
    selectedAction: CalAction? = null,
    onClicked: (() -> Unit)? = null
) {
    androidx.compose.material3.Card(
        onClick = {
            onClicked?.invoke()
        },
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedAction == action) Purple500 else ActionBtnBgColor,
            contentColor = if (selectedAction == action) Color.White else Color.Black
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
fun NumberButton(num: Int, onClicked: (() -> Unit)? = null) {
    androidx.compose.material3.Card(
        onClick = {
            onClicked?.invoke()
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