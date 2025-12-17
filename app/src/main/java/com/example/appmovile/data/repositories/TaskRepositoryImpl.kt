package com.example.appmovile.data.repositories

import android.util.Log
import com.example.appmovile.data.local.UserPreferencesRepository
import com.example.appmovile.data.remote.TaskApiService
import com.example.appmovile.data.remote.dto.TaskCreateRequest
import com.example.appmovile.data.remote.dto.TaskResponse
import com.example.appmovile.data.remote.dto.TaskUpdateRequest
import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.repositories.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskRepositoryImpl(
    private val taskApiService: TaskApiService,
    private val preferences: UserPreferencesRepository
) : TaskRepository {

    private val _tasksFlow = MutableStateFlow<List<Task>>(emptyList())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                refreshTasks()
            } catch (e: Exception) {
                Log.e("TaskRepo", "No se pudieron cargar las tareas al iniciar: ${e.message}")
            }
        }
    }

    private suspend fun getCurrentUserId(): Long {
        val session = preferences.userSession.first()
        return session?.id ?: throw Exception("Usuario no autenticado.")
    }

    private suspend fun refreshTasks() {
        try {
            val userId = getCurrentUserId()
            val taskResponses = taskApiService.getAllTasks(userId)
            _tasksFlow.value = taskResponses.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("TaskRepo", "Error al refrescar tareas: ${e.message}")
        }
    }

    override fun getAllTasks(): StateFlow<List<Task>> = _tasksFlow

    override suspend fun saveTask(task: Task) {
        saveTaskForUser(getCurrentUserId(), task)
        refreshTasks()
    }

    override suspend fun updateTaskStatus(task: Task, isCompleted: Boolean) {
        updateTaskStatusForUser(getCurrentUserId(), task, isCompleted)
        refreshTasks()
    }

    override suspend fun deleteTask(taskId: Int) {
        deleteTaskForUser(getCurrentUserId(), taskId)
        refreshTasks()
    }

    override suspend fun getTaskById(id: Int): Task? {
        return _tasksFlow.value.find { it.id == id }
    }

    // --- IMPLEMENTACIÓN ADMIN ---

    override suspend fun getTasksByUserId(userId: Long): List<Task> {
        return try {
            taskApiService.getAllTasks(userId).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("TaskRepo", "Error admin getTasks: ${e.message}")
            emptyList()
        }
    }

    override suspend fun saveTaskForUser(userId: Long, task: Task) {
        val request = TaskCreateRequest(
            title = task.title,
            description = task.description,
            location = task.location,
            priority = task.priority
        )
        try {
            taskApiService.createTask(userId, request)
        } catch (e: Exception) {
            Log.e("TaskRepo", "Error admin saveTask: ${e.message}")
        }
    }

    override suspend fun deleteTaskForUser(userId: Long, taskId: Int) {
        try {
            taskApiService.deleteTask(taskId.toLong(), userId)
        } catch (e: Exception) {
            Log.e("TaskRepo", "Error admin deleteTask: ${e.message}")
        }
    }

    override suspend fun updateTaskStatusForUser(userId: Long, task: Task, isCompleted: Boolean) {
        val request = TaskUpdateRequest(
            title = task.title,
            description = task.description,
            location = task.location,
            priority = task.priority,
            isCompleted = isCompleted
        )
        try {
            taskApiService.updateTask(task.id.toLong(), userId, request)
        } catch (e: Exception) {
            Log.e("TaskRepo", "Error admin updateTask: ${e.message}")
        }
    }

    private fun TaskResponse.toDomain(): Task {
        return Task(
            id = this.id.toInt(),
            title = this.title,
            description = this.description,
            location = this.location,
            priority = this.priority,
            isCompleted = this.isCompleted,
            reminderTime = null
        )
    }
}
