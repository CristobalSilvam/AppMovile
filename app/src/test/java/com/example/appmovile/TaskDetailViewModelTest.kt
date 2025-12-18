package com.example.appmovile.ui.viewmodels

import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.use_cases.GetTaskDetailsUseCase
import com.example.appmovile.domain.use_cases.UpdateTaskStatusUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var viewModel: TaskDetailViewModel
    private val getTaskDetailsUseCase: GetTaskDetailsUseCase = mockk()
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase = mockk()

    private val sampleTask = Task(
        id = 1,
        title = "Tarea de prueba",
        description = "Descripción",
        location = "Casa",
        priority = "MEDIA",
        isCompleted = false,
        reminderTime = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1. Cargar los detalles correctamente
    @Test
    fun `loadTask carga correctamente la tarea`() = runTest {
        coEvery { getTaskDetailsUseCase(1) } returns sampleTask

        viewModel = TaskDetailViewModel(
            taskId = 1,
            getTaskDetailsUseCase = getTaskDetailsUseCase,
            updateTaskStatusUseCase = updateTaskStatusUseCase
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(sampleTask, state.task)
        assertNull(state.errorMessage)
    }

    // 2. Actualizar prioridad correctamente
    @Test
    fun `updatePriority modifica la prioridad y llama al use case`() = runTest {
        coEvery { getTaskDetailsUseCase(1) } returns sampleTask
        coEvery { updateTaskStatusUseCase(any(), any()) } returns Unit

        viewModel = TaskDetailViewModel(
            taskId = 1,
            getTaskDetailsUseCase = getTaskDetailsUseCase,
            updateTaskStatusUseCase = updateTaskStatusUseCase
        )

        advanceUntilIdle() // Esperar carga inicial

        viewModel.updatePriority("ALTA")
        advanceUntilIdle()

        val updatedTask = viewModel.state.value.task
        assertEquals("ALTA", updatedTask?.priority)

        coVerify { updateTaskStatusUseCase(match { it.priority == "ALTA" }, false) }
    }
}