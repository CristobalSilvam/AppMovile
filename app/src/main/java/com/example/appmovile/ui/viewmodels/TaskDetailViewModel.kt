package com.example.appmovile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.use_cases.GetTaskDetailsUseCase
import com.example.appmovile.domain.use_cases.UpdateTaskStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado de la Pantalla de Detalle
data class TaskDetailState(
    val task: Task? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// ViewModel
class TaskDetailViewModel(
    private val taskId: Int,
    private val getTaskDetailsUseCase: GetTaskDetailsUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Carga la tarea por el ID que recibió
                val task = getTaskDetailsUseCase(taskId)
                _state.value = _state.value.copy(task = task, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = "Error: ${e.message}")
            }
        }
    }

    // Función para Modificar Prioridad
    fun updatePriority(newPriority: String) {
        val currentTask = _state.value.task ?: return

        viewModelScope.launch {
            val updatedTask = currentTask.copy(priority = newPriority)
            updateTaskStatusUseCase(updatedTask, updatedTask.isCompleted)
            // Refleja el cambio en la vista de detalle
            _state.value = _state.value.copy(task = updatedTask)
        }
    }
}

// Factory (ya que el ViewModel recibe un ID)
class TaskDetailViewModelFactory(
    private val taskId: Int,
    private val getTaskDetailsUseCase: GetTaskDetailsUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            return TaskDetailViewModel(
                taskId = taskId,
                getTaskDetailsUseCase = getTaskDetailsUseCase,
                updateTaskStatusUseCase = updateTaskStatusUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}