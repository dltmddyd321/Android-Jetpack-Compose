package com.example.bookingdesign

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookingdesign.ui.theme.BookingDesignTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}

@Composable
fun HomeComponent() {
    return Scaffold(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopSection()
    }
}

@Composable
fun TopSection() {
    return Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hi Compose",
                color = Color(0xFF222222),
                style = MaterialTheme.typography.h6,
                fontSize = 37.sp,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF77797A)
            )
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(
                text = buildAnnotatedString {
                    append("WHERE TO ")
                    withStyle(style = SpanStyle(Color.Red)) {
                        append("54 KING PORTS")
                    }
                },
                color = Color(0xFF222222),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                contentDescription = "Drop Down",
                modifier = Modifier
                    .padding(start = 4.dp, bottom = 2.dp)
                    .size(12.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewGreeting() {
    HomeComponent()
}