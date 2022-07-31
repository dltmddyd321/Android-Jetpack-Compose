package com.example.bookingdesign

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

val dummyDestinations by lazy {
    listOf(
        Destination(R.drawable.ic_launcher_background, "Android", 4.8F),
        Destination(R.drawable.ic_launcher_background, "Phone", 4.8F),
        Destination(R.drawable.ic_launcher_background, "Kotlin", 4.8F),
        Destination(R.drawable.ic_launcher_background, "JAVA", 4.8F)
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeComponent()
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
        Column {
            TopSection()
            Spacer(modifier = Modifier.size(20.dp))
            Event()
            Spacer(modifier = Modifier.size(20.dp))
            EventList()
        }
    }
}

@Composable
fun EventList() {
    val textColor = Color(0xFF222222)

    return Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Best Destination",
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp
            )
            Text(
                text = "SEE ALL",
                color = Color(0xFF77797A),
                fontWeight = FontWeight.Medium
            )
        }
        StaggeredVerticalGrid(
            maxColumnWidth = 220.dp,
            modifier = Modifier.padding(4.dp)
        ) {
            dummyDestinations.forEach { destination ->
                DestinationCard(destination)
            }
        }
    }
}

@Composable
fun DestinationCard(destination: Destination) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomStart
    ) {
        Image(
            painter = painterResource(id = destination.destinationImage),
            contentDescription = destination.destinationName,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = destination.destinationName,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = destination.destinationRating.toString(), color = Color.White)
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(text = destination.destinationRating.toString())
        }
    }
}

@Composable
fun Event() {
    val textColor = Color(0xFF222222)

    return Row(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF1F6F7)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_house_24),
                    contentDescription = "Home"
                )
            }
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = "HOME",
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                letterSpacing = 0.2.sp
            )
        }
        Column(
            modifier = Modifier.weight(1F),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(shape = RoundedCornerShape(18.dp))
                    .background(Color(0xFFFFF5F5)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_fly),
                    contentDescription = "Flights"
                )
            }
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = "HOME",
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                letterSpacing = 0.2.sp
            )
        }
        Column(
            modifier = Modifier.weight(1F),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(shape = RoundedCornerShape(18.dp))
                    .background(Color(0xFFFFF7F2)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_fastfood_24),
                    contentDescription = "Foods"
                )
            }
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = "Foods",
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                letterSpacing = 0.2.sp
            )
        }
        Column(
            modifier = Modifier.weight(1F),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(shape = RoundedCornerShape(18.dp))
                    .background(Color(0xFFF4F3FB)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_emoji_events_24),
                    contentDescription = "Events"
                )
            }
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = "EVENTS",
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                letterSpacing = 0.2.sp
            )
        }
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