package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.repositories.AuthRepository
import java.lang.IllegalArgumentException

class RegisterUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, confirmPassword: String) {
        // VALIDACIÓN DE CAMPOS (IL 2.1 y IL 2.2)
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            throw IllegalArgumentException("Todos los campos son obligatorios.")
        }
        if (password.length < 6) {
            throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.")
        }
        if (password != confirmPassword) {
            throw IllegalArgumentException("Las contraseñas no coinciden.")
        }
        // Validación básica de formato de email
        if (!email.contains("@") || !email.contains(".")) {
            throw IllegalArgumentException("El formato del email es incorrecto.")
        }

        // Si la validación pasa, registra al usuario
        repository.register(email, password) // Guarda la contraseña (simulación)
    }
}