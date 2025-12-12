package com.example.appmovile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.use_cases.UpdateTaskStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditTaskViewModel(
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : ViewModel() {

    private val _taskState = MutableStateFlow<Task?>(null)
    val taskState: StateFlow<Task?> = _taskState.asStateFlow()

    fun setTask(task: Task) {
        _taskState.value = task
    }

    fun saveTask(task: Task) {
        viewModelScope.launch {
            updateTaskStatusUseCase(task, task.isCompleted)
        }
    }
}

class EditTaskViewModelFactory(
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditTaskViewModel::class.java)) {
            return EditTaskViewModel(updateTaskStatusUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}