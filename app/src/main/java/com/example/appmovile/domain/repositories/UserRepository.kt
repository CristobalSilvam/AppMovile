package com.example.appmovile.domain.repositories
import com.example.appmovile.data.remote.dto.UserDto

interface UserRepository {
    suspend fun getAllUsers(): List<UserDto>
    suspend fun deleteUser(userIdToDelete: Long)
    
    // Gestión de Roles (Admin)
    suspend fun updateUserRole(userId: Long, newRole: String)
    
    // Gestión de Grupos (Leader/Admin)
    suspend fun addMemberByEmail(leaderId: Long, email: String)
    suspend fun getGroupMembers(leaderId: Long): List<UserDto>
    suspend fun removeMemberFromGroup(leaderId: Long, memberId: Long)
    suspend fun deleteGroup(leaderId: Long)
}
