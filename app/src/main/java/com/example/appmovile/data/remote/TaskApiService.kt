package com.example.appmovile.data.remote

import com.example.appmovile.data.remote.dto.TaskCreateRequest
import com.example.appmovile.data.remote.dto.TaskResponse
import com.example.appmovile.data.remote.dto.TaskUpdateRequest
import retrofit2.Response // Usamos Response para manejar códigos HTTP (ej. 204 No Content)
import retrofit2.http.*

interface TaskApiService {

    // --- GET (Leer Todas las Tareas) ---
    // GET /api/tasks
    @GET("/api/tasks")
    suspend fun getAllTasks(): List<TaskResponse>

    // --- POST (Crear Nueva Tarea) ---
    // POST /api/tasks
    @POST("/api/tasks")
    suspend fun createTask(@Body request: TaskCreateRequest): TaskResponse

    // --- PUT (Actualizar Tarea Existente) ---
    // PUT /api/tasks/{id}
    @PUT("/api/tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: Long,
        @Body request: TaskUpdateRequest
    ): TaskResponse

    // --- DELETE (Borrar Tarea) ---
    // DELETE /api/tasks/{id}
    @DELETE("/api/tasks/{id}")
    suspend fun deleteTask(@Path("id") taskId: Long): Response<Unit> // Devuelve 204 No Content
}