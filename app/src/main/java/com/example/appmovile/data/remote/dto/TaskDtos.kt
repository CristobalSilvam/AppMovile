package com.example.appmovile.data.remote.dto

/*
 * DTO que la app Android ENVIARÁ
 * al endpoint: POST /api/tasks (Crear Tarea)
 */
data class TaskCreateRequest(
    val title: String,
    val description: String?,
    val location: String?,
    val priority: String
)

/*
 * DTO que la app Android ENVIARÁ
 * al endpoint: PUT /api/tasks/{id} (Actualizar Tarea)
 */
data class TaskUpdateRequest(
    val title: String,
    val description: String?,
    val location: String?,
    val priority: String,
    val isCompleted: Boolean
)

/*
 * DTO que la app Android RECIBIRÁ
 * de los endpoints: GET /api/tasks, POST /api/tasks, PUT /api/tasks/{id}
 */
data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val location: String?,
    val priority: String,
    val isCompleted: Boolean
)