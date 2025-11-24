package com.windrr.couplewidgetapp

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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import java.text.SimpleDateFormat
import java.util.*
import com.windrr.couplewidgetapp.ui.theme.CoupleWidgetAppTheme
import com.windrr.couplewidgetapp.ui.theme.CreamWhite
import com.windrr.couplewidgetapp.ui.theme.LovelyPink
import com.windrr.couplewidgetapp.ui.theme.SoftGray
import com.windrr.couplewidgetapp.ui.theme.SoftPeach
import com.windrr.couplewidgetapp.ui.theme.WarmText

class AnniversarySettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val baseStartDate = intent.getLongExtra("BASE_DATE", System.currentTimeMillis())

        setContent {
            CoupleWidgetAppTheme {
                AnniversaryManagementScreen(
                    baseStartDate = baseStartDate,
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
    baseStartDate: Long, // 메인에서 관리하는 '사귄 날짜' (숫자 계산용)
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // 입력 상태 관리
    var selectedTab by remember { mutableIntStateOf(0) } // 0: 날짜 지정, 1: 숫자 입력
    var titleInput by remember { mutableStateOf("") }

    // 0: 날짜 지정용 상태
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // 1: 숫자 입력용 상태
    var numberInput by remember { mutableStateOf("") }

    // TODO: DB에서 불러온 리스트 상태 (임시 데이터)
    val anniversaryList = remember { mutableStateListOf<AnniversaryItem>() }

    // 배경 그라데이션 (메인과 통일)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CreamWhite, SoftPeach.copy(alpha = 0.3f))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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

            // [입력 섹션] 카드 형태
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // 탭 스위치
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        TabButton(text = "날짜 선택", isSelected = selectedTab == 0) { selectedTab = 0 }
                        TabButton(text = "D-Day 입력", isSelected = selectedTab == 1) { selectedTab = 1 }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 제목 입력 (공통)
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("기념일 이름 (예: 생일, 100일)") },
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

                    if (selectedTab == 0) {
                        // [Tab 1] 직접 날짜 선택
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
                            Text(text = dateString, color = WarmText, fontWeight = FontWeight.Medium)
                        }
                    } else {
                        // [Tab 2] 숫자(N일) 입력
                        OutlinedTextField(
                            value = numberInput,
                            onValueChange = { if (it.all { char -> char.isDigit() }) numberInput = it },
                            label = { Text("며칠째 되는 날인가요? (예: 100)") },
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
                        // 계산된 날짜 미리보기
                        if (numberInput.isNotEmpty()) {
                            val days = numberInput.toLongOrNull() ?: 0L
                            val calcDate = calculateDateFromBase(baseStartDate, days)
                            Text(
                                text = "📅 ${formatDate(calcDate)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = LovelyPink,
                                modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 등록 버튼
                    Button(
                        onClick = {
                            if (titleInput.isBlank()) {
                                Toast.makeText(context, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val finalDate = if (selectedTab == 0) {
                                selectedDateMillis
                            } else {
                                val days = numberInput.toLongOrNull() ?: 0L
                                calculateDateFromBase(baseStartDate, days)
                            }

                            // TODO: 로컬 DB에 저장하는 로직 호출 (Insert)
                            // repository.insertAnniversary(titleInput, finalDate, ...)

                            // UI 업데이트용 임시 추가
                            anniversaryList.add(0, AnniversaryItem(title = titleInput, dateMillis = finalDate))

                            // 초기화
                            titleInput = ""
                            numberInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LovelyPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("등록하기", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = SoftPeach, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "다가오는 기념일",
                style = MaterialTheme.typography.titleMedium,
                color = SoftGray,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // [리스트 섹션]
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // TODO: DB에서 가져온 데이터 연결 (Observe)
                items(anniversaryList) { item ->
                    AnniversaryItemCard(item = item)
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            colors = DatePickerDefaults.colors(containerColor = CreamWhite, selectedDayContainerColor = LovelyPink),
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    showDatePicker = false
                }) { Text("확인", color = LovelyPink) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("취소", color = SoftGray) }
            }
        ) {
            DatePicker(state = datePickerState)
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

@Composable
fun AnniversaryItemCard(item: AnniversaryItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CreamWhite),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.ThumbUp, contentDescription = null, tint = LovelyPink, modifier = Modifier.size(20.dp))
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

            // D-Day 남은 날짜 표시 (옵션)
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
                color = if(dDay <= 0) LovelyPink else SoftGray
            )
        }
    }
}

// --- 유틸 함수 ---

// N일째 되는 날짜 계산 (기준일 + (N-1)일)
fun calculateDateFromBase(baseMillis: Long, days: Long): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = baseMillis }
    calendar.add(Calendar.DAY_OF_YEAR, (days - 1).toInt())
    return calendar.timeInMillis
}

fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREA)
    return formatter.format(Date(millis))
}

// 오늘 기준 D-Day 계산
fun getDDayCount(targetMillis: Long): Long {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val target = Calendar.getInstance().apply {
        timeInMillis = targetMillis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val diff = target.timeInMillis - today.timeInMillis
    return diff / (24 * 60 * 60 * 1000)
}

data class AnniversaryItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val dateMillis: Long
)