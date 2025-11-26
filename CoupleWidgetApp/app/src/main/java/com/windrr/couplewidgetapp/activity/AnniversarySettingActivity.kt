package com.windrr.couplewidgetapp.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.windrr.couplewidgetapp.anniversary.AnniversaryIntent
import com.windrr.couplewidgetapp.anniversary.AnniversaryItem
import com.windrr.couplewidgetapp.anniversary.AnniversarySideEffect
import com.windrr.couplewidgetapp.anniversary.AnniversaryViewModel
import com.windrr.couplewidgetapp.anniversary.AnniversaryViewModelFactory
import com.windrr.couplewidgetapp.anniversary.AppDatabase
import java.text.SimpleDateFormat
import java.util.*
import com.windrr.couplewidgetapp.ui.theme.CoupleWidgetAppTheme
import com.windrr.couplewidgetapp.ui.theme.CreamWhite
import com.windrr.couplewidgetapp.ui.theme.LovelyPink
import com.windrr.couplewidgetapp.ui.theme.SoftGray
import com.windrr.couplewidgetapp.ui.theme.SoftPeach
import com.windrr.couplewidgetapp.ui.theme.WarmText

class AnniversarySettingActivity : ComponentActivity() {

    private lateinit var viewModel: AnniversaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.anniversaryDao()
        val factory = AnniversaryViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[AnniversaryViewModel::class.java]
        val baseStartDate = intent.getLongExtra("BASE_DATE", System.currentTimeMillis())

        setContent {
            CoupleWidgetAppTheme {
                AnniversaryManagementScreen(
                    baseStartDate = baseStartDate,
                    viewModel = viewModel,
                    onBackClick = {
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnniversaryManagementScreen(
    modifier: Modifier = Modifier,
    baseStartDate: Long,
    viewModel: AnniversaryViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // [MVI - View] 1. State 구독 (UI 상태 동기화)
    // collectAsStateWithLifecycle()을 쓰면 앱이 백그라운드로 갈 때 리소스 절약 가능
    val state by viewModel.state.collectAsState()

    // [MVI - View] 2. SideEffect 처리 (토스트 메시지 등)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AnniversarySideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // --- UI 입력 상태 (View 내부 상태) ---
    var selectedTab by remember { mutableIntStateOf(0) }
    var titleInput by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var numberInput by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CreamWhite, SoftPeach.copy(alpha = 0.3f))
                )
            )
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 헤더 영역
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = SoftGray)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "기념일 추가하기",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = WarmText
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 입력 카드
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // 탭 버튼들
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        TabButton(text = "날짜 선택", isSelected = selectedTab == 0) { selectedTab = 0 }
                        TabButton(text = "D-Day 입력", isSelected = selectedTab == 1) {
                            selectedTab = 1
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 제목 입력
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("기념일 이름") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LovelyPink,
                            focusedLabelColor = LovelyPink,
                            cursorColor = LovelyPink
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 탭에 따른 입력 UI
                    if (selectedTab == 0) {
                        val dateString = formatDate(selectedDateMillis)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true }
                                .background(CreamWhite, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = LovelyPink)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = dateString,
                                color = WarmText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = numberInput,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) numberInput = it
                            },
                            label = { Text("며칠째 되는 날인가요?") },
                            trailingIcon = { Text("일  ", color = SoftGray) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LovelyPink,
                                focusedLabelColor = LovelyPink,
                                cursorColor = LovelyPink
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // [MVI - Intent] 등록 버튼 클릭 시 Intent 전달
                    Button(
                        onClick = {
                            if (titleInput.isBlank()) return@Button

                            val (finalDate, finalCount) = if (selectedTab == 0) {
                                selectedDateMillis to 0
                            } else {
                                val days = numberInput.toIntOrNull() ?: 0
                                calculateDateFromBase(baseStartDate, days.toLong()) to days
                            }

                            // ViewModel에게 "추가해줘"라는 Intent 전송
                            viewModel.handleIntent(
                                AnniversaryIntent.AddAnniversary(
                                    title = titleInput,
                                    dateMillis = finalDate,
                                    dateCount = finalCount
                                )
                            )

                            // 입력창 초기화
                            titleInput = ""
                            numberInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LovelyPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("등록하기", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 리스트 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "다가오는 기념일",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftGray,
                    modifier = Modifier.padding(start = 4.dp)
                )

                // 로딩 인디케이터 (State.isLoading 활용)
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = LovelyPink,
                        strokeWidth = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // [MVI - View] 리스트 렌더링 (State.anniversaries 활용)
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.anniversaries) { item ->
                    AnniversaryItemCard(
                        item = item,
                        // 롱 클릭 시 삭제 Intent 전송
                        onLongClick = {
                            viewModel.handleIntent(AnniversaryIntent.DeleteAnniversary(item.id))
                        }
                    )
                }
            }
        }
    }

    // DatePicker UI (생략 없이 동일하게 유지)
    if (showDatePicker) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = LovelyPink,
                onPrimary = Color.White,
                surface = Color.White,
                onSurface = WarmText
            )
        ) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedDateMillis =
                                datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            showDatePicker = false
                        }
                    ) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("취소")
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White
                )
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun RowScope.TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) LovelyPink else SoftGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnniversaryItemCard(
    item: AnniversaryItem,
    onLongClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CreamWhite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.ThumbUp,
                    contentDescription = null,
                    tint = LovelyPink,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = WarmText
                )
                Text(
                    text = formatDate(item.dateMillis),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftGray
                )
            }

            val dDay = getDDayCount(item.dateMillis)
            val dDayString = when {
                dDay == 0L -> "Today"
                dDay > 0 -> "D-${dDay}"
                else -> "D+${-dDay}"
            }

            Text(
                text = dDayString,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (dDay <= 0) LovelyPink else SoftGray
            )
        }
    }
}

fun calculateDateFromBase(baseMillis: Long, days: Long): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = baseMillis }
    calendar.add(Calendar.DAY_OF_YEAR, (days - 1).toInt())
    return calendar.timeInMillis
}

fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREA)
    return formatter.format(Date(millis))
}

fun getDDayCount(targetMillis: Long): Long {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(
        Calendar.SECOND,
        0
    ); set(Calendar.MILLISECOND, 0)
    }
    val target = Calendar.getInstance().apply {
        timeInMillis = targetMillis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(
        Calendar.SECOND,
        0
    ); set(Calendar.MILLISECOND, 0)
    }
    val diff = target.timeInMillis - today.timeInMillis
    return diff / (24 * 60 * 60 * 1000)
}