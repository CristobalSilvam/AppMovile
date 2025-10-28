package com.example.appmovile.domain.repositories

import com.example.appmovile.domain.models.User

interface AuthRepository {
    // Registra un nuevo usuario
    suspend fun register(email: String, passwordHash: String)

    // Intenta iniciar sesión con un usuario
    suspend fun login(email: String, passwordHash: String): User? // Devuelve User si tiene éxito, null si falla

    // Verifica si hay un usuario actualmente conectado (simulación simple)
    suspend fun getCurrentUserEmail(): String?

    // Cierra la sesión del usuario actual (simulación simple)
    suspend fun logout()
}