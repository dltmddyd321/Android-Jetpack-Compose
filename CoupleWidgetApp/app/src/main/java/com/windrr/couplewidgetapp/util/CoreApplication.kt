package com.windrr.couplewidgetapp.util

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

class CoreApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val langCode = prefs.getString("language", Locale.getDefault().language) ?: "ko"
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        applicationContext.resources.updateConfiguration(config, resources.displayMetrics)
    }
}