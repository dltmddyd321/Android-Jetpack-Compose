package com.windrr.couplewidgetapp

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.windrr.couplewidgetapp.ui.theme.CoupleWidgetAppTheme
import com.windrr.couplewidgetapp.ui.theme.CreamWhite
import com.windrr.couplewidgetapp.ui.theme.LovelyPink
import com.windrr.couplewidgetapp.ui.theme.SoftGray
import com.windrr.couplewidgetapp.ui.theme.SoftPeach
import com.windrr.couplewidgetapp.ui.theme.WarmText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoupleWidgetAppTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(CreamWhite, SoftPeach.copy(alpha = 0.3f))
                            )
                        )
                ) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        DDaySettingsScreen(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DDaySettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val savedDateMillis by getStartDateFlow(context).collectAsState(initial = null)
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = savedDateMillis ?: System.currentTimeMillis(),
        initialDisplayMode = DisplayMode.Picker
    )

    LaunchedEffect(savedDateMillis) {
        if (savedDateMillis != null) {
            datePickerState.selectedDateMillis = savedDateMillis
        }
    }

    Box(
        modifier = modifier
    ) {
        IconButton(
            onClick = {
                val intent = Intent(context, AnniversarySettingActivity::class.java)
                intent.putExtra("BASE_DATE", savedDateMillis ?: System.currentTimeMillis())
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "설정",
                tint = SoftGray,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = "Heart",
                tint = LovelyPink,
                modifier = Modifier
                    .size(64.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape)
                    .background(Color.White, CircleShape)
                    .padding(12.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "우리가 사랑한 지",
                style = MaterialTheme.typography.titleMedium,
                color = SoftGray
            )

            val dDayText = remember(savedDateMillis) {
                if (savedDateMillis != null) {
                    val days = calculateDDay(savedDateMillis!!)
                    if (days > 0) "+ $days 일" else "D${days - 1}"
                } else {
                    "The Beginning"
                }
            }

            Text(
                text = buildAnnotatedString {
                    if (savedDateMillis != null) {
                        withStyle(style = SpanStyle(color = LovelyPink)) {
                            append("❤ ")
                        }
                    }
                    append(dDayText)
                },
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = WarmText
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val dateText = if (savedDateMillis != null) {
                        formatMillisToDate(savedDateMillis)
                    } else {
                        "날짜를 선택해주세요"
                    }

                    Text(
                        text = "Start Date",
                        style = MaterialTheme.typography.labelLarge,
                        color = LovelyPink
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = WarmText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showDatePicker = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LovelyPink,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "날짜 변경하기", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ExactAlarmPermissionCheck(modifier = Modifier.fillMaxWidth())
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            colors = DatePickerDefaults.colors(
                containerColor = CreamWhite,
                selectedDayContainerColor = LovelyPink,
                todayDateBorderColor = LovelyPink,
                todayContentColor = LovelyPink
            ),
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        coroutineScope.launch {
                            val selectedDate =
                                datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            saveStartDate(context, selectedDate)
                            DDayGlanceWidget.updateAllWidgets(context)
                        }
                    }
                ) {
                    Text("확인", color = LovelyPink, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("취소", color = SoftGray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * '정확한 알람' 권한 확인 UI - 부드러운 알림 박스 스타일
 */
@Composable
fun ExactAlarmPermissionCheck(modifier: Modifier = Modifier) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by rememberSaveable { mutableStateOf(alarmManager.canScheduleExactAlarms()) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = alarmManager.canScheduleExactAlarms()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (hasPermission) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
            .padding(16.dp)
    ) {
        if (!hasPermission) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = null,
                        tint = Color(0xFFFF9800)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "자동 갱신을 위해 필요해요",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFFEF6C00),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "자정에 D-Day가 바뀌려면\n'알람 및 리마인더' 권한을 허용해주세요.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = WarmText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also {
                            context.startActivity(it)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("권한 설정하러 가기", fontSize = 12.sp)
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "매일 자정, 추억이 갱신됩니다 ✨",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Milliseconds (Long) 값을 "yyyy년 MM월 dd일" 형태의 문자열로 변환합니다.
 */
private fun formatMillisToDate(millis: Long?): String {
    if (millis == null) return "N/A"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            val localDate = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            return localDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
        } catch (e: Exception) {
            return "날짜 변환 오류"
        }
    } else {
        try {
            val date = Date(millis)
            val formatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
            return formatter.format(date)
        } catch (e: Exception) {
            return "날짜 변환 오류"
        }
    }
}

private fun calculateDDay(startDateMillis: Long): Long {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val start = Calendar.getInstance().apply {
        timeInMillis = startDateMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val diffMillis = today.timeInMillis - start.timeInMillis
    return (diffMillis / (24 * 60 * 60 * 1000)) + 1
}