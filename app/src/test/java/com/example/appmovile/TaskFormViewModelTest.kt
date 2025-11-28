package com.example.appmovile.ui.viewmodels

import android.content.Context
import com.example.appmovile.domain.use_cases.SaveTaskUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskFormViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var viewModel: TaskFormViewModel
    private val saveTaskUseCase: SaveTaskUseCase = mockk()
    private val context: Context = mockk(relaxed = true)

    @Before
    fun setup() {
        // Necesario para evitar error de Looper
        Dispatchers.setMain(dispatcher)
        viewModel = TaskFormViewModel(saveTaskUseCase, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1. Cambios de texto simples
    @Test
    fun `onTitleChange actualiza el título correctamente`() {
        viewModel.onTitleChange("Nueva tarea")

        val result = viewModel.state.value
        assertEquals("Nueva tarea", result.title)
        assertNull(result.titleError)
    }

    @Test
    fun `onDescriptionChange actualiza la descripción correctamente`() {
        viewModel.onDescriptionChange("Descripción de prueba")

        assertEquals("Descripción de prueba", viewModel.state.value.description)
    }

    @Test
    fun `onLocationChange actualiza la ubicación correctamente`() {
        viewModel.onLocationChange("Casa")

        assertEquals("Casa", viewModel.state.value.location)
    }

    @Test
    fun `onPriorityChange actualiza la prioridad correctamente`() {
        viewModel.onPriorityChange("ALTA")

        assertEquals("ALTA", viewModel.state.value.priority)
    }

    // 2. Guardado exitoso
    @Test
    fun `saveTask guarda exitosamente cuando no hay errores`() = runTest {
        viewModel.onTitleChange("Tarea válida")

        coEvery { saveTaskUseCase(any()) } returns Unit

        viewModel.saveTask()
        advanceUntilIdle() // Ejecuta las coroutines

        val state = viewModel.state.value
        assertFalse(state.isSaving)
        assertTrue(state.saveSuccessful)
        assertNull(state.titleError)
    }

    // 3. Error de validación desde el UseCase
    @Test
    fun `saveTask muestra error de validación cuando el use case lanza IllegalArgumentException`() = runTest {
        viewModel.onTitleChange("")

        coEvery { saveTaskUseCase(any()) } throws IllegalArgumentException("Título requerido")

        viewModel.saveTask()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Título requerido", state.titleError)
        assertFalse(state.saveSuccessful)
    }
}