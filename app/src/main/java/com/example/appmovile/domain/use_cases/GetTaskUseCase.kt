package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.repositories.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> {
        return repository.getAllTasks().map { tasks ->
            tasks.filter { !it.isCompleted } //filtra completadas
        }
    }
}