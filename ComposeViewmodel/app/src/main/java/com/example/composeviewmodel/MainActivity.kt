package com.example.composeviewmodel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeviewmodel.ui.theme.ComposeViewmodelTheme

class MainActivity : ComponentActivity() {

    //ViewModel을 통해 remember 필요없이 변수 변경 가능
    //ViewModel이 Activity와 라이프 사이클을 동일하게 가짐
//    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //변경된 내용을 기억
//            val data = remember { mutableStateOf("Hello") }

            val viewModel = viewModel<MainViewModel>()
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    viewModel.data.value,
                    fontSize = 30.sp
                )
                Button(onClick = {
                   viewModel.changeValue()
                }) {
                    Text("변경")
                }
            }
        }
    }
}

class MainViewModel : ViewModel() {
    private val _data = mutableStateOf("Hello")
    val data : State<String> = _data

    //값의 변화를 관측
    fun changeValue() {
        _data.value = "World"
    }
}