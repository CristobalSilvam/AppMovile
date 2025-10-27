package com.example.appmovile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appmovile.domain.use_cases.DeleteTaskUseCase
import com.example.appmovile.domain.use_cases.GetTaskUseCase
import com.example.appmovile.domain.use_cases.UpdateTaskStatusUseCase
import java.lang.IllegalArgumentException

class TaskListViewModelFactory(
    private val getTaskUseCase: GetTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Crea la instancia del ViewModel pasándole las dependencias
            return TaskListViewModel(
                getTaskUseCase,
                deleteTaskUseCase,
                updateTaskStatusUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}