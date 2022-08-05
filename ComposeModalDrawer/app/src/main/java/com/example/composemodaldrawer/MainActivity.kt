package com.example.composemodaldrawer

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composemodaldrawer.ui.theme.ComposeModalDrawerTheme
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeModalDrawerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
//                    ModalDrawerSample()
                    ShowDatePicker(this)
                }
            }
        }
    }
}

@Composable
fun ModalDrawerSample() {

    //닫힘 여부 상태
    val drawerState = rememberDrawerState(DrawerValue.Open)
    val scope = rememberCoroutineScope()
    val shouldGestureEnabled = drawerState.currentValue == DrawerValue.Open

    ModalDrawer(
        drawerState = drawerState,
        gesturesEnabled = shouldGestureEnabled,
        drawerContent = {
            Column {
                Text("Text in Drawer")
                Button(onClick = {
                    scope.launch {
                        drawerState.close()
                    }
                }) {
                    Text("Close Drawer")
                }
            }
        },
        content = {
            Column {
                Text("Text in Bodycontext")
                Button(onClick = {

                    scope.launch {
                        drawerState.open()
                    }

                }) {
                    Text("Click to open")
                }
            }
        }
    )
}

@SuppressLint("NewApi")
@Composable
fun ShowDatePicker(context: Context) {

    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val date = remember {
        mutableStateOf("")
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, datOfMonth: Int ->
            date.value = "$datOfMonth/$month/$year"

        }, year, month, day
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(text = "Selected Date: ${date.value}")
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = {
            datePickerDialog.show()
        }) {
            Text(text = "Open Date Picker!")
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
    ComposeModalDrawerTheme {
        Greeting("Android")
    }
}