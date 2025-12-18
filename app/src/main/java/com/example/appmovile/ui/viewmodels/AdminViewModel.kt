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
    val leaders: List<UserDto> = emptyList(), // Para la gestión de grupos
    val selectedUserTasks: List<Task> = emptyList(),
    val selectedGroupMembers: List<UserDto> = emptyList(), // Miembros del líder seleccionado
    val selectedUserId: Long? = null,
    val selectedLeaderId: Long? = null,
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
                val leaderList = userList.filter { it.role == "LEADER" }
                _state.update { it.copy(users = userList, leaders = leaderList, isLoading = false) }
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
            try {
                val tasks = taskRepository.getTasksByUserId(userId)
                _state.update { it.copy(selectedUserTasks = tasks, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al cargar tareas: ${e.message}", isLoading = false) }
            }
        }
    }

    // --- GESTIÓN DE ROLES ---
    fun updateUserRole(userId: Long, newRole: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                userRepository.updateUserRole(userId, newRole)
                loadUsers() // Refrescar la lista
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al actualizar rol: ${e.message}", isLoading = false) }
            }
        }
    }

    // --- GESTIÓN DE GRUPOS PARA ADMIN ---

    fun selectLeader(leaderId: Long?) {
        _state.update { it.copy(selectedLeaderId = leaderId, selectedGroupMembers = emptyList()) }
        if (leaderId != null) {
            loadGroupMembers(leaderId)
        }
    }

    private fun loadGroupMembers(leaderId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val members = userRepository.getGroupMembers(leaderId)
                _state.update { it.copy(selectedGroupMembers = members, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al cargar miembros: ${e.message}", isLoading = false) }
            }
        }
    }

    fun addMemberToGroup(leaderId: Long, email: String) {
        viewModelScope.launch {
            try {
                userRepository.addMemberByEmail(leaderId, email)
                loadGroupMembers(leaderId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "No se pudo añadir al miembro: ${e.message}") }
            }
        }
    }

    fun removeMemberFromGroup(leaderId: Long, memberId: Long) {
        viewModelScope.launch {
            try {
                userRepository.removeMemberFromGroup(leaderId, memberId)
                loadGroupMembers(leaderId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al remover miembro: ${e.message}") }
            }
        }
    }

    fun deleteGroup(leaderId: Long) {
        viewModelScope.launch {
            try {
                userRepository.deleteGroup(leaderId)
                loadUsers() // Recargar para actualizar la lista de líderes
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al eliminar grupo: ${e.message}") }
            }
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

    fun addTaskToUser(title: String, description: String, priority: String) {
        val userId = _state.value.selectedUserId ?: return
        viewModelScope.launch {
            try {
                val newTask = Task(
                    id = 0, 
                    title = title, 
                    description = description, 
                    priority = priority,
                    isCompleted = false
                )
                taskRepository.saveTaskForUser(userId, newTask)
                loadTasksForUser(userId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al guardar tarea: ${e.message}") }
            }
        }
    }

    fun deleteTaskFromUser(taskId: Int) {
        val userId = _state.value.selectedUserId ?: return
        viewModelScope.launch {
            try {
                taskRepository.deleteTaskForUser(userId, taskId)
                loadTasksForUser(userId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al borrar tarea: ${e.message}") }
            }
        }
    }

    fun toggleTaskCompletionForUser(task: Task) {
        val userId = _state.value.selectedUserId ?: return
        viewModelScope.launch {
            try {
                taskRepository.updateTaskStatusForUser(userId, task, !task.isCompleted)
                loadTasksForUser(userId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al actualizar tarea: ${e.message}") }
            }
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
