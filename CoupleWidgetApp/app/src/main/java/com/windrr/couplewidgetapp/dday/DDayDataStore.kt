package com.windrr.couplewidgetapp.dday

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
    name = "dday_settings"
)

val START_DATE_KEY = longPreferencesKey("start_date_millis")

val START_TITLE_KEY = stringPreferencesKey("start_title")

suspend fun saveStartDate(context: Context, dateInMillis: Long) {
    context.dataStore.edit { settings ->
        settings[START_DATE_KEY] = dateInMillis
    }
}

fun getStartDateFlow(context: Context): Flow<Long?> {
    return context.dataStore.data
        .map { preferences ->
            preferences[START_DATE_KEY]
        }
}

suspend fun saveStartTitle(context: Context, title: String) {
    context.dataStore.edit { settings ->
        settings[START_TITLE_KEY] = title
    }
}

fun getStartTitle(context: Context): Flow<String> {
    return context.dataStore.data
        .map { preferences ->
            preferences[START_TITLE_KEY] ?: "우리가 사랑한 지"
        }
}