package com.windrr.couplewidgetapp.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.windrr.couplewidgetapp.widget.DDayGlanceWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class DDayGlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = DDayGlanceWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action

        // 1. 재부팅 완료 시
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleNextMidnightUpdate(context)
            return
        }

        // 2. 자정 알람이 울렸을 때 OR 날짜/시간 수동 변경 시
        if (action == ACTION_MIDNIGHT_UPDATE ||
            action == Intent.ACTION_DATE_CHANGED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            // [핵심 수정] goAsync()를 사용하여 리시버가 비동기 작업 동안 죽지 않게 함
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // 위젯 강제 업데이트
                    DDayGlanceWidget.updateAllWidgets(context)

                    // 자정 업데이트인 경우 다음 날 알람 재예약
                    if (action == ACTION_MIDNIGHT_UPDATE) {
                        scheduleNextMidnightUpdate(context)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    // 작업이 끝나면 반드시 finish()를 호출해야 함
                    pendingResult.finish()
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleNextMidnightUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelMidnightUpdate(context)
    }

    companion object {
        // [중요] 패키지명을 포함한 고유한 액션명 사용
        const val ACTION_MIDNIGHT_UPDATE = "com.example.couplewidgetapp.ACTION_MIDNIGHT_UPDATE"
        private const val REQUEST_CODE_MIDNIGHT_UPDATE = 1001

        fun scheduleNextMidnightUpdate(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = createMidnightPendingIntent(context)

            // 이미 예약된 것이 있으면 취소하고 다시 잡음 (중복 방지)
            alarmManager.cancel(pendingIntent)

            // 다음 날 자정 0시 0분 1초 계산 (00:00:00은 가끔 전날로 인식될 수 있어 1초 여유 둠)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 1)
                set(Calendar.MILLISECOND, 0)
            }

            // 정확한 시간에 깨우기 (Doze 모드에서도 동작)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    } else {
                        // 권한 없으면 일반 정확한 알람 시도
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

        fun cancelMidnightUpdate(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = createMidnightPendingIntent(context)
            alarmManager.cancel(pendingIntent)
        }

        private fun createMidnightPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, DDayGlanceWidgetReceiver::class.java).apply {
                action = ACTION_MIDNIGHT_UPDATE
            }
            return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_MIDNIGHT_UPDATE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}