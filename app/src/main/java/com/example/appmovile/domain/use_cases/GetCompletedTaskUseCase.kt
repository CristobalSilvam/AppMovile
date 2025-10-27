package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.repositories.TaskRepository
import com.example.appmovile.domain.models.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCompletedTasksUseCase(private val repository: TaskRepository) {
    //todas las tareas y las filtra para devolver solo las completadas.
    operator fun invoke(): Flow<List<Task>> {
        return repository.getAllTasks().map { tasks ->
            tasks.filter { it.isCompleted }
        }
    }
}