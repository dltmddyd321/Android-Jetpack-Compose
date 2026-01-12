package com.windrr.couplewidgetapp.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import java.util.Locale

class AppLanguageState {
    fun updateLocale(context: Context, langCode: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale(langCode)))
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        context.applicationContext.resources.updateConfiguration(config, context.resources.displayMetrics)

        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit { putString("language", langCode) }
    }
}