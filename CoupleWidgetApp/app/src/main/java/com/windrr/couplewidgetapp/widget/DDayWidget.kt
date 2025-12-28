package com.windrr.couplewidgetapp.widget

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.windrr.couplewidgetapp.R
import com.windrr.couplewidgetapp.dday.dataStore
import com.windrr.couplewidgetapp.dday.getStartDateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
        val startDateMillis = getStartDateFlow(context).first()

        val WIDGET_COLOR_KEY = intPreferencesKey("widget_color")
        val savedColorInt = context.dataStore.data
            .map { preferences ->
                preferences[WIDGET_COLOR_KEY] ?: android.graphics.Color.WHITE
            }
            .first()

        val widgetColor = Color(savedColorInt)

        val dDayString = if (startDateMillis != null) {
            val dDayCount = calculateDDay(startDateMillis)
            "$dDayCount"
        } else {
            "-"
        }

        provideContent {
            GlanceTheme {
                DDayWidgetContent(dDayString = dDayString, textColor = widgetColor)
            }
        }
    }

    @Composable
    private fun DDayWidgetContent(dDayString: String, textColor: Color) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                provider = ImageProvider(R.drawable.heart),
                contentDescription = "Heart Icon",
                modifier = GlanceModifier.size(16.dp),
                colorFilter = ColorFilter.tint(ColorProvider(textColor))
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = dDayString,
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
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
        suspend fun updateAllWidgets(context: Context) {
            GlanceAppWidgetManager(context)
                .getGlanceIds(DDayGlanceWidget::class.java)
                .forEach { glanceId ->
                    DDayGlanceWidget().update(context, glanceId)
                }
        }
    }
}