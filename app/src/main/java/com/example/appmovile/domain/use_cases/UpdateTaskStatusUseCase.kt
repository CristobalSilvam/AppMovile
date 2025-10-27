package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.repositories.TaskRepository
import com.example.appmovile.domain.models.Task

class UpdateTaskStatusUseCase(private val repository: TaskRepository) {

    suspend operator fun invoke(task: Task, isCompleted: Boolean) {
        val updatedTask = task.copy(isCompleted = isCompleted)
        repository.saveTask(updatedTask)
    }
}