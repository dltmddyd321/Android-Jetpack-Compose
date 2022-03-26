package com.example.tablayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.ExperimentalPagingApi
import com.example.tablayout.ui.theme.JetPackColorOne
import com.example.tablayout.ui.theme.TabLayoutTheme
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import java.nio.file.WatchEvent

@ExperimentalPagerApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TabLayoutTheme {
                TabScreen()
            }
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabScreen() {
    val pagerState = rememberPagerState(pageCount = 3)
    Column(modifier = Modifier.background(Color.White)) {
        Tabs(pagerState = pagerState)
        TabsContent(pagerState = pagerState)
    }
}

@ExperimentalPagerApi
@Composable
fun Tabs(pagerState : PagerState) {
    val list = listOf("BAR 1", "BAR 2", "BAR 3")
    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = JetPackColorOne,
        contentColor = Color.White,
        divider = {
            TabRowDefaults.Divider(
                thickness = 2.dp,
                color = Color.Green
            )
        },
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Color.White
            )
    }) {
        list.forEachIndexed { index, _ ->  
            Tab(
                text = {
                    Text(
                        text = list[index],
                        color = if(pagerState.currentPage == index) Color.White else Color.LightGray)
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabsContent(pagerState : PagerState) {
    HorizontalPager(state = pagerState) { page ->
        when(page) {
            0 -> TabScreenOne(data = "First Tab!")
            1 -> TabScreenOne(data = "Second Tab!")
            2 -> TabScreenOne(data = "Third Tab!")
        }
    }
}