package com.windrr.couplewidgetapp.anniversary

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.core.app.NotificationCompat
import com.windrr.couplewidgetapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class AnniversaryNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        // 1. 알림 체크 알람이 울렸거나, 재부팅 되었을 때
        if (action == ACTION_DAILY_NOTIFICATION_CHECK || action == Intent.ACTION_BOOT_COMPLETED) {

            // 비동기 작업을 위해 goAsync 사용
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // (1) DB에서 모든 기념일 가져오기
                    val db = AppDatabase.getDatabase(context)
                    val anniversaries = db.anniversaryDao().getAll()

                    // (2) 조건에 맞는 기념일 찾아서 알림 보내기
                    anniversaries.forEach { item ->
                        checkAndSendNotification(context, item)
                    }

                    // (3) 내일 오전 9시에 다시 예약 (무한 반복)
                    if (action == ACTION_DAILY_NOTIFICATION_CHECK) {
                        scheduleNextMorningNotification(context)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }

            // 재부팅 시에는 예약만 하고 종료
            if (action == Intent.ACTION_BOOT_COMPLETED) {
                scheduleNextMorningNotification(context)
            }
        }
    }

    private fun getLocalizedContext(context: Context): Context {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val langCode = prefs.getString("language", Locale.getDefault().language)
            ?: Locale.getDefault().language
        val locale = Locale(langCode)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    private fun checkAndSendNotification(context: Context, item: AnniversaryItem) {
        val localizedContext = getLocalizedContext(context)

        // 다음 기념일 날짜 계산 (반복 기념일 고려)
        val nextDateMillis = if (item.dateCount == 0) {
            calculateNextAnniversaryDate(item.dateMillis)
        } else {
            item.dateMillis
        }

        // D-Day 계산
        val dDay = getDDayCount(nextDateMillis)

        // 조건 체크 (D-7, D-1, 당일)
        when (dDay) {
            7L -> sendNotification(
                localizedContext,
                item.id,
                localizedContext.getString(R.string.noti_title_d7),
                localizedContext.getString(R.string.noti_desc_d7, item.title)
            )

            1L -> sendNotification(
                localizedContext,
                item.id,
                localizedContext.getString(R.string.noti_title_d1),
                localizedContext.getString(R.string.noti_desc_d1, item.title)
            )

            0L -> sendNotification(
                localizedContext,
                item.id,
                localizedContext.getString(R.string.noti_title_d_day),
                localizedContext.getString(R.string.noti_desc_d_day, item.title)
            )
        }
    }

    private fun sendNotification(context: Context, id: Int, title: String, content: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "anniversary_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.noti_channel_name), // "기념일 알림"
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.noti_channel_desc) // "기념일 D-7, D-1, 당일 알림을 보냅니다."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // ID를 다르게 주어 여러 알림이 동시에 뜰 수 있게 함
        notificationManager.notify(id, notification)
    }

    private fun calculateNextAnniversaryDate(selectedMillis: Long): Long {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(
            Calendar.MILLISECOND,
            0
        )
        }
        val selected = Calendar.getInstance().apply { timeInMillis = selectedMillis }
        val target = Calendar.getInstance().apply {
            set(Calendar.YEAR, today.get(Calendar.YEAR))
            set(Calendar.MONTH, selected.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, selected.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(
            Calendar.MILLISECOND,
            0
        )
        }
        if (target.before(today)) {
            target.add(Calendar.YEAR, 1)
        }
        return target.timeInMillis
    }

    private fun getDDayCount(targetMillis: Long): Long {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(
            Calendar.MILLISECOND,
            0
        )
        }
        val target = Calendar.getInstance().apply {
            timeInMillis = targetMillis
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(
            Calendar.MILLISECOND,
            0
        )
        }
        val diff = target.timeInMillis - today.timeInMillis
        return diff / (24 * 60 * 60 * 1000)
    }

    companion object {
        const val ACTION_DAILY_NOTIFICATION_CHECK =
            "com.windrr.couplewidgetapp.ACTION_DAILY_NOTIFICATION_CHECK"
        private const val REQUEST_CODE_NOTIFICATION = 3000

        // 매일 오전 9시에 실행되도록 예약
        fun scheduleNextMorningNotification(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AnniversaryNotificationReceiver::class.java).apply {
                action = ACTION_DAILY_NOTIFICATION_CHECK
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_CODE_NOTIFICATION, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 내일 오전 9시 계산
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 9) // 오전 9시
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // 만약 이미 9시가 지났다면 내일 9시로 설정
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            try {
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}