package com.example.appmovile.data.remote.dto

data class UserDto(
    val id: Long,
    val email: String,
    val role: String
)

// Estructura que espera tu Backend (AddMemberRequest)
data class AddMemberRequest(
    val leaderId: Long,
    val memberEmail: String,
    val groupName: String = "Mi Equipo Principal"
)
