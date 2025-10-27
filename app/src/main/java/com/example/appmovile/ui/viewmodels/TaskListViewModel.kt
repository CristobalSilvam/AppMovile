package com.example.appmovile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.use_cases.DeleteTaskUseCase
import com.example.appmovile.domain.use_cases.GetTaskUseCase
import com.example.appmovile.domain.use_cases.UpdateTaskStatusUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


//Definición del Estado de la UI


data class TaskListState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

// ViewModel Principal

class TaskListViewModel(
    private val getTaskUseCase: GetTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : ViewModel() {

    // StateFlow para la lista de tareas
    val state: StateFlow<TaskListState> = getTaskUseCase()
        .map { tasks ->
            // Mapea el Flow<List<Task>> a TaskListState
            TaskListState(
                tasks = tasks,
                isLoading = false // Desactivar carga al recibir los datos
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskListState()
        )



    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            try {
                // Invierte el estado actual y llama al UseCase de actualización
                updateTaskStatusUseCase(task, !task.isCompleted)
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            try {
                deleteTaskUseCase(taskId)
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }
}
