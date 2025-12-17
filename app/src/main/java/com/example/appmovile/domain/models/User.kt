package com.example.appmovile.domain.models

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String // <--- CAMBIO: Añadido el rol
)