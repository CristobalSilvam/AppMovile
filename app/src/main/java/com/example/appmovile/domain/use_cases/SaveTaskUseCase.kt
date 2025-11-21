package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.repositories.TaskRepository
import java.lang.IllegalArgumentException

class SaveTaskUseCase(
    // Depende de la interfaz TaskRepository (contrato de negocio)
    private val repository: TaskRepository
) {

    suspend operator fun invoke(task: Task) {

        // LÓGICA DE VALIDACIÓN
        if (task.title.isBlank()) {
            throw IllegalArgumentException("El título de la tarea no puede estar vacío.")
        }
        if (task.priority.isBlank() || task.priority.uppercase() !in listOf("ALTA", "MEDIA", "BAJA")) {
            throw IllegalArgumentException("La prioridad no es válida.")
        }

        repository.saveTask(task)
    }
}