package com.example.appmovile.data.remote

import com.example.appmovile.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {

    @GET("api/users")
    suspend fun getAllUsers(@Query("userId") adminId: Long): List<UserDto>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(
        @Path("id") userIdToDelete: Long,
        @Query("userId") adminId: Long
    ): Response<Unit>

    // NUEVO: Actualizar rol de usuario
    @PUT("api/users/{id}/role")
    suspend fun updateUserRole(
        @Path("id") userId: Long,
        @Query("role") newRole: String,
        @Query("adminId") adminId: Long
    ): Response<Unit>
}
