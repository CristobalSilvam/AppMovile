package com.example.appmovile.data.repositories

import com.example.appmovile.data.local.UserPreferencesRepository
import com.example.appmovile.data.remote.UserApiService
import com.example.appmovile.data.remote.dto.UserDto
import com.example.appmovile.domain.repositories.UserRepository
import kotlinx.coroutines.flow.first

class UserRepositoryImpl(
    private val api: UserApiService,
    private val preferences: UserPreferencesRepository
) : UserRepository {

    // Helper para obtener MI propio ID (el del Admin logueado)
    private suspend fun getMyId(): Long {
        return preferences.userSession.first()?.id
            ?: throw Exception("No estás logueado")
    }

    override suspend fun getAllUsers(): List<UserDto> {
        val adminId = getMyId()
        return api.getAllUsers(adminId)
    }

    override suspend fun deleteUser(userIdToDelete: Long) {
        val adminId = getMyId()
        api.deleteUser(userIdToDelete, adminId)
    }
}