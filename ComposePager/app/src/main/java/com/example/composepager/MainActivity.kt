package com.example.composepager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composepager.MainActivity.Companion.MAX_CNT
import com.example.composepager.ui.theme.ComposePagerTheme
import com.google.accompanist.pager.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        const val MAX_CNT = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        MyPagerView()
                        MyTabPagerView()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MyPagerView() {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Column() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = {
                scope.launch {
                    if (pagerState.currentPage <= 0) return@launch
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            }) {
                Text(text = "이전 페이지")
            }
            TextButton(onClick = {
                scope.launch {
                    if (pagerState.currentPage >= MAX_CNT) return@launch
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }) {
                Text(text = "다음 페이지")
            }
        }
        // Display 10 items
        HorizontalPager(
            count = MAX_CNT,
            state = pagerState
//            contentPadding = PaddingValues(64.dp)
        ) { page ->
            // Our page content
            Card(modifier = Modifier.padding(50.dp),
            backgroundColor = Color.Yellow,
            elevation = 10.dp) {
                Text(
                    text = "Card View!!! $page",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(100.dp)
                )
                Text(
                    text = "Page : $page",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun MyTabPagerView() {
    val tabPagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    val tabs = MyTab.values().toList()

    Column() {
        
        TabRow(selectedTabIndex = tabPagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(tabPagerState, tabPositions)
            )
        }) {
            tabs.forEachIndexed { index, tabItem ->
                Tab(text = { Text(tabItem.title) }, selected = tabPagerState.currentPage == index, onClick = {
                    scope.launch {
                        tabPagerState.animateScrollToPage(index)
                    }
                })
            }
        }
        
        // Display 10 items
        HorizontalPager(
            count = tabs.size,
            state = tabPagerState
//            contentPadding = PaddingValues(64.dp)
        ) { page ->
            // Our page content
            Card(modifier = Modifier.padding(50.dp).fillMaxSize(),
                backgroundColor = Color.Yellow,
                elevation = 10.dp) {
                Text(
                    text = tabs[page].title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Page : $page",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


enum class MyTab(val title: String) {
    HOME("홈"), PROFILE("프로필"), LIST("리스트"), SETTING("설정")
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposePagerTheme {
        Greeting("Android")
    }
}