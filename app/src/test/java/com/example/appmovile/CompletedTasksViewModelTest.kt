package com.example.appmovile.ui.viewmodels

import com.example.appmovile.domain.models.Task
import com.example.appmovile.domain.use_cases.GetCompletedTasksUseCase
import com.example.appmovile.domain.use_cases.UpdateTaskStatusUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CompletedTasksViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CompletedTasksViewModel
    private val getCompletedTasksUseCase: GetCompletedTasksUseCase = mockk()
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase = mockk(relaxed = true)

    private val completedList = listOf(
        Task(
            id = 1,
            title = "Tarea 1",
            description = "Desc",
            location = "Casa",
            priority = "MEDIA",
            isCompleted = true,
            reminderTime = null
        ),
        Task(
            id = 2,
            title = "Tarea 2",
            description = "Otra desc",
            location = "Trabajo",
            priority = "ALTA",
            isCompleted = true,
            reminderTime = null
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Test: carga correcta de tareas completadas
    @Test
    fun `carga correctamente las tareas completadas`() = runTest {
        // Fake flow con tareas completadas
        val flow = MutableStateFlow(completedList)
        every { getCompletedTasksUseCase() } returns flow

        viewModel = CompletedTasksViewModel(
            getCompletedTasksUseCase,
            updateTaskStatusUseCase
        )

        // Esperar la primera emisión real ignorando el initialValue
        val state = viewModel.state
            .drop(1) // descarta el initialValue donde isLoading = true
            .first()

        // Assert
        assertFalse(state.isLoading)
        assertEquals(2, state.completedTasks.size)
        assertEquals("Tarea 1", state.completedTasks[0].title)
    }
}
