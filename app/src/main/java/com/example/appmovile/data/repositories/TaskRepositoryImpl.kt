package com.example.appmovile.data.repositories

// 1. Quita imports de Room (DAO, Mappers)
// 2. Importa el servicio de API y DTOs
import com.example.appmovile.data.remote.TaskApiService
import com.example.appmovile.data.remote.dto.TaskCreateRequest
import com.example.appmovile.data.remote.dto.TaskResponse
import com.example.appmovile.data.remote.dto.TaskUpdateRequest
import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.repositories.TaskRepository
import java.lang.Exception

class TaskRepositoryImpl(
    // 3. ⬇️ Cambia la dependencia del constructor
    private val taskApiService: TaskApiService
) : TaskRepository {

    // --- Mapeador simple de DTO (Respuesta) a Modelo de Dominio ---
    private fun TaskResponse.toDomain(): Task {
        return Task(
            id = this.id.toInt(), // Cuidado con Long vs Int (ajusta tu modelo Task si es necesario)
            title = this.title,
            description = this.description,
            location = this.location,
            priority = this.priority,
            isCompleted = this.isCompleted,
            reminderTime = null // El DTO de respuesta no tiene reminderTime
        )
    }

    // --- Implementaciones de API ---

    override fun getAllTasks(): kotlinx.coroutines.flow.Flow<List<Task>> {
        // Retrofit (suspend) no devuelve un Flow directamente como Room.
        // Para una implementación simple, usamos flow { ... }
        return kotlinx.coroutines.flow.flow {
            try {
                // 4. Llama a la API
                val taskResponses = taskApiService.getAllTasks()
                // 5. Mapea la respuesta
                emit(taskResponses.map { it.toDomain() })
            } catch (e: Exception) {
                // En caso de error de red, emite lista vacía
                emit(emptyList())
            }
        }
    }

    override suspend fun saveTask(task: Task) {
        // 6. Crea el DTO de Request para la API
        val request = TaskCreateRequest(
            title = task.title,
            description = task.description,
            location = task.location,
            priority = task.priority
        )
        try {
            taskApiService.createTask(request)
        } catch (e: Exception) {
            // Manejar error
        }
    }

    override suspend fun updateTaskStatus(task: Task, isCompleted: Boolean) {
        // 7. Llama al endpoint de Actualización
        val request = TaskUpdateRequest(
            title = task.title,
            description = task.description,
            location = task.location,
            priority = task.priority,
            isCompleted = isCompleted
        )
        try {
            taskApiService.updateTask(task.id.toLong(), request)
        } catch (e: Exception) {
            // Manejar error
        }
    }

    override suspend fun deleteTask(taskId: Int) {
        try {
            taskApiService.deleteTask(taskId.toLong())
        } catch (e: Exception) {
            // Manejar error
        }
    }

    // --- Lógica de Detalle ---
    override suspend fun getTaskById(id: Int): Task? {
        // (Nota: No creamos un endpoint para esto en el back-end,
        // pero si lo hicieras, la lógica iría aquí)
        // Por ahora, simulamos buscando en la lista completa
        try {
            val tasks = taskApiService.getAllTasks()
            return tasks.find { it.id == id.toLong() }?.toDomain()
        } catch (e: Exception) {
            return null
        }
    }
}