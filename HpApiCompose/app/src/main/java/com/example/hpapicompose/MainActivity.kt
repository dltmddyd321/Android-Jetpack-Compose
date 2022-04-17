package com.example.hpapicompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hpapicompose.ui.theme.HpApiComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HpApiComposeTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {

    val list = listOf("One", "Two", "Three", "Four", "Five")
    val expanded = remember{ mutableStateOf(false) }
    val currentValue = remember { mutableStateOf(list[0]) }


    Surface(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.clickable { expanded.value = !expanded.value }
                .align(Alignment.Center)) {
                Text(text = currentValue.value)
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)

                DropdownMenu(expanded = expanded.value, onDismissRequest = {
                    expanded.value = false
                }) {
                    list.forEach {
                        DropdownMenuItem(onClick = {
                            currentValue.value = it
                            expanded.value = false
                        }) {
                            Text(text = it)
                        }
                    }
                }
            }
        }
    }
}