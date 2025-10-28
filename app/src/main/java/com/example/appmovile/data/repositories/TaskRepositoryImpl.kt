package com.example.appmovile.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.appmovile.data.local.TaskDao
import com.example.appmovile.data.local.toDomain
import com.example.appmovile.data.local.toEntity
import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.repositories.TaskRepository

class TaskRepositoryImpl(
    private val taskDao: TaskDao // Inyecta el DAO de Room
) : TaskRepository {
    //Todas las tareas
    override fun getAllTasks(): Flow<List<Task>> {
        // Mapea el Flow<List<TaskEntity>> a Flow<List<Task>>
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    //guardar tarea
    override suspend fun saveTask(task: Task) {
        taskDao.insertTask(task.toEntity()) // Mapea y guarda la Entidad
    }
    //borrar tarea
    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }
    //trae una tarea
    override suspend fun getTaskById(id: Int): Task? {
        // Llama al DAO y usa el mapper para convertir TaskEntity a Task
        return taskDao.getTaskById(id)?.toDomain()
    }
}