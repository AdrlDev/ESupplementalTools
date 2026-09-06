package com.esupplemental.domain.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    data class Session(val userId: String, val token: String)
    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val DARK_MODE = stringPreferencesKey("dark_mode") // "system", "dark", "light"
        val THEME_COLOR = stringPreferencesKey("theme_color") // "navy", "teal", "purple", "emerald", "rose", "gold"
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
    }

    val userId: Flow<String?> = context.dataStore.data.map { it[USER_ID] }
    val session: Flow<Session?> = combine(
        userId,
        context.dataStore.data.map { it[AUTH_TOKEN] }
    ) { userId, token ->
        if (userId.isNullOrBlank() || token.isNullOrBlank()) null else Session(userId, token)
    }

    // Dark mode: "system", "dark", "light"
    val darkModeSetting: Flow<String> = context.dataStore.data.map { it[DARK_MODE] ?: "system" }

    // Theme color: "navy", "teal", "purple", "emerald", "rose", "gold"
    val themeColorSetting: Flow<String> = context.dataStore.data.map { it[THEME_COLOR] ?: "navy" }

    // Sound effects
    val soundEffectsSetting: Flow<Boolean> = context.dataStore.data.map { it[SOUND_EFFECTS] ?: true }

    suspend fun saveSession(userId: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[AUTH_TOKEN] = token
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun setDarkModeSetting(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE] = mode
        }
    }

    suspend fun setThemeColorSetting(colorKey: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_COLOR] = colorKey
        }
    }

    suspend fun setSoundEffectsSetting(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SOUND_EFFECTS] = enabled
        }
    }
}
