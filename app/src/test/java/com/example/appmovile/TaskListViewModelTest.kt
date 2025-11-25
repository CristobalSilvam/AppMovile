package com.example.appmovile

import app.cash.turbine.test
import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.use_cases.*
import com.example.appmovile.ui.viewmodels.TaskListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    private lateinit var getTaskUseCase: GetTaskUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private lateinit var updateTaskStatusUseCase: UpdateTaskStatusUseCase
    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var viewModel: TaskListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        // 🟢 SOLO MockK
        getTaskUseCase = mockk()
        deleteTaskUseCase = mockk()
        updateTaskStatusUseCase = mockk()
        getWeatherUseCase = mockk()

        // Mock inicial de tareas
        coEvery { getTaskUseCase() } returns flowOf(emptyList())

        viewModel = TaskListViewModel(
            getTaskUseCase,
            deleteTaskUseCase,
            updateTaskStatusUseCase,
            getWeatherUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getTasks carga tareas correctamente`() = runTest {

        val fakeTasks = listOf(
            Task(1, "Tarea 1", "Desc1", "Casa", "ALTA", false, null),
            Task(2, "Tarea 2", "Desc2", "Trabajo", "MEDIA", false, null)
        )

        coEvery { getTaskUseCase() } returns flowOf(fakeTasks)

        viewModel = TaskListViewModel(
            getTaskUseCase,
            deleteTaskUseCase,
            updateTaskStatusUseCase,
            getWeatherUseCase
        )

        viewModel.state.test {
            awaitItem()
            val item = awaitItem()
            assert(item.tasks.size == 2)
            assert(!item.isLoading)
        }
    }

    @Test
    fun `deleteTask elimina una tarea correctamente`() = runTest {
        val taskFlow = MutableStateFlow(
            listOf(
                Task(1, "Tarea 1", "Desc", "Casa", "ALTA", false, null),
                Task(2, "Tarea 2", "Desc2", "Trabajo", "MEDIA", false, null)
            )
        )

        coEvery { getTaskUseCase() } returns taskFlow
        coEvery { deleteTaskUseCase(any()) } coAnswers {
            val id = firstArg<Int>()
            taskFlow.value = taskFlow.value.filter { it.id != id }
        }

        // 🔹 Crear ViewModel después de configurar mocks
        val viewModel = TaskListViewModel(
            getTaskUseCase,
            deleteTaskUseCase,
            updateTaskStatusUseCase,
            getWeatherUseCase
        )

        // Llamada al ViewModel para eliminar la tarea
        viewModel.deleteTask(1)

        val task = taskFlow.value.first()

        // Observamos el estado con Turbine
        viewModel.state.test {
            skipItems(1)
            viewModel.toggleTaskCompletion(task)
            val state = awaitItem()
            assert(state.tasks.size == 1)
            assert(state.tasks[0].id == 2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateTaskStatus actualiza correctamente`() = runTest {
        val taskFlow = MutableStateFlow(
            listOf(Task(1, "Tarea 1", "Desc", "Casa", "ALTA", false, null))
        )

        coEvery { getTaskUseCase() } returns taskFlow
        coEvery { updateTaskStatusUseCase(any(), any()) } coAnswers {
            val task = firstArg<Task>()
            val isCompleted = secondArg<Boolean>()
            taskFlow.value = taskFlow.value.map {
                if (it.id == task.id) it.copy(isCompleted = isCompleted) else it
            }
        }

        // 🔹 Crear ViewModel después de configurar mocks
        val viewModel = TaskListViewModel(
            getTaskUseCase,
            deleteTaskUseCase,
            updateTaskStatusUseCase,
            getWeatherUseCase
        )

        val task = taskFlow.value.first()
        viewModel.toggleTaskCompletion(task)

        viewModel.state.test {
            skipItems(1)
            viewModel.deleteTask(1)
            val state = awaitItem()
            assert(state.tasks.first().isCompleted)
            cancelAndIgnoreRemainingEvents()
        }
    }
}