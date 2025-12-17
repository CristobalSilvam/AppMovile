package com.example.appmovile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appmovile.data.remote.dto.UserDto
import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.repositories.TaskRepository
import com.example.appmovile.domain.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminState(
    val users: List<UserDto> = emptyList(),
    val selectedUserTasks: List<Task> = emptyList(),
    val selectedUserId: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminViewModel(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminState())
    val state = _state.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val userList = userRepository.getAllUsers()
                _state.update { it.copy(users = userList, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun selectUser(userId: Long?) {
        _state.update { it.copy(selectedUserId = userId, selectedUserTasks = emptyList()) }
        if (userId != null) {
            loadTasksForUser(userId)
        }
    }

    private fun loadTasksForUser(userId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val tasks = taskRepository.getTasksByUserId(userId)
            _state.update { it.copy(selectedUserTasks = tasks, isLoading = false) }
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(userId)
                loadUsers()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al borrar usuario: ${e.message}") }
            }
        }
    }

    // --- OPERACIONES DE TAREAS PARA EL ADMIN ---
    
    fun addTaskToUser(title: String, description: String, priority: String) {
        val userId = _state.value.selectedUserId ?: return
        viewModelScope.launch {
            val newTask = Task(
                id = 0, 
                title = title, 
                description = description, 
                priority = priority,
                isCompleted = false // Campo obligatorio según el modelo
            )
            taskRepository.saveTaskForUser(userId, newTask)
            loadTasksForUser(userId)
        }
    }

    fun deleteTaskFromUser(taskId: Int) {
        val userId = _state.value.selectedUserId ?: return
        viewModelScope.launch {
            taskRepository.deleteTaskForUser(userId, taskId)
            loadTasksForUser(userId)
        }
    }

    fun toggleTaskCompletionForUser(task: Task) {
        val userId = _state.value.selectedUserId ?: return
        viewModelScope.launch {
            taskRepository.updateTaskStatusForUser(userId, task, !task.isCompleted)
            loadTasksForUser(userId)
        }
    }
}

class AdminViewModelFactory(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminViewModel(userRepository, taskRepository) as T
    }
}
