package com.example.appmovile.data.repositories

import android.util.Log
import com.example.appmovile.data.local.UserPreferencesRepository
import com.example.appmovile.data.remote.GroupApiService
import com.example.appmovile.data.remote.UserApiService
import com.example.appmovile.data.remote.dto.AddMemberRequest
import com.example.appmovile.data.remote.dto.UserDto
import com.example.appmovile.domain.repositories.UserRepository
import kotlinx.coroutines.flow.first

class UserRepositoryImpl(
    private val api: UserApiService,
    private val groupApi: GroupApiService,
    private val preferences: UserPreferencesRepository
) : UserRepository {

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

    override suspend fun updateUserRole(userId: Long, newRole: String) {
        val adminId = getMyId()
        val response = api.updateUserRole(userId, newRole, adminId)
        if (!response.isSuccessful) {
            throw Exception("Error al actualizar rol: ${response.code()}")
        }
    }

    // --- IMPLEMENTACIÓN GRUPOS (Leader / Admin) ---
    
    override suspend fun addMemberByEmail(leaderId: Long, email: String) {
        val request = AddMemberRequest(leaderId = leaderId, memberEmail = email)
        val response = groupApi.addMemberByEmail(request)
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string()
            throw Exception(errorMsg ?: "Error desconocido")
        }
    }

    override suspend fun getGroupMembers(leaderId: Long): List<UserDto> {
        return groupApi.getGroupMembers(leaderId)
    }

    override suspend fun removeMemberFromGroup(leaderId: Long, memberId: Long) {
        val response = groupApi.removeMember(leaderId, memberId)
        if (!response.isSuccessful) {
            throw Exception("No se pudo remover al miembro")
        }
    }

    override suspend fun deleteGroup(leaderId: Long) {
        val response = groupApi.deleteGroup(leaderId)
        if (!response.isSuccessful) {
            throw Exception("No se pudo eliminar el grupo")
        }
    }
}
