package com.windrr.couplewidgetapp.activity

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.windrr.couplewidgetapp.R
import com.windrr.couplewidgetapp.anniversary.AnniversaryItem
import com.windrr.couplewidgetapp.anniversary.AnniversaryNotificationReceiver
import com.windrr.couplewidgetapp.anniversary.AppDatabase
import com.windrr.couplewidgetapp.dday.dataStore
import com.windrr.couplewidgetapp.dday.getStartDateFlow
import com.windrr.couplewidgetapp.dday.getStartTitle
import com.windrr.couplewidgetapp.dday.saveStartDate
import com.windrr.couplewidgetapp.dday.saveStartTitle
import com.windrr.couplewidgetapp.ui.AdMobBanner
import com.windrr.couplewidgetapp.ui.theme.CoupleWidgetAppTheme
import com.windrr.couplewidgetapp.ui.theme.MinimalAccent
import com.windrr.couplewidgetapp.ui.theme.MinimalAccentLight
import com.windrr.couplewidgetapp.ui.theme.MinimalBgColor
import com.windrr.couplewidgetapp.ui.theme.MinimalCardColor
import com.windrr.couplewidgetapp.ui.theme.MinimalTextMain
import com.windrr.couplewidgetapp.ui.theme.MinimalTextSub
import com.windrr.couplewidgetapp.widget.DDayGlanceWidget
import com.windrr.couplewidgetapp.widget.DDayGlanceWidgetReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


private fun calculateDDay(startDateMillis: Long): Long {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(
        Calendar.SECOND,
        0
    ); set(Calendar.MILLISECOND, 0)
    }
    val start = Calendar.getInstance().apply {
        timeInMillis = startDateMillis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(
        Calendar.SECOND,
        0
    ); set(Calendar.MILLISECOND, 0)
    }
    val diffMillis = today.timeInMillis - start.timeInMillis
    return (diffMillis / (24 * 60 * 60 * 1000)) + 1
}

// 기념일 자동 등록을 위한 날짜 계산 함수
private fun calculateTargetDate(baseMillis: Long, days: Int): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = baseMillis }
    calendar.add(Calendar.DAY_OF_YEAR, days - 1)
    return calendar.timeInMillis
}

// 로케일에 따른 날짜 포맷
private fun formatMillisToDate(context: Context, millis: Long?): String {
    if (millis == null) return context.getString(R.string.date_placeholder)

    val locale = Locale.getDefault()
    val pattern = if (locale.language == "ko") {
        "yyyy년 MM월 dd일"
    } else {
        "MMM dd, yyyy"
    }

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                .format(DateTimeFormatter.ofPattern(pattern, locale))
        } else {
            SimpleDateFormat(pattern, locale).format(Date(millis))
        }
    } catch (e: Exception) {
        context.getString(R.string.error_date_format)
    }
}


class MainActivity : ComponentActivity() {

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            DDayGlanceWidget.updateAllWidgets(applicationContext)
        }
        DDayGlanceWidgetReceiver.scheduleNextMidnightUpdate(applicationContext)
        AnniversaryNotificationReceiver.scheduleNextMorningNotification(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            CoupleWidgetAppTheme {
                val context = LocalContext.current
                val bgUriString by context.dataStore.data
                    .map { preferences -> preferences[BACKGROUND_IMAGE_URI_KEY] ?: "" }
                    .collectAsState(initial = "")

                var bgBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

                // 배경 이미지 로드 및 회전 보정
                LaunchedEffect(bgUriString) {
                    if (bgUriString.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            try {
                                val uri = bgUriString.toUri()
                                var rotation = 0f
                                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        val exif = ExifInterface(inputStream)
                                        val orientation = exif.getAttributeInt(
                                            ExifInterface.TAG_ORIENTATION,
                                            ExifInterface.ORIENTATION_NORMAL
                                        )
                                        rotation = when (orientation) {
                                            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                                            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                                            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                                            else -> 0f
                                        }
                                    }
                                }

                                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                                    if (originalBitmap != null) {
                                        bgBitmap = if (rotation != 0f) {
                                            val matrix = Matrix().apply { postRotate(rotation) }
                                            val rotatedBitmap =
                                                android.graphics.Bitmap.createBitmap(
                                                    originalBitmap, 0, 0,
                                                    originalBitmap.width, originalBitmap.height,
                                                    matrix, true
                                                )
                                            if (rotatedBitmap != originalBitmap) {
                                                originalBitmap.recycle()
                                            }
                                            rotatedBitmap.asImageBitmap()
                                        } else {
                                            originalBitmap.asImageBitmap()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                bgBitmap = null
                            }
                        }
                    } else {
                        bgBitmap = null
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MinimalBgColor) // 새로운 배경색 적용
                ) {
                    // 배경 이미지가 있을 때
                    if (bgBitmap != null) {
                        Image(
                            bitmap = bgBitmap!!,
                            contentDescription = stringResource(R.string.desc_selected_bg),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // 가독성을 위한 오버레이
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                        )
                    }

                    Scaffold(
                        containerColor = Color.Transparent,
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            AdMobBanner(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .navigationBarsPadding()
                            )
                        }
                    ) { innerPadding ->
                        // 배경 유무에 따라 텍스트 색상 결정
                        val contentColor = if (bgBitmap != null) Color.White else MinimalTextMain
                        val subContentColor =
                            if (bgBitmap != null) Color.White.copy(alpha = 0.8f) else MinimalTextSub

                        DDaySettingsScreen(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            contentColor = contentColor,
                            subContentColor = subContentColor,
                            hasBackgroundImage = bgBitmap != null
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DDaySettingsScreen(
    modifier: Modifier = Modifier,
    contentColor: Color,
    subContentColor: Color,
    hasBackgroundImage: Boolean
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val savedDateMillis by getStartDateFlow(context).collectAsState(initial = null)
    var showDatePicker by remember { mutableStateOf(false) }
    val defaultTitle = stringResource(R.string.default_main_title)
    val storedTitle by getStartTitle(context).collectAsState(initial = defaultTitle)
    var showTitleDialog by remember { mutableStateOf(false) }
    var showGuideDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = savedDateMillis ?: System.currentTimeMillis(),
        initialDisplayMode = DisplayMode.Picker
    )

    val widgetSettingLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val isLanguageChanged = result.data?.getBooleanExtra("language_changed", false) ?: false
            if (isLanguageChanged) {
                activity?.recreate()
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isNotificationGranted =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    } else {
                        true
                    }
                val isExactAlarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager =
                        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.canScheduleExactAlarms()
                } else {
                    true
                }
                showPermissionDialog = !isNotificationGranted || !isExactAlarmGranted
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(savedDateMillis) {
        if (savedDateMillis != null) {
            datePickerState.selectedDateMillis = savedDateMillis
        }
    }

    Box(modifier = modifier) {
        // [수정] 상단 바 - 스크린샷과 동일한 배치 (좌측 가이드 텍스트 포함, 우측 설정)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showGuideDialog = true }
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info, // 스크린샷의 원형 느낌 아이콘
                    contentDescription = null,
                    tint = subContentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "위젯 설정 방법", // 텍스트 추가
                    style = MaterialTheme.typography.bodyMedium,
                    color = subContentColor
                )
            }

            IconButton(
                onClick = {
                    val intent = Intent(context, WidgetSettingActivity::class.java)
                    widgetSettingLauncher.launch(intent)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = stringResource(R.string.settings_title),
                    tint = subContentColor
                )
            }
        }

        // [수정] 중앙 컨텐츠 영역
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. 하트 아이콘 (스크린샷처럼 아웃라인 형태)
            Icon(
                imageVector = Icons.Rounded.FavoriteBorder,
                contentDescription = "Heart",
                tint = subContentColor.copy(alpha = 0.7f),
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 타이틀 (우리가 사랑한 지 ✏️)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showTitleDialog = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = storedTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = subContentColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = null,
                    tint = subContentColor,
                    modifier = Modifier.size(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. 메인 D-Day 텍스트 (엄청 크고 깔끔하게)
            val dDayCount = remember(savedDateMillis) {
                savedDateMillis?.let { calculateDDay(it) }
            }
            val dDayString = if (dDayCount != null) {
                if (dDayCount > 0) stringResource(R.string.d_day_plus_format, dDayCount)
                else stringResource(R.string.d_day_minus_format, dDayCount - 1)
            } else {
                stringResource(R.string.the_beginning)
            }

            Text(
                text = dDayString,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-1).sp
                ),
                color = contentColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4. 날짜 설정 카드 (스크린샷 스타일 완벽 반영)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (hasBackgroundImage) Color.White.copy(alpha = 0.85f) else MinimalCardColor
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (hasBackgroundImage) 0.dp else 4.dp),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val dateText = formatMillisToDate(context, savedDateMillis)

                    Text(
                        text = "START DATE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MinimalTextSub
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MinimalTextMain
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // "날짜 변경하기" 버튼 (테두리 있는 스타일)
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MinimalAccent
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MinimalAccent.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "날짜 변경하기", fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 5. 하단 꾸밈 텍스트 (선과 함께)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = subContentColor.copy(alpha = 0.3f)
                )
                Text(
                    text = " 매일 자정, 추억이 갱신됩니다 ✨ ",
                    style = MaterialTheme.typography.bodySmall,
                    color = subContentColor
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = subContentColor.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 권한 체크 알림 (필요 시에만 표시)
            if (showPermissionDialog) {
                ExactAlarmPermissionCheck(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // [수정] 우측 하단 기념일 원형 FAB (스크린샷 스타일)
        FloatingActionButton(
            onClick = {
                val intent = Intent(context, AnniversarySettingActivity::class.java)
                intent.putExtra("BASE_DATE", savedDateMillis ?: System.currentTimeMillis())
                context.startActivity(intent)
            },
            containerColor = MinimalAccent,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(68.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_calendar_check_24),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "기념일",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 로딩 화면
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) { }
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }

    // [다이얼로그들]

    // 1. DatePicker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = savedDateMillis ?: System.currentTimeMillis()
        )
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = MinimalAccent, // 포인트 색상 적용
                onPrimary = Color.White,
                surface = MinimalCardColor,
                onSurface = MinimalTextMain
            )
        ) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            isLoading = true
                            coroutineScope.launch {
                                val selectedDate =
                                    datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                                try {
                                    withContext(Dispatchers.IO) {
                                        saveStartDate(context, selectedDate)
                                        val db = AppDatabase.getDatabase(context)
                                        val dao = db.anniversaryDao()
                                        val allItems = dao.getAll()
                                        allItems.forEach { item -> dao.deleteById(item.id) }

                                        val todayCalendar = Calendar.getInstance().apply {
                                            set(Calendar.HOUR_OF_DAY, 0); set(
                                            Calendar.MINUTE,
                                            0
                                        ); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                                        }
                                        val todayThreshold = todayCalendar.timeInMillis

                                        val date50 = calculateTargetDate(selectedDate, 50)
                                        if (date50 >= todayThreshold) {
                                            val title = context.getString(
                                                R.string.anniversary_title_format,
                                                50
                                            )
                                            dao.insert(
                                                AnniversaryItem(
                                                    title = title,
                                                    dateMillis = date50,
                                                    dateCount = 50
                                                )
                                            )
                                        }
                                        for (i in 1..20) {
                                            val days = i * 100
                                            val dateDays = calculateTargetDate(selectedDate, days)
                                            if (dateDays >= todayThreshold) {
                                                val title = context.getString(
                                                    R.string.anniversary_title_format,
                                                    days
                                                )
                                                dao.insert(
                                                    AnniversaryItem(
                                                        title = title,
                                                        dateMillis = dateDays,
                                                        dateCount = days
                                                    )
                                                )
                                            }
                                        }
                                        DDayGlanceWidget.updateAllWidgets(context)
                                    }
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    ) { Text(stringResource(R.string.confirm), color = MinimalAccent) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(R.string.cancel), color = MinimalTextSub)
                    }
                },
                colors = DatePickerDefaults.colors(containerColor = MinimalCardColor)
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = MinimalCardColor, // 달력 전체 배경을 명시적으로 흰색 고정
                        titleContentColor = MinimalTextSub, // 상단 '날짜 선택' 글자색
                        headlineContentColor = MinimalTextMain, // 선택된 날짜(큰 글씨) 색상
                        weekdayContentColor = MinimalTextSub, // 월, 화, 수... 요일 색상
                        subheadContentColor = MinimalTextMain, // 상단 '2026년 2월' 글자색
                        navigationContentColor = MinimalTextMain, // 좌우 화살표 색상
                        yearContentColor = MinimalTextMain, // 연도 선택 시 일반 연도 색상
                        currentYearContentColor = MinimalAccent, // 연도 선택 시 현재 연도 색상
                        selectedYearContentColor = Color.White, // 연도 선택 시 선택된 연도 글자색
                        selectedYearContainerColor = MinimalAccent, // 연도 선택 시 선택된 연도 배경색
                        dayContentColor = MinimalTextMain, // 일반 날짜 글자색
                        selectedDayContentColor = Color.White, // 선택된 날짜 글자색
                        selectedDayContainerColor = MinimalAccent, // 선택된 날짜 배경색
                        todayContentColor = MinimalAccent, // 오늘 날짜 글자색
                        todayDateBorderColor = MinimalAccent // 오늘 날짜 테두리색
                    )
                )
            }
        }
    }

    // 2. 타이틀 수정 다이얼로그
    if (showTitleDialog) {
        var tempTitle by remember { mutableStateOf(storedTitle) }
        AlertDialog(
            onDismissRequest = { showTitleDialog = false },
            containerColor = MinimalCardColor,
            title = {
                Text(
                    stringResource(R.string.dialog_title_change_main_text),
                    fontWeight = FontWeight.Bold,
                    color = MinimalTextMain
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempTitle,
                        onValueChange = { if (it.length <= 15) tempTitle = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MinimalAccent,
                            unfocusedBorderColor = MinimalTextSub.copy(alpha = 0.5f),
                            cursorColor = MinimalAccent,
                            focusedTextColor = MinimalTextMain,
                            unfocusedTextColor = MinimalTextMain
                        )
                    )
                    Text(
                        stringResource(R.string.text_length_format, tempTitle.length),
                        fontSize = 12.sp,
                        color = MinimalTextSub,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (tempTitle.isNotBlank()) coroutineScope.launch {
                        saveStartTitle(context, tempTitle)
                        showTitleDialog = false
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = MinimalAccent)) {
                    Text(
                        stringResource(R.string.change)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showTitleDialog = false }) {
                    Text(
                        stringResource(R.string.cancel),
                        color = MinimalTextSub
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 3. 가이드 다이얼로그 (색상 미니멀하게 조정)
    if (showGuideDialog) {
        Dialog(
            onDismissRequest = { showGuideDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = MinimalBgColor) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.widget_setup_guide),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MinimalTextMain
                        )
                        IconButton(onClick = {
                            showGuideDialog = false
                        }) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "닫기",
                                tint = MinimalTextSub
                            )
                        }
                    }
                    HorizontalDivider(color = MinimalTextSub.copy(alpha = 0.2f))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(40.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(40.dp)
                        ) {
                            GuidePageItem(
                                1,
                                stringResource(R.string.guide_step1_title),
                                stringResource(R.string.guide_step1_desc),
                                R.drawable.guide_first
                            )
                            GuidePageItem(
                                2,
                                stringResource(R.string.guide_step2_title),
                                stringResource(R.string.guide_step2_desc),
                                R.drawable.guide_second
                            )
                            GuidePageItem(
                                3,
                                stringResource(R.string.guide_step3_title),
                                stringResource(R.string.guide_step3_desc),
                                R.drawable.guide_third
                            )

                            Button(
                                onClick = { showGuideDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = MinimalAccent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text(stringResource(R.string.btn_guide_start), fontSize = 16.sp) }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GuidePageItem(step: Int, title: String, description: String, @DrawableRes imageResId: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .background(MinimalAccentLight, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                stringResource(R.string.step_format, step),
                style = MaterialTheme.typography.labelMedium,
                color = MinimalAccent,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MinimalTextMain
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MinimalTextSub,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(0.6f)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ExactAlarmPermissionCheck(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.8f))
            .border(1.dp, MinimalTextSub.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.permission_card_title_needed),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MinimalTextMain
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                } else {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                }
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = MinimalAccent),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            modifier = Modifier.height(36.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(stringResource(R.string.btn_allow_permission), fontSize = 13.sp)
        }
    }
}