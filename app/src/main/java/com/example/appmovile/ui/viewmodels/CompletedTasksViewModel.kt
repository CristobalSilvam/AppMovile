package com.example.appmovile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.use_cases.GetCompletedTasksUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.lang.IllegalArgumentException

// Estado de la UI (reutilizamos la estructura de TaskListState si es posible)
data class CompletedTasksState(
    val completedTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

class CompletedTasksViewModel(
    private val getCompletedTasksUseCase: GetCompletedTasksUseCase
) : ViewModel() {

    val state: StateFlow<CompletedTasksState> = getCompletedTasksUseCase()
        .map { tasks ->
            CompletedTasksState(
                completedTasks = tasks,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CompletedTasksState()
        )
}

// Fábrica de ViewModel
class CompletedTasksViewModelFactory(
    private val getCompletedTasksUseCase: GetCompletedTasksUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompletedTasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CompletedTasksViewModel(getCompletedTasksUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}