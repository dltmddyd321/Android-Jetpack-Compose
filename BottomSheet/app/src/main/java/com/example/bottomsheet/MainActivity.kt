package com.example.bottomsheet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bottomsheet.ui.theme.BottomSheetTheme
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomSheetTheme {
                val sheetState = rememberBottomSheetState(
                    initialValue = BottomSheetValue.Collapsed,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy
                    )
                )
                val scaffoldState = rememberBottomSheetScaffoldState(
                    bottomSheetState = sheetState
                )
                val scope = rememberCoroutineScope()
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                    contentAlignment = Alignment.Center) {
                        Text(text = "BottomSheet",
                        fontSize = 60.sp)
                    }
                },
                sheetBackgroundColor = Color.Green,
                    sheetPeekHeight = 0.dp
                    ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            scope.launch {
                                if (sheetState.isCollapsed) {
                                    sheetState.expand()
                                } else {
                                    sheetState.collapse()
                                }
                            }
                        }) {
                            Text(text = "Toggle Sheet")
                        }
                    }
                }
            }
        }
    }
}

class DefaultAlarmDetailActivity : ComponentActivity() {
    private val All_DAY_CUSTOM_ALARM = "All_DAY_CUSTOM_ALARM"
    private val EVENT_CUSTOM_ALARM = "EVENT_CUSTOM_ALARM"
    private val PLAN_CUSTOM_ALARM = "PLAN_CUSTOM_ALARM"
    private val alarmManager = TimeBlockAlarmManager.getInstance()
    private var selected = 0

    data class CustomAlarm(
        val day: Int,
        val hour: Int,
        val minute: Int,
        val period: String?
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = intent.getStringExtra("TYPE") ?: run {
            finish()
            return
        }
        val title = when (type) {
            "interval" -> getString(R.string.plan)
            "allDayEvent" -> getString(R.string.allday_event)
            else -> getString(R.string.event)
        }
        val list = if (type != "timeEvent") {
            resources.getStringArray(R.array.default_alarm_time_allday_event) + "사용자화"
        } else {
            resources.getStringArray(R.array.default_alarm_time_event) + "사용자화"
        }
        selected = checkDefaultAlarm(type, list)
        setContent {
            MainScreen(title, list, type)
        }
    }

    private fun checkDefaultAlarm(type: String, list: Array<String>): Int {
        val alarmOffset = when (type) {
            "interval" -> alarmManager.defaultPlanAlarmTime
            "allDayEvent" -> alarmManager.defaultAlldayEventAlarmOffset
            else -> alarmManager.defaultEventAlarmOffset
        }
        return if (alarmOffset == java.lang.Long.MIN_VALUE) {
            val key = when (type) {
                "interval" -> PLAN_CUSTOM_ALARM
                "allDayEvent" -> All_DAY_CUSTOM_ALARM
                else -> EVENT_CUSTOM_ALARM
            }
            val customAlarm = alarmManager.getCustomAlarm(key)
            if (customAlarm.isNotEmpty()) list.size - 1 else 0
        }
        else Alarm.offsetToSpinnerIndex(type != "timeEvent", alarmOffset) + 1
    }

    private fun checkDefaultCustomAlarm(type: String): CustomAlarm? {
        val key = when (type) {
            "interval" -> PLAN_CUSTOM_ALARM
            "allDayEvent"-> All_DAY_CUSTOM_ALARM
            else -> EVENT_CUSTOM_ALARM
        }
        val alarmData = alarmManager.getCustomAlarm(key)
        if (alarmData.isEmpty()) return null
        val beforeDay = (alarmData["daysBefore"])?.toInt() ?: 0
        val parts = (alarmData["time"])?.split(" ".toRegex())
            ?.dropLastWhile { it.isEmpty() }?.toTypedArray() ?: return null
        val timeParts = parts[0].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val amPm = if (parts.size > 1) parts[1] else null
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        return CustomAlarm(beforeDay, hour, minute, amPm)
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    private fun MainScreen(title: String, list: Array<String>, type: String) {
        var selectedIndex by remember { mutableIntStateOf(selected) }
        val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val coroutineScope = rememberCoroutineScope()

        BackHandler(enabled = sheetState.isVisible) {
            coroutineScope.launch { sheetState.hide() }
        }

        ModalBottomSheetLayout(sheetState = sheetState, sheetContent = {
            val existAlarm = checkDefaultCustomAlarm(type)
            if (type == "timeEvent") {
                EventTimePicker(
                    initialDay = existAlarm?.day ?: 1,
                    initialHour = existAlarm?.hour ?: 1,
                    initialMinute = existAlarm?.minute ?: 0,
                    onDismiss = { coroutineScope.launch { sheetState.hide() }
                }, onConfirm = { day, hour, minute ->
                    val data = mutableMapOf<String, String>()
                    data["daysBefore"] = day.toString()
                    data["time"] = "$hour:$minute"
                    alarmManager.setCustomAlarm(data, EVENT_CUSTOM_ALARM)
                    alarmManager.defaultEventAlarmOffset = Long.MIN_VALUE
                    updateUserPrefs()
                    selectedIndex = list.size - 1
                    coroutineScope.launch { sheetState.hide() }
                })
            } else {
                AllDayTimePicker(
                    initialDay = existAlarm?.day ?: 1,
                    initialHour = existAlarm?.hour ?: 1,
                    initialMinute = existAlarm?.minute ?: 0,
                    isAfterNoon = (existAlarm?.period ?: "오전") == "오후",
                    onDismiss = { coroutineScope.launch { sheetState.hide() }
                }, onConfirm = { day, hour, minute, period ->
                    val data = mutableMapOf<String, String>()
                    data["daysBefore"] = day.toString()
                    data["time"] = "$hour:$minute ${if (period == "오전") "AM" else "PM"}"
                    when (type) {
                        "interval" -> {
                            alarmManager.setCustomAlarm(data, PLAN_CUSTOM_ALARM)
                            alarmManager.defaultPlanAlarmTime = Long.MIN_VALUE
                        }
                        else -> {
                            alarmManager.setCustomAlarm(data, All_DAY_CUSTOM_ALARM)
                            alarmManager.defaultAlldayEventAlarmOffset = Long.MIN_VALUE
                        }
                    }
                    updateUserPrefs()
                    selectedIndex = list.size - 1
                    coroutineScope.launch { sheetState.hide() }
                })
            }
        }, sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) {
            Scaffold(topBar = {
                TopAppBar(modifier = Modifier.padding(start = 12.dp),
                    title = {
                        Text(
                            text = title,
                            lineHeight = dpToSp(dp = 6.dp),
                            style = applyFont(font = AppFont.mainBold, baseFontSize = 18.dp),
                            fontWeight = FontWeight.Bold,
                        )
                    }, navigationIcon = {
                        IconButton(modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp),
                            onClick = { finish() }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.arrow_left_large),
                                contentDescription = null
                            )
                        }
                    }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }, content = { paddingValue ->
                AlarmTimeSelector(paddingValue, list, selectedIndex) { position ->
                    when (position) {
                        0 -> { //없음
                            selectedIndex = 0
                            when (type) {
                                "interval" -> alarmManager.defaultPlanAlarmTime = Long.MIN_VALUE
                                "allDayEvent" -> alarmManager.defaultAlldayEventAlarmOffset = Long.MIN_VALUE
                                else -> alarmManager.defaultEventAlarmOffset = Long.MIN_VALUE
                            }
                        }
                        list.size - 1 -> { //커스텀 기본 알람 설정
                            coroutineScope.launch { sheetState.show() }
                        }
                        else -> { //지정 기본 알람 설정
                            selectedIndex = position
                            when (type) {
                                "interval" -> alarmManager.defaultPlanAlarmTime =
                                    Alarm.spinnerIndexToOffset(true, position - 1)
                                "allDayEvent" -> alarmManager.defaultAlldayEventAlarmOffset =
                                    Alarm.spinnerIndexToOffset(false, position - 1)
                                else -> alarmManager.defaultEventAlarmOffset =
                                    Alarm.spinnerIndexToOffset(true, position - 1)
                            }
                        }
                    }
                }
            })
        }
    }

    @Composable
    fun AlarmTimeSelector(
        paddingValues: PaddingValues,
        options: Array<String>,
        selectedIndex: Int,
        onOptionSelected: (Int) -> Unit
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            itemsIndexed(options) { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable { onOptionSelected(index) }
                        .padding(vertical = 12.dp, horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        modifier = Modifier.weight(1f),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = dpToSp(dp = 14.dp) * AppFont.appFontRatio,
                            fontFamily = AppFont.mainRegular?.let { FontFamily(it) },
                        )
                    )
                    if (index == selectedIndex) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = colorResource(R.color.colorPrimary)
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @Composable
    private fun AllDayTimePicker(
        initialDay: Int,
        initialHour: Int,
        initialMinute: Int,
        isAfterNoon: Boolean,
        onDismiss: () -> Unit,
        onConfirm: (Int, Int, Int, String) -> Unit
    ) {
        val days = (1..199).map { "${it}일" }
        val hours = (1..12).map { "$it" }
        val minutes = (0..59).map { String.format("%02d", it) }
        val amPm = listOf("오전", "오후")
        var selectedDay by remember { mutableIntStateOf(initialDay) }
        var selectedMinute by remember { mutableIntStateOf(initialMinute) }
        var selectedHour by remember { mutableIntStateOf(initialHour) }
        var selectedPeriod by remember { mutableStateOf(if (isAfterNoon) "오후" else "오전") }
        val coroutineScope = rememberCoroutineScope()
        val pickerState = rememberPickerState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "사용자화",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = dpToSp(dp = 16.dp) * AppFont.appFontRatio,
                        fontFamily = AppFont.mainBold?.let { FontFamily(it) },
                    ),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (selectedDay != 0) "${selectedDay}일 전 $selectedPeriod " +
                                "${selectedHour}시 ${selectedMinute}분"
                           else "당일 $selectedPeriod " + "${selectedHour}시 ${selectedMinute}분",
                    style = TextStyle(
                        color = colorResource(R.color.subtler),
                        fontSize = dpToSp(dp = 16.dp) * AppFont.appFontRatio,
                        fontFamily = AppFont.mainRegular?.let { FontFamily(it) },
                    ),
                    color = Color.Gray,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(
                color = colorResource(R.color.defaultDivider),
                thickness = 1.dp,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberPicker(
                    isDatePicker = true,
                    list = days,
                    selectedValue = if (selectedDay == 0) "당일" else "${selectedDay}일",
                    onValueChange = {
                        selectedDay = if (it == "당일") 0 else it.removeSuffix("일").toInt()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
                val listState = rememberLazyListState(initialFirstVisibleItemIndex = amPm.indexOf(selectedPeriod))
                NumberPicker(
                    pickerState = pickerState,
                    lazyListState = listState,
                    list = amPm,
                    selectedValue = selectedPeriod,
                    onValueChange = { selectedPeriod = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
                NumberPicker(
                    list = hours,
                    selectedValue = selectedHour.toString(),
                    onValueChange = {
                        selectedHour = it.toInt()
                        if (selectedHour == 12) {
                            coroutineScope.launch { listState.animateScrollToItem(0) }
                            pickerState.selectedItem = "오전"
                            selectedPeriod = "오전"
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
                Text(
                    ":", style = TextStyle(
                        color = colorResource(R.color.subtler),
                        fontSize = dpToSp(dp = 16.dp) * AppFont.appFontRatio,
                        fontFamily = AppFont.mainRegular?.let { FontFamily(it) },
                    )
                )
                NumberPicker(
                    list = minutes,
                    selectedValue = String.format("%02d", selectedMinute),
                    onValueChange = { selectedMinute = it.toInt() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        "취소", style = TextStyle(
                            color = colorResource(R.color.subtler),
                            fontSize = dpToSp(dp = 16.dp) * AppFont.appFontRatio,
                            fontFamily = AppFont.mainRegular?.let { FontFamily(it) },
                        )
                    )
                }
                TextButton(onClick = {
                    onConfirm(selectedDay, selectedHour, selectedMinute, selectedPeriod)
                }) {
                    Text("확인", style = TextStyle(
                        color = colorResource(R.color.colorPrimary),
                        fontSize = dpToSp(dp = 16.dp) * AppFont.appFontRatio,
                        fontFamily = AppFont.mainRegular?.let { FontFamily(it) },
                    ))
                }
            }
        }
    }

    @Composable
    private fun EventTimePicker(
        initialDay: Int,
        initialHour: Int,
        initialMinute: Int,
        onDismiss: () -> Unit,
        onConfirm: (Int, Int, Int) -> Unit
    ) {
        val days = (1..199).map { "${it}일" }
        val hours = (1..23).map { "${it}시간" }
        val minutes = (1..59).map { "${it}분" }

        var selectedDay by remember { mutableIntStateOf(initialDay) }
        var selectedMinute by remember { mutableIntStateOf(initialMinute) }
        var selectedHour by remember { mutableIntStateOf(initialHour) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "사용자화",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = dpToSp(dp = 16.dp) * AppFont.appFontRatio,
                        fontFamily = AppFont.mainBold?.let { FontFamily(it) },
                    ),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (selectedDay != 0) "${selectedDay}일 ${selectedHour}시간 ${selectedMinute}분 전"
                    else "당일 ${selectedHour}시간 ${selectedMinute}분 전",
                    style = TextStyle(
                        color = colorResource(R.color.subtler),
                        fontSize = dpToSp(dp = 16.dp) * AppFont.appFontRatio,
                        fontFamily = AppFont.mainRegular?.let { FontFamily(it) },
                    ),
                    color = Color.Gray,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(
                color = colorResource(R.color.defaultDivider),
                thickness = 1.dp,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberPicker(
                    isDatePicker = true,
                    list = days,
                    selectedValue = selectedDay.toString(),
                    onValueChange = {
                        selectedDay = if (it == "당일") 0 else it.removeSuffix("일").toInt()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
                NumberPicker(
                    list = hours,
                    selectedValue = selectedHour.toString(),
                    onValueChange = { selectedHour = it.removeSuffix("시간").toInt() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
                Text(
                    ":", style = TextStyle(
                        color = colorResource(R.color.subtler),
                        fontSize = dpToSp(dp = 16.dp) * AppFont.appFontRatio,
                        fontFamily = AppFont.mainRegular?.let { FontFamily(it) },
                    )
                )
                NumberPicker(
                    list = minutes,
                    selectedValue = selectedMinute.toString(),
                    onValueChange = { selectedMinute = it.removeSuffix("분").toInt() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        "취소", style = TextStyle(
                            color = colorResource(R.color.subtler),
                            fontSize = dpToSp(dp = 16.dp) * AppFont.appFontRatio,
                            fontFamily = AppFont.mainRegular?.let { FontFamily(it) },
                        )
                    )
                }
                TextButton(onClick = {
                    onConfirm(selectedDay, selectedHour, selectedMinute)
                }) {
                    Text("확인", style = TextStyle(
                        color = colorResource(R.color.colorPrimary),
                        fontSize = dpToSp(dp = 16.dp) * AppFont.appFontRatio,
                        fontFamily = AppFont.mainRegular?.let { FontFamily(it) },
                    ))
                }
            }
        }
    }
}


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.day2life.timeblocks.R
import com.day2life.timeblocks.application.AppFont
import com.day2life.timeblocks.util.dpToSp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun rememberPickerState() = remember { PickerState() }

class PickerState {
    var selectedItem by mutableStateOf("")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberPicker(
    list: List<String>,
    selectedValue: String,
    isDatePicker: Boolean = false,
    onValueChange: (String) -> Unit,
    pickerState: PickerState = rememberPickerState(),
    lazyListState: LazyListState? = null,
    modifier: Modifier = Modifier,
    visibleItemsCount: Int = 3,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        color = colorResource(R.color.subtler),
        fontSize = dpToSp(dp = 16.dp) * AppFont.appFontRatio
    ),
    dividerColor: Color = colorResource(R.color.colorPrimary),
) {
    val items = mutableListOf<String>()
    items.add("")
    if (isDatePicker) items.add("당일")
    items.addAll(list)
    items.add("")
    val visibleItemsMiddle = visibleItemsCount / 2
    val listState = lazyListState ?: rememberLazyListState(initialFirstVisibleItemIndex = items.indexOf(selectedValue))
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val itemHeightPixels = remember { mutableIntStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels.intValue)

    fun getItem(index: Int): String = items.getOrNull(index)?.takeIf { it.isNotEmpty() } ?: ""

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.5f to Color.Black,
            1f to Color.Transparent
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> getItem(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item ->
                pickerState.selectedItem = item
                onValueChange(item)
            }
    }

    Box(
        modifier = modifier.width(67.dp)
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient)
        ) {
            items(items.size) { index ->
                Text(
                    text = items[index],
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle,
                    modifier = Modifier
                        .onSizeChanged { size -> itemHeightPixels.intValue = size.height }
                        .then(textModifier)
                        .padding(vertical = 20.dp)
                )
            }
        }
        Divider(
            thickness = 1.5.dp,
            color = dividerColor,
            modifier = Modifier.offset(y = itemHeightDp * ((visibleItemsCount / 2)))
        )
        Divider(
            thickness = 1.5.dp,
            color = dividerColor,
            modifier = Modifier.offset(y = itemHeightDp * ((visibleItemsCount / 2) + 1))
        )
    }
}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }
