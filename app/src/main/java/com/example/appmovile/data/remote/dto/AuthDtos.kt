package com.example.appmovile.data.remote.dto

/*
 * DTO (Data Transfer Object) que la app Android ENVIARÁ
 * al endpoint: POST /api/auth/register
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val role: String = "user" // Rol predeterminado
)

/*
 * DTO que la app Android ENVIARÁ
 * al endpoint: POST /api/auth/login
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/*
 * DTO que la app Android RECIBIRÁ
 * del endpoint: POST /api/auth/login (si el login es exitoso)
 */
data class AuthResponse(
    val id: Long,
    val email: String,
    val token: String, // El token JWT simulado
    val role: String
)