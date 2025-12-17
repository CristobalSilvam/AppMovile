package com.example.appmovile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.use_cases.DeleteTaskUseCase
import com.example.appmovile.domain.use_cases.GetTaskUseCase
import com.example.appmovile.domain.use_cases.GetWeatherUseCase
import com.example.appmovile.domain.use_cases.UpdateTaskStatusUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Definición de Estados

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val temperature: Double, val description: String) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

data class TaskListState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val userRole: String = "USER",
    val isSortedByPriority: Boolean = false, // Para el botón de ordenar
    val filterPriority: String = "TODAS",
    val weatherState: WeatherState = WeatherState.Loading // ⬅️ Estado del Clima
)

// ViewModel Principal

class TaskListViewModel(
    private val getTaskUseCase: GetTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val getWeatherUseCase: GetWeatherUseCase // Caso de uso de clima
) : ViewModel() {

    // ESTADOS INTERNOS
    private val _isSortedByPriority = MutableStateFlow(false)
    private val _filterPriority = MutableStateFlow("TODAS")
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)

    // ⬇️ El Use Case de Tareas se convierte en un Flow que solo devuelve las tareas ⬇️
    private val tasksFlow: Flow<List<Task>> = getTaskUseCase()

    init {
        fetchWeather() // Llama a la API Externa al inicio
    }

    // FUNCIÓN DE FUSIÓN
    val state: StateFlow<TaskListState> = combine(
        tasksFlow, // Tareas desde el Back-end
        _isSortedByPriority, // Estado de Ordenamiento
        _filterPriority, // Estado de Filtrado
        _weatherState // Estado del Clima
    ) { tasks, isSorted, filter, weatherState ->

        // APLICAR FILTRO
        val filteredTasks = if (filter == "TODAS") {
            tasks
        } else {
            tasks.filter { it.priority.uppercase() == filter }
        }

        // APLICAR ORDENAMIENTO (Por prioridad)
        val finalTasks = if (isSorted) {
            filteredTasks.sortedWith(compareByDescending {
                when (it.priority.uppercase()) {
                    "ALTA" -> 3; "MEDIA" -> 2; "BAJA" -> 1; else -> 0
                }
            })
        } else {
            filteredTasks.sortedBy { it.id }
        }

        // EMITIR EL ESTADO FINAL A LA PANTALLA
        TaskListState(
            tasks = finalTasks,
            isLoading = false,
            isSortedByPriority = isSorted,
            filterPriority = filter,
            weatherState = weatherState // Pasa el clima (cargado en background)
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskListState() // Inicializa con isLoading=true
        )

    // 4. Lógica Funcional (Control de Interacciones)

    // Función para obtener el clima (llamada desde init)
    private fun fetchWeather(){
        viewModelScope.launch {
            try {
                // El UseCase llama a la API Externa
                val weather = getWeatherUseCase("Santiago")
                _weatherState.value = WeatherState.Success(
                    temperature = weather.temperature,
                    description = weather.description
                )
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error("Error al cargar el clima")
            }
        }
    }

    // Funciones de control de UI (para los botones de la barra superior)
    fun toggleSortByPriority() {
        _isSortedByPriority.update { !it }
    }

    fun setFilterPriority(priority: String) {
        _filterPriority.update { priority }
    }

    // Funciones de CRUD
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            try {
                updateTaskStatusUseCase(task, !task.isCompleted)
                getTaskUseCase()
            } catch (e: Exception) { /* Manejo de errores */ }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            try {
                deleteTaskUseCase(taskId)
                getTaskUseCase()
            } catch (e: Exception) { /* Manejo de errores */ }
        }
    }
}