package com.example.swipebutton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swipebutton.ui.theme.SwipeButtonTheme
import kotlin.math.roundToInt

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SwipeButtonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(topBar = {
                        TopAppBar(title = {
                            Text(text = "Swipe to Button",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center)
                        })
                    }) {
                        ConfirmationButton()
                    }
                }
            }
        }
    }
}

@Composable
private fun DraggableControl(
    modifier : Modifier,
    progress : Float
) {
    Box(modifier = Modifier
        .padding(4.dp)
        .shadow(
            elevation = 2.dp,
            CircleShape,
            clip = false
        )
        .background(Color.White, CircleShape)
    , contentAlignment = Alignment.Center
    ) {
        val isConfirmed = derivedStateOf { progress >= 0.8f }
        
        Crossfade(targetState = isConfirmed.value) {
            if(it) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Confirm Icon!",
                    tint = Color.Green
                    )
            } else {
                Icon(imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Forward Icon!",
                    tint = Color.Green)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ConfirmationButton(
    modifier: Modifier = Modifier
) {
    val width = 350.dp
    val dragSize = 50.dp
    val swipeState = rememberSwipeableState(initialValue = ConfirmationState.DEFAULT)
    val sizePx = with(LocalDensity.current) {
        (width - dragSize).toPx()
    }
    val anchors = mapOf(0f to ConfirmationState.DEFAULT, sizePx to ConfirmationState.CONFIRMED)
    val progress = derivedStateOf {
        if(swipeState.offset.value == 0f) 0f else swipeState.offset.value / sizePx
    }
    
    Box(modifier = modifier
        .width(width)
        .swipeable(
            state = swipeState,
            anchors = anchors,
            thresholds = { _, _ -> FractionalThreshold(0.5f) },
            orientation = Orientation.Horizontal
        )
        .background(Color.Green, RoundedCornerShape(dragSize))
    ) {
        Column(
            Modifier
                .align(Alignment.Center)
                .alpha(1f - progress.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your order 1000 rub",
                color = Color.White,
                fontSize = 18.sp
            )
            Text(
                text = "Swipe to confirm",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        DraggableControl(
            modifier = Modifier
                .offset {
                    IntOffset(swipeState.offset.value.roundToInt(), 0)
                }
                .size(dragSize),
            progress = progress.value)
    }
}