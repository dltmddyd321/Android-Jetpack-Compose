package com.windrr.couplewidgetapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import java.util.Calendar

class DDayGlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    // 이 리시버가 관리할 위젯 클래스 지정
    override val glanceAppWidget: GlanceAppWidget = DDayGlanceWidget()

    /**
     * 위젯이 처음 생성될 때 (onEnabled) 또는 재부팅 시 (BOOT_COMPLETED)
     * 자정 알람을 스케줄합니다.
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action
        // 1. 재부팅이 완료되었을 때
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleNextMidnightUpdate(context)
            return
        }

        // 2. 우리가 설정한 자정 알람이 울렸을 때
        if (action == ACTION_MIDNIGHT_UPDATE) {
            // (1) 위젯을 업데이트
            DDayGlanceWidget.updateAllWidgets(context)
            // (2) *다음 날* 자정 알람을 다시 스케줄
            scheduleNextMidnightUpdate(context)
            return
        }

        // 3. (보너스) 날짜나 시간대가 수동으로 변경되었을 때 (이건 작동 안 할 수 있음)
        if (action == Intent.ACTION_DATE_CHANGED || action == Intent.ACTION_TIMEZONE_CHANGED) {
            DDayGlanceWidget.updateAllWidgets(context)
        }
    }

    /**
     * 이 타입의 첫 번째 위젯이 추가될 때 호출됩니다.
     * 여기서 첫 자정 알람을 스케줄합니다.
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleNextMidnightUpdate(context)
    }

    /**
     * 이 타입의 마지막 위젯이 삭제될 때 호출됩니다.
     * 여기서 예약된 알람을 취소합니다.
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelMidnightUpdate(context)
    }

    companion object {
        private const val ACTION_MIDNIGHT_UPDATE =
            "com.example.couplewidgetapp.ACTION_MIDNIGHT_UPDATE"
        private const val REQUEST_CODE_MIDNIGHT_UPDATE = 1001

        /**
         * 다음 날 자정 0시 0분에 알람을 스케줄합니다.
         */
        fun scheduleNextMidnightUpdate(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = createMidnightPendingIntent(context)

            // 다음 날 자정 시간 계산
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
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
        }

        /**
         * 스케줄된 자정 알람을 취소합니다.
         */
        fun cancelMidnightUpdate(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = createMidnightPendingIntent(context)
            alarmManager.cancel(pendingIntent)
        }

        /**
         * 알람 매니저가 사용할 PendingIntent를 생성합니다.
         */
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