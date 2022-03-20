package com.example.jepackmvvm

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.view.RoundedCorner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.jepackmvvm.ui.theme.JepackMvvmTheme

class MainActivity : ComponentActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(FakeMyRepositoryImpl()))
            .get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JepackMvvmTheme {
                val myModels = viewModel.myModels.observeAsState().value ?: emptyList()
                val clickedModel = viewModel.itemOnClickEvent.observeAsState().value

                MyModelList(models = myModels, onItemClick = viewModel::onItemClick)

                if(clickedModel != null) {
                    ComposableToast(message = clickedModel.title)
                }
            }
        }
        viewModel.loadMyModels()
    }
}

@Composable
fun MyModelList(models : List<TestModel>, onItemClick: (Int) -> Unit = {}) {
    val itemList = (0..models.size).toList()
    val itemIndexedList = models

    LazyColumn(modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally) {
        items(itemList) {
            Text(text = "Item is $it")
        }

        item {
            Text(text = "Single Item!")
        }

        itemsIndexed(itemIndexedList) { index, item ->  
            MyModelListItem(model = item, onClick = {
                onItemClick.invoke(index)
            })
        }
    }
}

@Composable
fun MyModelListItem(model : TestModel, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Blue),
        backgroundColor = Color.White,
        contentColor = Color.Blue,
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Text(
            text = model.title,
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.secondary
            )
    }
}

@Composable
fun ComposableToast(message: String) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
}