package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.repositories.TaskRepository

class UpdateTaskStatusUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task, isCompleted: Boolean) {
        repository.updateTaskStatus(task, isCompleted)
    }
}