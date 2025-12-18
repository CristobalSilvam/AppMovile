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

data class LeaderState(
    val members: List<UserDto> = emptyList(),
    val selectedMemberTasks: List<Task> = emptyList(),
    val selectedMemberId: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCompletedTasks: Boolean = false
)

class LeaderViewModel(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderState())
    val state = _state.asStateFlow()

    fun loadMembers(leaderId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val members = userRepository.getGroupMembers(leaderId)
                _state.update { it.copy(members = members, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun addMember(leaderId: Long, email: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                userRepository.addMemberByEmail(leaderId, email)
                loadMembers(leaderId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "No se pudo agregar al usuario: ${e.message}", isLoading = false) }
            }
        }
    }

    fun selectMember(memberId: Long?) {
        _state.update { it.copy(selectedMemberId = memberId, selectedMemberTasks = emptyList(), showCompletedTasks = false) }
        if (memberId != null) {
            loadMemberTasks(memberId)
        }
    }

    private fun loadMemberTasks(memberId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val tasks = taskRepository.getTasksByUserId(memberId)
                _state.update { it.copy(selectedMemberTasks = tasks, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al cargar tareas: ${e.message}", isLoading = false) }
            }
        }
    }

    fun toggleShowCompleted(show: Boolean) {
        _state.update { it.copy(showCompletedTasks = show) }
    }

    fun addTaskToMember(task: Task) {
        val memberId = _state.value.selectedMemberId ?: return
        viewModelScope.launch {
            try {
                taskRepository.saveTaskForUser(memberId, task)
                loadMemberTasks(memberId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al crear tarea: ${e.message}") }
            }
        }
    }

    fun updateTaskForMember(task: Task) {
        val memberId = _state.value.selectedMemberId ?: return
        viewModelScope.launch {
            try {
                taskRepository.updateTaskStatusForUser(memberId, task, task.isCompleted)
                loadMemberTasks(memberId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al actualizar tarea: ${e.message}") }
            }
        }
    }

    fun deleteTaskFromMember(taskId: Int) {
        val memberId = _state.value.selectedMemberId ?: return
        viewModelScope.launch {
            try {
                taskRepository.deleteTaskForUser(memberId, taskId)
                loadMemberTasks(memberId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al eliminar tarea: ${e.message}") }
            }
        }
    }

    fun toggleTaskCompletionForMember(task: Task) {
        val memberId = _state.value.selectedMemberId ?: return
        viewModelScope.launch {
            try {
                taskRepository.updateTaskStatusForUser(memberId, task, !task.isCompleted)
                loadMemberTasks(memberId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al actualizar tarea: ${e.message}") }
            }
        }
    }
}

class LeaderViewModelFactory(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LeaderViewModel(userRepository, taskRepository) as T
    }
}
