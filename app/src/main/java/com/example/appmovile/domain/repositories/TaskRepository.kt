package com.example.appmovile.domain.repositories

import kotlinx.coroutines.flow.Flow
import com.example.appmovile.domain.models.Task

interface TaskRepository {

    // Obtener la lista de tareas del usuario logueado
    fun getAllTasks(): Flow<List<Task>>

    // Operaciones para el usuario logueado
    suspend fun saveTask(task: Task)
    suspend fun deleteTask(taskId: Int)
    suspend fun getTaskById(id: Int): Task?
    suspend fun updateTaskStatus(task: Task, isCompleted: Boolean)

    // --- NUEVAS FUNCIONES PARA ADMIN ---
    suspend fun getTasksByUserId(userId: Long): List<Task>
    suspend fun saveTaskForUser(userId: Long, task: Task)
    suspend fun deleteTaskForUser(userId: Long, taskId: Int)
    suspend fun updateTaskStatusForUser(userId: Long, task: Task, isCompleted: Boolean)
}
