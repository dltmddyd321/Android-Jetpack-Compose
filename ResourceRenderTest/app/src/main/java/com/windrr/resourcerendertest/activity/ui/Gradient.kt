package com.windrr.resourcerendertest.activity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AllGradientsScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearGradientExample()
        RadialGradientExample()
        SweepGradientExample()
    }
}

@Composable
fun LinearGradientExample() {
    val colorStops = arrayOf(
        0.0f to Color.Yellow,
        0.2f to Color.Red,
        1.0f to Color.Blue
    )
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Brush.horizontalGradient(colorStops = colorStops))
    )
}

@Composable
fun RadialGradientExample() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF2be4dc), Color(0xFF243484))
                )
            )
    )
}

@Composable
fun SweepGradientExample() {
    val density = LocalDensity.current
    val colors = listOf(Color.Red, Color.Blue, Color.Red)
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(
                Brush.sweepGradient(
                    colors = colors,
                    center = Offset(
                        with(density) { 100.dp.toPx() },
                        with(density) { 100.dp.toPx() }
                    )
                )
            )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAllGradientsScreen() {
    AllGradientsScreen()
}