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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.windrr.couplewidgetapp.ui.theme.CoupleWidgetAppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoupleWidgetAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DDaySettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val savedDateMillis by getStartDateFlow(context).collectAsState(initial = null)

    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        // savedDateMillis가 null일 경우, datePickerState의 초기값을
        // 현재 날짜로 설정합니다.
        initialSelectedDateMillis = savedDateMillis ?: System.currentTimeMillis(),
        initialDisplayMode = DisplayMode.Picker
    )

    // ✅ [수정] 날짜가 외부에서 (DataStore 등) 변경되었을 때,
    // DatePicker의 상태(datePickerState)도 함께 업데이트합니다.
    LaunchedEffect(savedDateMillis) {
        if (savedDateMillis != null) {
            datePickerState.selectedDateMillis = savedDateMillis
        }
    }

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

        // ✅ [제거] 기존 '저장하기' 버튼이 제거되었습니다.
        // Spacer(modifier = Modifier.height(16.dp))
        // Button(onClick = { ... }) { ... }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        ExactAlarmPermissionCheck(modifier = Modifier.padding(horizontal = 16.dp))
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false // 1. 다이얼로그 닫기

                        // ✅ [수정] 2. '확인' 버튼 클릭 시 즉시 저장 및 위젯 업데이트
                        coroutineScope.launch {
                            val selectedDate =
                                datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            saveStartDate(context, selectedDate)
                            DDayGlanceWidget.updateAllWidgets(context)
                        }
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
 * '정확한 알람'(SCHEDULE_EXACT_ALARM) 권한을 확인하고
 * 설정으로 이동하는 버튼을 제공하는 Composable
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

    if (!hasPermission) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "위젯이 자정에 자동으로 갱신되려면 '알람 및 리마인더' 권한이 필요합니다.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also {
                    context.startActivity(it)
                }
            }) {
                Text("권한 설정하러 가기")
            }
        }
    } else {
        Text(
            text = "자정 자동 갱신이 활성화되었습니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary // 긍정적 피드백
        )
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