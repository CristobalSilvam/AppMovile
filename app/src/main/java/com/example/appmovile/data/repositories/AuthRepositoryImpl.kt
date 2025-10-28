package com.example.appmovile.data.repositories

import com.example.appmovile.data.local.AuthDao
import com.example.appmovile.data.local.models.UserEntity
import com.example.appmovile.domain.models.User
import com.example.appmovile.domain.repositories.AuthRepository


class AuthRepositoryImpl (
    private val authDao: AuthDao
) : AuthRepository {

    // Almacenamiento simple en memoria para la sesión del usuario actual
    private var currentUserEmail: String? = null

    override suspend fun register(email: String, passwordHash: String) {
        val userEntity = UserEntity(email = email, passwordHash = passwordHash)
        authDao.registerUser(userEntity)
        // Iniciar sesión automáticamente después del registro
        currentUserEmail = email
    }

    override suspend fun login(email: String, passwordHash: String): User? {
        val userEntity = authDao.findUserByEmail(email)
        // Verificación simple de contraseña
        return if (userEntity != null && userEntity.passwordHash == passwordHash) {
            currentUserEmail = userEntity.email // Establecer usuario actual si el login es exitoso
            User(email = userEntity.email) // Devuelve el modelo User limpio
        } else {
            currentUserEmail = null // Limpiar usuario actual si el login falla
            null // Devuelve null si falla
        }
    }

    override suspend fun getCurrentUserEmail(): String? {
        // Devuelve el email almacenado en memoria
        return currentUserEmail
    }

    override suspend fun logout() {
        // Limpia la sesión del usuario actual
        currentUserEmail = null
    }
}