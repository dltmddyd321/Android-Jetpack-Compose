package com.example.composeunderstand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.composeunderstand.ui.theme.ComposeUnderstandTheme
import java.util.UUID

class ModelActivity : ComponentActivity() {

    private lateinit var viewModel: TestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel = ViewModelProvider(this)[TestViewModel::class.java]

            ComposeUnderstandTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GreetingTest()
                }
            }
        }
    }

    @Composable
    fun GreetingTest() {
        val data by viewModel.text.observeAsState("")
        Column {
            Text(text = data)
            Button(onClick = { viewModel.updateText(UUID.randomUUID().toString()) }) {}
        }
    }
}


