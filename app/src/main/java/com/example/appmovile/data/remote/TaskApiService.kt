package com.example.appmovile.data.remote

import com.example.appmovile.data.remote.dto.TaskCreateRequest
import com.example.appmovile.data.remote.dto.TaskResponse
import com.example.appmovile.data.remote.dto.TaskUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface TaskApiService {

    @GET("api/tasks")
    suspend fun getAllTasks(@Query("userId") userId: Long): List<TaskResponse>

    @POST("api/tasks")
    suspend fun createTask(
        @Query("userId") userId: Long,
        @Body request: TaskCreateRequest
    ): TaskResponse

    @PUT("api/tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: Long,
        @Query("userId") userId: Long,
        @Body request: TaskUpdateRequest
    ): TaskResponse

    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(
        @Path("id") taskId: Long,
        @Query("userId") userId: Long
    ): Response<Unit>
}