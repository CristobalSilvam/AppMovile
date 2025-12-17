package com.example.appmovile.data.remote

import com.example.appmovile.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {

    // GET /api/users?userId=1
    @GET("api/users")
    suspend fun getAllUsers(@Query("userId") adminId: Long): List<UserDto>

    // DELETE /api/users/{id}?userId=1
    @DELETE("api/users/{id}")
    suspend fun deleteUser(
        @Path("id") userIdToDelete: Long,
        @Query("userId") adminId: Long
    ): Response<Unit>
}