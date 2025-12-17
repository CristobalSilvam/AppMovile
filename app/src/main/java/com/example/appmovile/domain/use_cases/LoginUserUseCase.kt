package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.models.User
import com.example.appmovile.domain.repositories.AuthRepository
import java.lang.IllegalArgumentException

class LoginUserUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): User {
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Email y contraseña son obligatorios.")
        }

        return repository.login(email, password) ?: throw IllegalArgumentException("Credenciales incorrectas")
    }
}