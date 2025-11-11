package com.windrr.couplewidgetapp

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class DDayGlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    // 이 리시버가 어떤 GlanceAppWidget을 관리할지 지정합니다.
    override val glanceAppWidget: GlanceAppWidget = DDayGlanceWidget()

    /**
     * 표준 위젯 업데이트 외의 브로드캐스트를 수신합니다.
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent) // Glance의 기본 로직 처리

        // [핵심] 날짜가 변경되었거나(자정) 시간대가 변경되었을 때
        if (intent.action == Intent.ACTION_DATE_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            // DDayGlanceWidget의 companion object 함수를 호출하여
            // 모든 D-Day 위젯을 즉시 업데이트하도록 합니다.
            DDayGlanceWidget.updateAllWidgets(context)
        }
    }
}