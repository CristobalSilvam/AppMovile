package com.example.appmovile.domain.repositories
import com.example.appmovile.data.remote.dto.UserDto // O tu modelo de dominio si prefieres mapear

interface UserRepository {
    suspend fun getAllUsers(): List<UserDto>
    suspend fun deleteUser(userIdToDelete: Long)
}