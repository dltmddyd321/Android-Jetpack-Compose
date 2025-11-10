package com.windrr.couplewidgetapp

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar

/**
 * Glance 위젯의 UI와 로직을 정의합니다.
 */
class DDayGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            // GlanceTheme을 사용하면 Material3 스타일을 쉽게 적용할 수 있습니다.
            GlanceTheme {
                DDayWidgetContent()
            }
        }
    }

    @Composable
    private fun DDayWidgetContent() {
        val context = LocalContext.current
        val startDateMillis by getStartDateFlow(context).collectAsState(initial = null)

        val dDayString = if (startDateMillis != null) {
            val dDayCount = calculateDDay(startDateMillis!!)
            "D+${dDayCount}"
        } else {
            "D-Day"
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "❤️ $dDayString",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }

    /**
     * D-Day 계산 (기존 로직 동일)
     */
    private fun calculateDDay(startDateMillis: Long): Long {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val startDate = Instant.ofEpochMilli(startDateMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val today = LocalDate.now(ZoneId.systemDefault())
            return ChronoUnit.DAYS.between(startDate, today) + 1
        } else {
            val startDate = Calendar.getInstance().apply {
                timeInMillis = startDateMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val diffMillis = today.timeInMillis - startDate.timeInMillis
            val diffDays = diffMillis / (1000 * 60 * 60 * 24)
            return diffDays + 1
        }
    }

    companion object {
        /**
         * MainActivity에서 위젯을 즉시 업데이트하기 위해 호출할 함수
         */
        fun updateAllWidgets(context: Context) {
            // Glance 위젯 업데이트는 비동기로 수행되어야 합니다.
            CoroutineScope(Dispatchers.IO).launch {
                DDayGlanceWidget().updateAll(context)
            }
        }
    }
}

/**
 * 이 Receiver가 AndroidManifest.xml에 등록됩니다.
 */
class DDayGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DDayGlanceWidget()
}