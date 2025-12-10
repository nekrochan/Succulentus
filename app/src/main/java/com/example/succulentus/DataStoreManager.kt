package com.example.succulentus

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class AppSettings(
    val notificationsEnabled: Boolean = true,
    val language: String = "ru",
    val fontSize: Int = 2,
    val backupFilename: String = "backup_01.txt"
)

class DataStoreManager(private val context: Context) {

    companion object {
        private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val FONT_SIZE_KEY = intPreferencesKey("font_size")
        private val BACKUP_FILENAME_KEY = stringPreferencesKey("backup_filename")
    }

    // Получение всех настроек одним Flow
    val appSettings: Flow<AppSettings> = context.dataStore.data
        .map { preferences ->
            AppSettings(
                notificationsEnabled = preferences[NOTIFICATIONS_KEY] ?: true,
                language = preferences[LANGUAGE_KEY] ?: "ru",
                fontSize = preferences[FONT_SIZE_KEY] ?: 2,
                backupFilename = preferences[BACKUP_FILENAME_KEY] ?: "backup_01.txt"
            )
        }

    // Сохранение всех настроек
    suspend fun saveSettings(settings: AppSettings) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_KEY] = settings.notificationsEnabled
            preferences[LANGUAGE_KEY] = settings.language
            preferences[FONT_SIZE_KEY] = settings.fontSize
            preferences[BACKUP_FILENAME_KEY] = settings.backupFilename
        }
    }
}