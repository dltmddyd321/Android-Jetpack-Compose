package com.sycompany.composewithxml

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sycompany.composewithxml.ui.theme.ComposeWithXmlTheme
import com.sycompany.composewithxml.ui.theme.Purple200

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContainerApp {
                MyScreenContent(listOf("김남일", "박지성", "이영표"))
            }
//            ComposeWithXmlTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    //전체 화면 배경색이 모두 보라색으로 변경
//                    modifier = Modifier.fillMaxSize(),
//                    color = Purple200
//                ) {
//                    Greeting("Android")
//                }
//            }
        }
    }
}

@Composable
fun MyScreenContent(names: List<String>) {
    val cntState = remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxHeight()) {
        //리스트 생성 (수평 방향 아이템 배치)
        Column(modifier = Modifier.weight(1f)) {
            names.forEach {
                Greeting(name = it)
                Divider(color = Color.Black)
            }

            Counter(
                cnt = cntState.value,
                updateCnt = {
                    cntState.value = it
                }
            )
        }
    }
}

@Composable
fun NameList(names: List<String>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(names) { name ->
            Greeting(name = name)
            Divider(color = Color.Black)
        }
    }
}

@Composable
fun Greeting(name: String) {
    //modifier : UI 컴포넌트의 위치 지정 가능
    Text(text = "Legend $name!", modifier = Modifier.padding(24.dp))
}

@Composable
fun ContainerApp(content: @Composable () -> Unit) {
    ComposeWithXmlTheme {
        androidx.compose.material.Surface(color = Purple200) {
            content()
        }
    }
}

@Composable
fun Counter(cnt: Int, updateCnt: (Int) -> Unit) {
    
    Button( onClick = { updateCnt(cnt + 1) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (cnt >= 10) Color.Red else Color.Cyan
            )) {
        if (cnt < 10) {
            Text(text = "${cnt}번 클릭!!")
        } else if (cnt >= 10) {
            Text(text = "클릭 최대치 도달!!")
        }
    }
}

@Composable
fun MyApp() {
    ComposeWithXmlTheme {
        androidx.compose.material.Surface(color = Color.Green) {
            Greeting(name = "Compose")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeWithXmlTheme {
        Greeting("Android")
    }
}