package com.windrr.couplewidgetapp.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.windrr.couplewidgetapp.R
import com.windrr.couplewidgetapp.activity.MainActivity
import com.windrr.couplewidgetapp.anniversary.AnniversaryItem
import com.windrr.couplewidgetapp.anniversary.AppDatabase

class AnniversaryListWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getDatabase(context)
        val anniversaries = db.anniversaryDao().getAll()

        provideContent {
            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(ColorProvider(Color(0xFFFFF5F5)))
                        .clickable(actionStartActivity<MainActivity>())
                ) {
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            Image(
                                provider = ImageProvider(R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                modifier = GlanceModifier.size(24.dp)
                            )
                            Spacer(modifier = GlanceModifier.width(8.dp))
                            Text(
                                text = "우리의 기념일",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF5D4037)),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        }

                        if (anniversaries.isEmpty()) {
                            Box(
                                modifier = GlanceModifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "등록된 기념일이 없어요",
                                    style = TextStyle(color = ColorProvider(Color.Gray))
                                )
                            }
                        } else {
                            LazyColumn {
                                items(anniversaries) { item ->
                                    AnniversaryItemRow(item)
                                    Spacer(modifier = GlanceModifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AnniversaryItemRow(item: AnniversaryItem) {
        val dDay = getDDayCount(item.dateMillis)
        val dDayString = when {
            dDay == 0L -> "Today"
            dDay > 0 -> "D-${dDay}"
            else -> "D+${-dDay}"
        }
        val isDDayUpcoming = dDay >= 0

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(ColorProvider(Color.White))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = item.title,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF5D4037)),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = formatDate(item.dateMillis),
                    style = TextStyle(
                        color = ColorProvider(Color.Gray),
                        fontSize = 12.sp
                    )
                )
            }

            Text(
                text = dDayString,
                style = TextStyle(
                    color = ColorProvider(if (isDDayUpcoming) Color(0xFFFF8FAB) else Color.Gray),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
        }
    }

    private fun getDDayCount(targetMillis: Long): Long {
        val today = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0); set(
            java.util.Calendar.MINUTE,
            0
        ); set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0)
        }
        val target = java.util.Calendar.getInstance().apply {
            timeInMillis = targetMillis
            set(java.util.Calendar.HOUR_OF_DAY, 0); set(
            java.util.Calendar.MINUTE,
            0
        ); set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0)
        }
        val diff = target.timeInMillis - today.timeInMillis
        return diff / (24 * 60 * 60 * 1000)
    }

    private fun formatDate(millis: Long): String {
        val formatter = java.text.SimpleDateFormat("MM월 dd일", java.util.Locale.KOREA)
        return formatter.format(java.util.Date(millis))
    }
}