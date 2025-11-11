package com.windrr.couplewidgetapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.windrr.couplewidgetapp.ui.theme.CoupleWidgetAppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        notificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    showDDayNotification()
                }
            }

        setContent {
            CoupleWidgetAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DDaySettingsScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        checkAndRequestNotificationPermission()
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showDDayNotification()
            }
        } else {
            showDDayNotification()
        }
    }

    private fun showDDayNotification() {
//        val channelId = "dday_channel"
//        val notificationId = 1
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "D-Day 알림",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            manager.createNotificationChannel(channel)
//        }
//        val builder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(android.R.drawable.star_on)
//            .setContentTitle("❤ D+123")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setOngoing(true) // 고정 알림
//            .setOnlyAlertOnce(true)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//        with(NotificationManagerCompat.from(this)) {
//            if (ActivityCompat.checkSelfPermission(
//                    this@MainActivity,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) return
//            notify(notificationId, builder.build())
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "D-Day 설정",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        val formattedDate = if (savedDateMillis != null) {
            formatMillisToDate(savedDateMillis)
        } else {
            "날짜를 선택해 주세요"
        }

        Text(
            text = "현재 설정된 날짜: $formattedDate",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDatePicker = true }) {
            Text("시작 날짜 변경하기")
        }

        Button(onClick = {
            coroutineScope.launch {
                val selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                saveStartDate(context, selectedDate)
                DDayGlanceWidget.updateAllWidgets(context)
            }
        }) {
            Text("저장하기")
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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

@Preview(showBackground = true)
@Composable
fun DDaySettingsScreenPreview() {
    CoupleWidgetAppTheme {
        DDaySettingsScreen()
    }
}