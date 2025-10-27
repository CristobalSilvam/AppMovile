package com.example.appmovile.domain.repositories

import kotlinx.coroutines.flow.Flow
import com.example.appmovile.domain.models.Task // Importa el modelo de dominio limpio

interface   TaskRepository {

    // Obtener la lista de tareas para la UI
    fun getAllTasks(): Flow<List<Task>>

    // Guardar (Insertar o Actualizar) una tarea
    suspend fun saveTask(task: Task)

    // Eliminar una tarea
    suspend fun deleteTask(taskId: Int)
}