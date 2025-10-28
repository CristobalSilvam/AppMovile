package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.repositories.AuthRepository
import java.lang.IllegalArgumentException

class LoginUserUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Email y contraseña son obligatorios.")
        }

        val user = repository.login(email, password)
        return user != null // Devuelve true si el repositorio encontró y validó al usuario
    }
}