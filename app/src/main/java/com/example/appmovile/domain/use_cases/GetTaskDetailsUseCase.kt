package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.repositories.TaskRepository

class GetTaskDetailsUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Int): Task? {
        return repository.getTaskById(id)
    }
}