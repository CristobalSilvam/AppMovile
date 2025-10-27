package com.example.appmovile.ui.viewmodels


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.use_cases.SaveTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

// ⬇️ Estado de la UI expuesto a la pantalla
data class TaskFormState(
    val title: String = "",
    val priority: String = "MEDIA",
    val titleError: String? = null,
    val isSaving: Boolean = false,
    val saveSuccessful: Boolean = false
)

class TaskFormViewModel(
    // ⬇️ Inyección del Caso de Uso (Lógica de negocio pura)
    private val saveTaskUseCase: SaveTaskUseCase,
    private val applicationContext: Context
) : ViewModel() {

    // Estado interno que se puede modificar
    private val _state = MutableStateFlow(TaskFormState())
    // Estado expuesto a la UI (solo lectura)
    val state: StateFlow<TaskFormState> = _state.asStateFlow()

    // ----------------------------------------------------
    // 1. Manejo de Eventos (User Input)
    // ----------------------------------------------------

    fun onTitleChange(newTitle: String) {
        _state.value = _state.value.copy(
            title = newTitle,
            titleError = null // Limpiar el error cuando el usuario escribe
        )
    }

    fun onPriorityChange(newPriority: String) {
        _state.value = _state.value.copy(priority = newPriority)
    }

    // ----------------------------------------------------
    // 2. Lógica de Guardado (IL 2.2)
    // ----------------------------------------------------

    fun saveTask() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, saveSuccessful = false)

            try {
                // 1. Crear el objeto de Dominio (Task)
                val taskToSave = Task(
                    title = state.value.title,
                    priority = state.value.priority,
                    isCompleted = false
                )

                // 2. Ejecutar el Caso de Uso (Aquí ocurre la validación desacoplada)
                saveTaskUseCase(taskToSave)

                // 3. Éxito: Notificar a la UI
                _state.value = _state.value.copy(
                    isSaving = false,
                    saveSuccessful = true
                )

                // ⬇️ Aquí se insertará la lógica de Recursos Nativos (Vibración, Notificación) ⬇️
                // triggerNativeVibration()
                // scheduleNativeNotification()

            } catch (e: IllegalArgumentException) {
                // 4. Fracaso por Validación (Captura el error lanzado por el UseCase)
                _state.value = _state.value.copy(
                    isSaving = false,
                    titleError = e.message ?: "Error desconocido de validación"
                )
            } catch (e: Exception) {
                // 5. Otros Errores (DB, etc.)
                _state.value = _state.value.copy(isSaving = false, titleError = "Error al guardar la tarea.")
            }
        }
    }
    fun resetState() {
        // Restablece el estado a sus valores predeterminados después de navegar
        _state.value = TaskFormState()
    }
}