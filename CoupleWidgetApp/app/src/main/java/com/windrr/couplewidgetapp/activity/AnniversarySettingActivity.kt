package com.windrr.couplewidgetapp.activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.windrr.couplewidgetapp.R
import com.windrr.couplewidgetapp.anniversary.AnniversaryIntent
import com.windrr.couplewidgetapp.anniversary.AnniversaryItem
import com.windrr.couplewidgetapp.anniversary.AnniversarySideEffect
import com.windrr.couplewidgetapp.anniversary.AnniversaryViewModel
import com.windrr.couplewidgetapp.anniversary.AnniversaryViewModelFactory
import com.windrr.couplewidgetapp.anniversary.AppDatabase
import com.windrr.couplewidgetapp.ui.theme.CoupleWidgetAppTheme
import com.windrr.couplewidgetapp.ui.theme.CreamWhite
import com.windrr.couplewidgetapp.ui.theme.LovelyPink
import com.windrr.couplewidgetapp.ui.theme.SoftGray
import com.windrr.couplewidgetapp.ui.theme.SoftPeach
import com.windrr.couplewidgetapp.ui.theme.WarmText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AnniversarySettingActivity : ComponentActivity() {

    private lateinit var viewModel: AnniversaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

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

    // [MVI - View] 1. State 구독
    val state by viewModel.state.collectAsState()

    val sortedAnniversaries = remember(state.anniversaries) {
        state.anniversaries.sortedBy { item ->
            if (item.dateCount == 0) {
                calculateNextAnniversaryDate(item.dateMillis)
            } else {
                item.dateMillis
            }
        }
    }

    // [MVI - View] 2. SideEffect 처리
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AnniversarySideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.desc_back), // 리소스 적용
                        tint = SoftGray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.title_add_anniversary), // "기념일 추가하기"
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = WarmText
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        TabButton(text = stringResource(R.string.tab_select_date), isSelected = selectedTab == 0) { selectedTab = 0 }
                        TabButton(text = stringResource(R.string.tab_input_dday), isSelected = selectedTab == 1) {
                            selectedTab = 1
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text(stringResource(R.string.label_anniversary_name)) }, // "기념일 이름"
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LovelyPink,
                            focusedLabelColor = LovelyPink,
                            cursorColor = LovelyPink,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedTab == 0) {
                        // [수정] 날짜 포맷팅에 Context 전달
                        val dateString = formatAnnualDate(context, selectedDateMillis)

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
                        Text(
                            text = stringResource(R.string.msg_annual_repeat_desc), // "매년 반복되는..."
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftGray,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    } else {
                        OutlinedTextField(
                            value = numberInput,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) numberInput = it
                            },
                            label = { Text(stringResource(R.string.label_how_many_days)) }, // "며칠째 되는..."
                            trailingIcon = { Text(stringResource(R.string.suffix_day_unit), color = SoftGray) }, // "일 "
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LovelyPink,
                                focusedLabelColor = LovelyPink,
                                cursorColor = LovelyPink,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (titleInput.isBlank()) return@Button

                            val (finalDate, finalCount) = if (selectedTab == 0) {
                                selectedDateMillis to 0
                            } else {
                                val days = numberInput.toIntOrNull() ?: 0
                                calculateDateFromBase(baseStartDate, days.toLong()) to days
                            }

                            viewModel.handleIntent(
                                AnniversaryIntent.AddAnniversary(
                                    title = titleInput,
                                    dateMillis = finalDate,
                                    dateCount = finalCount
                                )
                            )

                            titleInput = ""
                            numberInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LovelyPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(stringResource(R.string.btn_register), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.header_upcoming), // "다가오는 기념일"
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftGray,
                    modifier = Modifier.padding(start = 4.dp)
                )

                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = LovelyPink,
                        strokeWidth = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = sortedAnniversaries,
                    key = { it.id }
                ) { item ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.handleIntent(AnniversaryIntent.DeleteAnniversary(item.id))
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color by animateColorAsState(
                                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color(
                                    0xFFFF5252
                                ) else Color.Transparent,
                                label = "DismissColor"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color, RoundedCornerShape(16.dp))
                                    .padding(end = 24.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = stringResource(R.string.desc_delete_icon),
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        enableDismissFromStartToEnd = false,
                        content = {
                            AnniversaryItemCard(item = item)
                        }
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = LovelyPink,
                onPrimary = Color.White,
                surface = Color.White,
                onSurface = Color.Black // 날짜 텍스트 가독성 (검은색)
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
                        Text(stringResource(R.string.confirm), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(R.string.cancel), color = SoftGray)
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White
                )
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        dayContentColor = Color.Black,
                        selectedDayContentColor = Color.White,
                        selectedDayContainerColor = LovelyPink,
                        todayDateBorderColor = LovelyPink,
                        todayContentColor = LovelyPink,
                        weekdayContentColor = SoftGray,
                        yearContentColor = Color.Black,
                        currentYearContentColor = LovelyPink
                    )
                )
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

@Composable
fun AnniversaryItemCard(
    item: AnniversaryItem
) {
    val context = LocalContext.current

    // [수정] 날짜 포맷팅에 Context 전달 (리소스 사용)
    val displayDateText = if (item.dateCount == 0) {
        formatAnnualDate(context, item.dateMillis)
    } else {
        formatDate(context, item.dateMillis)
    }

    val targetMillisForDDay = if (item.dateCount == 0) {
        calculateNextAnniversaryDate(item.dateMillis)
    } else {
        item.dateMillis
    }

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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CreamWhite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (item.dateCount > 0) Icons.Rounded.ThumbUp else Icons.Rounded.Star,
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
                    text = displayDateText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftGray
                )
            }

            // [수정] D-Day 문자열 리소스 사용
            val dDay = getDDayCount(targetMillisForDDay)
            val dDayString = when {
                dDay == 0L -> stringResource(R.string.d_day_today)
                dDay > 0 -> stringResource(R.string.d_day_d_minus_format, dDay)
                else -> stringResource(R.string.d_day_d_plus_format, -dDay)
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

// [수정] Context를 받아 리소스에서 날짜 패턴을 가져오도록 변경
fun formatDate(context: Context, millis: Long): String {
    val pattern = context.getString(R.string.pattern_date_full) // "yyyy년 MM월 dd일 (E)"
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(millis))
}

// [수정] Context를 받아 리소스에서 패턴 및 포맷을 가져오도록 변경
fun formatAnnualDate(context: Context, millis: Long): String {
    val pattern = context.getString(R.string.pattern_date_annual) // "M월 d일"
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    val dateStr = formatter.format(Date(millis))
    return context.getString(R.string.format_every_year, dateStr) // "매년 %s"
}

fun getDDayCount(targetMillis: Long): Long {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0);
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val target = Calendar.getInstance().apply {
        timeInMillis = targetMillis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0);
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val diff = target.timeInMillis - today.timeInMillis
    return diff / (24 * 60 * 60 * 1000)
}

fun calculateNextAnniversaryDate(selectedMillis: Long): Long {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0);
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }

    val selected = Calendar.getInstance().apply {
        timeInMillis = selectedMillis
    }

    val target = Calendar.getInstance().apply {
        set(Calendar.YEAR, today.get(Calendar.YEAR))
        set(Calendar.MONTH, selected.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, selected.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0);
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }

    if (target.before(today)) {
        target.add(Calendar.YEAR, 1)
    }

    return target.timeInMillis
}