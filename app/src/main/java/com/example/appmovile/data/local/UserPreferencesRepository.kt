package com.example.appmovile.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Esta extensión crea la base de datos pequeña llamada "user_prefs"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    // Definimos las "Llaves" (como los nombres de las columnas en una tabla)
    companion object {
        val USER_ID = longPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    // --- GUARDAR SESIÓN ---
    suspend fun saveUserSession(id: Long, email: String, token: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = id
            preferences[USER_EMAIL] = email
            preferences[AUTH_TOKEN] = token
            preferences[USER_ROLE] = role
        }
        Log.d("UserPrefs", "Datos guardados: ID=$id, Email=$email")
    }

    // --- LEER SESIÓN (Devuelve un objeto UserSession simple) ---
    val userSession: Flow<UserSessionData?> = context.dataStore.data
        .map { preferences ->
            val id = preferences[USER_ID]
            val email = preferences[USER_EMAIL]
            val token = preferences[AUTH_TOKEN]
            val role = preferences[USER_ROLE] ?: "USER"

            if (id != null && email != null && token != null) {
                UserSessionData(id, email, token, role)
            } else {
                null // No hay sesión guardada
            }
        }

    // --- CERRAR SESIÓN ---
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
        Log.d("UserPrefs", "Sesión borrada")
    }
}

// Clase auxiliar simple para transportar los datos
data class UserSessionData(val id: Long, val email: String, val token: String, val role: String)