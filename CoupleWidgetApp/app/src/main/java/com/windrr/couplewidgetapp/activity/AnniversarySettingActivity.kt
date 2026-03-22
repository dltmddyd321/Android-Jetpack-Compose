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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
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
import com.windrr.couplewidgetapp.ui.theme.MinimalAccent
import com.windrr.couplewidgetapp.ui.theme.MinimalBgColor
import com.windrr.couplewidgetapp.ui.theme.MinimalCardColor
import com.windrr.couplewidgetapp.ui.theme.MinimalDeleteRed
import com.windrr.couplewidgetapp.ui.theme.MinimalTextMain
import com.windrr.couplewidgetapp.ui.theme.MinimalTextSub
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
            navigationBarStyle = SystemBarStyle.auto(
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
                    onBackClick = { finish() }
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

    // 수정 다이얼로그 관련 상태
    var showEditDialog by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<AnniversaryItem?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editSelectedTab by remember { mutableIntStateOf(0) }
    var editSelectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var editShowDatePicker by remember { mutableStateOf(false) }
    var editNumberInput by remember { mutableStateOf("") }

    // 수정 다이얼로그가 열릴 때 상태 초기화
    LaunchedEffect(editItem) {
        editItem?.let { item ->
            editTitle = item.title
            editSelectedTab = if (item.dateCount == 0) 0 else 1
            editSelectedDateMillis = item.dateMillis
            editNumberInput = if (item.dateCount > 0) item.dateCount.toString() else ""
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MinimalBgColor) // 미니멀 배경
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            // [상단 헤더 영역]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.desc_back),
                        tint = MinimalTextMain
                    )
                }
                Text(
                    text = stringResource(R.string.title_add_anniversary),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MinimalTextMain
                )
            }

            // [메인 스크롤 영역]
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. 입력 폼 섹션
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MinimalCardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            // 탭 버튼
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MinimalBgColor, RoundedCornerShape(12.dp))
                                    .padding(4.dp)
                            ) {
                                TabButton(
                                    text = stringResource(R.string.tab_select_date),
                                    isSelected = selectedTab == 0
                                ) { selectedTab = 0 }
                                TabButton(
                                    text = stringResource(R.string.tab_input_dday),
                                    isSelected = selectedTab == 1
                                ) { selectedTab = 1 }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // 기념일 이름 입력
                            OutlinedTextField(
                                value = titleInput,
                                onValueChange = { titleInput = it },
                                label = { Text(stringResource(R.string.label_anniversary_name)) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MinimalAccent,
                                    unfocusedBorderColor = MinimalTextSub.copy(alpha = 0.3f),
                                    focusedLabelColor = MinimalAccent,
                                    unfocusedLabelColor = MinimalTextSub,
                                    cursorColor = MinimalAccent,
                                    focusedTextColor = MinimalTextMain,
                                    unfocusedTextColor = MinimalTextMain
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (selectedTab == 0) {
                                val dateString = formatAnnualDate(context, selectedDateMillis)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { showDatePicker = true }
                                        .background(MinimalBgColor)
                                        .padding(16.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = MinimalAccent
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = dateString,
                                        color = MinimalTextMain,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.msg_annual_repeat_desc),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MinimalTextSub,
                                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                                )
                            } else {
                                OutlinedTextField(
                                    value = numberInput,
                                    onValueChange = {
                                        if (it.all { char -> char.isDigit() }) numberInput = it
                                    },
                                    label = { Text(stringResource(R.string.label_how_many_days)) },
                                    trailingIcon = {
                                        Text(
                                            stringResource(R.string.suffix_day_unit),
                                            color = MinimalTextSub
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MinimalAccent,
                                        unfocusedBorderColor = MinimalTextSub.copy(alpha = 0.3f),
                                        focusedLabelColor = MinimalAccent,
                                        unfocusedLabelColor = MinimalTextSub,
                                        cursorColor = MinimalAccent,
                                        focusedTextColor = MinimalTextMain,
                                        unfocusedTextColor = MinimalTextMain
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // 등록 버튼
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
                                colors = ButtonDefaults.buttonColors(containerColor = MinimalAccent),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.btn_register),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
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
                            text = stringResource(R.string.header_upcoming),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MinimalTextMain,
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MinimalAccent,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }

                // 2. 리스트 아이템 섹션
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
                                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                                    MinimalDeleteRed else Color.Transparent,
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
                            AnniversaryItemCard(
                                item = item,
                                onEditClick = { anniversaryItem ->
                                    editItem = anniversaryItem
                                    showEditDialog = true
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    // DatePicker
    if (showDatePicker) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = MinimalAccent,
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
                            selectedDateMillis =
                                datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            showDatePicker = false
                        }
                    ) {
                        Text(
                            stringResource(R.string.confirm),
                            fontWeight = FontWeight.Bold,
                            color = MinimalAccent
                        )
                    }
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

    // 수정 다이얼로그
    if (showEditDialog && editItem != null) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                editItem = null
            },
            containerColor = MinimalCardColor,
            title = {
                Text(
                    "기념일 수정",
                    fontWeight = FontWeight.Bold,
                    color = MinimalTextMain
                )
            },
            text = {
                Column {
                    // 기념일 이름 입력
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("기념일 이름") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MinimalAccent,
                            unfocusedBorderColor = MinimalTextSub.copy(alpha = 0.3f),
                            focusedLabelColor = MinimalAccent,
                            unfocusedLabelColor = MinimalTextSub,
                            cursorColor = MinimalAccent,
                            focusedTextColor = MinimalTextMain,
                            unfocusedTextColor = MinimalTextMain
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 탭 버튼
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MinimalBgColor, RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        TabButton(
                            text = "날짜 선택",
                            isSelected = editSelectedTab == 0
                        ) { editSelectedTab = 0 }
                        TabButton(
                            text = "D-Day 입력",
                            isSelected = editSelectedTab == 1
                        ) { editSelectedTab = 1 }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (editSelectedTab == 0) {
                        val dateString = formatAnnualDate(context, editSelectedDateMillis)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { editShowDatePicker = true }
                                .background(MinimalBgColor)
                                .padding(16.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Star,
                                contentDescription = null,
                                tint = MinimalAccent
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = dateString,
                                color = MinimalTextMain,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "매년 반복됩니다",
                            style = MaterialTheme.typography.labelSmall,
                            color = MinimalTextSub,
                            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                        )
                    } else {
                        OutlinedTextField(
                            value = editNumberInput,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) editNumberInput = it
                            },
                            label = { Text("며칠") },
                            trailingIcon = {
                                Text(
                                    "일",
                                    color = MinimalTextSub
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MinimalAccent,
                                unfocusedBorderColor = MinimalTextSub.copy(alpha = 0.3f),
                                focusedLabelColor = MinimalAccent,
                                unfocusedLabelColor = MinimalTextSub,
                                cursorColor = MinimalAccent,
                                focusedTextColor = MinimalTextMain,
                                unfocusedTextColor = MinimalTextMain
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTitle.isNotBlank() && editItem != null) {
                            val (finalDate, finalCount) = if (editSelectedTab == 0) {
                                editSelectedDateMillis to 0
                            } else {
                                val days = editNumberInput.toIntOrNull() ?: 0
                                calculateDateFromBase(baseStartDate, days.toLong()) to days
                            }

                            viewModel.handleIntent(
                                AnniversaryIntent.UpdateAnniversary(
                                    id = editItem!!.id,
                                    title = editTitle,
                                    dateMillis = finalDate,
                                    dateCount = finalCount
                                )
                            )

                            showEditDialog = false
                            editItem = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MinimalAccent)
                ) {
                    Text("수정 완료", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                    editItem = null
                }) {
                    Text("취소", color = MinimalTextSub)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 수정용 DatePicker
    if (editShowDatePicker) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = editSelectedDateMillis)

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = MinimalAccent,
                onPrimary = Color.White,
                surface = MinimalCardColor,
                onSurface = MinimalTextMain
            )
        ) {
            DatePickerDialog(
                onDismissRequest = { editShowDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            editSelectedDateMillis =
                                datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            editShowDatePicker = false
                        }
                    ) {
                        Text("확인", fontWeight = FontWeight.Bold, color = MinimalAccent)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editShowDatePicker = false }) {
                        Text("취소", color = MinimalTextSub)
                    }
                },
                colors = DatePickerDefaults.colors(containerColor = MinimalCardColor)
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = MinimalCardColor,
                        titleContentColor = MinimalTextSub,
                        headlineContentColor = MinimalTextMain,
                        weekdayContentColor = MinimalTextSub,
                        subheadContentColor = MinimalTextMain,
                        navigationContentColor = MinimalTextMain,
                        yearContentColor = MinimalTextMain,
                        currentYearContentColor = MinimalAccent,
                        selectedYearContentColor = Color.White,
                        selectedYearContainerColor = MinimalAccent,
                        dayContentColor = MinimalTextMain,
                        selectedDayContentColor = Color.White,
                        selectedDayContainerColor = MinimalAccent,
                        todayContentColor = MinimalAccent,
                        todayDateBorderColor = MinimalAccent
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
            .background(if (isSelected) MinimalCardColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) MinimalAccent else MinimalTextSub,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun AnniversaryItemCard(
    item: AnniversaryItem,
    onEditClick: (AnniversaryItem) -> Unit = {}
) {
    val context = LocalContext.current

    val displayDateText = if (item.dateCount == 0) {
        formatAnnualDate(context, item.dateMillis)
    } else {
        formatDate(item.dateMillis)
    }

    val targetMillisForDDay = if (item.dateCount == 0) {
        calculateNextAnniversaryDate(item.dateMillis)
    } else {
        item.dateMillis
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MinimalCardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MinimalTextMain
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = displayDateText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MinimalTextSub
                )
            }

            val dDay = getDDayCount(targetMillisForDDay)
            val dDayString = when {
                dDay == 0L -> stringResource(R.string.d_day_today)
                dDay > 0 -> stringResource(R.string.d_day_d_minus_format, dDay)
                else -> stringResource(R.string.d_day_d_plus_format, -dDay)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = dDayString,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (dDay <= 0) MinimalAccent else MinimalTextSub
                )

                IconButton(
                    onClick = { onEditClick(item) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "수정",
                        tint = MinimalTextSub.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Helper Functions
fun calculateDateFromBase(baseMillis: Long, days: Long): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = baseMillis }
    calendar.add(Calendar.DAY_OF_YEAR, (days - 1).toInt())
    return calendar.timeInMillis
}

fun formatDate(millis: Long): String {
    val locale = Locale.getDefault()
    val pattern = if (locale.language == "ko") {
        "yyyy.MM.dd (E)"
    } else {
        "EEE, MMM dd, yyyy"
    }
    val formatter = SimpleDateFormat(pattern, locale)
    return formatter.format(Date(millis))
}

fun formatAnnualDate(context: Context, millis: Long): String {
    val locale = Locale.getDefault()
    val pattern = if (locale.language == "ko") {
        "M.d"
    } else {
        "MMM dd"
    }
    val formatter = SimpleDateFormat(pattern, locale)
    val dateStr = formatter.format(Date(millis))

    return if (locale.language == "ko") {
        context.getString(R.string.format_every_year, dateStr)
    } else {
        "Every $dateStr"
    }
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