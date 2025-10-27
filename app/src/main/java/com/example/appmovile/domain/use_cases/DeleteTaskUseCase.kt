package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.repositories.TaskRepository

class DeleteTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(taskId: Int) {
        repository.deleteTask(taskId)
    }
}