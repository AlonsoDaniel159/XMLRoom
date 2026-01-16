package com.alonso.xmlroom.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {

    // UserPreferences.kt
    private val dataStore = context.dataStore

    // Guardar ID del usuario
    suspend fun saveUserId(userId: Long) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    // Obtener ID del usuario
    suspend fun getUserId(): Long? {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }.first()
    }

    // Borrar sesión (logout)
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
        }
    }

    companion object {
        private val Context.dataStore by preferencesDataStore("user_prefs")
        private val USER_ID_KEY = longPreferencesKey("user_id")
    }

}