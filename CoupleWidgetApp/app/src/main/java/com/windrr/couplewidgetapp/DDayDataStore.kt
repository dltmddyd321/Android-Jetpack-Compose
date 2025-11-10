package com.windrr.couplewidgetapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore 인스턴스를 생성합니다. (앱 전역에서 사용)
val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "dday_settings")

// 저장할 데이터의 Key를 정의합니다. (시작 날짜)
val START_DATE_KEY = longPreferencesKey("start_date_millis")

/**
 * DataStore에 시작 날짜(Long)를 저장하는 함수
 */
suspend fun saveStartDate(context: Context, dateInMillis: Long) {
    context.dataStore.edit { settings ->
        settings[START_DATE_KEY] = dateInMillis
    }
}

/**
 * DataStore에서 저장된 시작 날짜를 Flow로 불러오는 함수
 */
fun getStartDateFlow(context: Context): Flow<Long?> {
    return context.dataStore.data
        .map { preferences ->
            preferences[START_DATE_KEY]
        }
}