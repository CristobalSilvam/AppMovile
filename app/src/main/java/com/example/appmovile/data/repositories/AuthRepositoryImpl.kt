package com.example.appmovile.data.repositories

import android.util.Log
import com.example.appmovile.data.local.UserPreferencesRepository
import com.example.appmovile.data.remote.AuthApiService
import com.example.appmovile.data.remote.dto.LoginRequest
import com.example.appmovile.data.remote.dto.RegisterRequest
import com.example.appmovile.domain.models.User
import com.example.appmovile.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val preferences: UserPreferencesRepository
) : AuthRepository {

    override suspend fun register(email: String, passwordHash: String) {
        val request = RegisterRequest(
            email = email,
            password = passwordHash,
            confirmPassword = passwordHash,
            role = "USER"
        )
        try {
            authApiService.register(request)
        } catch (e: Exception) {
            throw IllegalArgumentException("Error al registrar: ${e.message}")
        }
    }

    override suspend fun login(email: String, passwordHash: String): User? {
        val request = LoginRequest(email, passwordHash)
        return try {
            val response = authApiService.login(request)

            Log.e("DEBUG_ROL", "--------------------------------------------------")
            Log.e("DEBUG_ROL", "RESPUESTA DEL SERVIDOR:")
            Log.e("DEBUG_ROL", "Email: ${response.email}")
            Log.e("DEBUG_ROL", "Token: ${response.token.take(10)}...")
            Log.e("DEBUG_ROL", "ROL:   ${response.role}")
            Log.e("DEBUG_ROL", "--------------------------------------------------")

            preferences.saveUserSession(
                id = response.id,
                email = response.email,
                token = response.token,
                role = response.role
            )

            User(
                id = response.id.toString(),
                email = response.email,
                name = "",
                role = response.role
            )
        } catch (e: Exception) {
            Log.e("AuthRepo", "Login fallido: ${e.message}")
            null
        }
    }

    override suspend fun logout() {
        preferences.clearSession()
        Log.d("AuthRepo", "Sesión cerrada y datos borrados")
    }

    override suspend fun getCurrentUser(): User? {
        val session = preferences.userSession.firstOrNull()
        return session?.let {
            User(
                id = it.id.toString(),
                email = it.email,
                name = "",
                role = it.role
            )
        }
    }

    // Mantenemos este por compatibilidad si se usa en otro lado, aunque ya no esté en la interfaz
    suspend fun getCurrentUserEmail(): String? {
        val session = preferences.userSession.firstOrNull()
        return session?.email
    }

    suspend fun getCurrentUserId(): Long? {
        val session = preferences.userSession.firstOrNull()
        return session?.id
    }
}
