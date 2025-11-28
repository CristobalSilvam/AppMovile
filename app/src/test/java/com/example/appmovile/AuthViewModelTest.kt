package com.example.appmovile.ui.viewmodels

import com.example.appmovile.domain.use_cases.LoginUserUseCase
import com.example.appmovile.domain.use_cases.RegisterUserUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.lang.IllegalArgumentException

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AuthViewModel

    private val registerUserUseCase: RegisterUserUseCase = mockk()
    private val loginUserUseCase: LoginUserUseCase = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = AuthViewModel(registerUserUseCase, loginUserUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Registro exitoso
    @Test
    fun `authenticate registro exitoso actualiza authSuccess`() = runTest {
        viewModel.switchMode() // Modo registro
        viewModel.onEmailChange("test@a.com")
        viewModel.onPasswordChange("123456")
        viewModel.onConfirmPasswordChange("123456")

        coEvery { registerUserUseCase("test@a.com","123456","123456") } returns Unit

        viewModel.authenticate()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.authSuccess)
        assertEquals(null, state.errorMessage)
    }

    // Login exitoso
    @Test
    fun `authenticate login exitoso actualiza authSuccess`() = runTest {
        viewModel.onEmailChange("user@a.com")
        viewModel.onPasswordChange("123456")

        coEvery { loginUserUseCase("user@a.com","123456") } returns true

        viewModel.authenticate()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.authSuccess)
        assertEquals(null, state.errorMessage)
    }

    // Login fallido
    @Test
    fun `authenticate login fallido actualiza errorMessage`() = runTest {
        viewModel.onEmailChange("user@a.com")
        viewModel.onPasswordChange("wrong")

        coEvery { loginUserUseCase("user@a.com","wrong") } returns false

        viewModel.authenticate()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertFalse(state.authSuccess)
        assertEquals("Email o contraseña incorrectos.", state.errorMessage)
    }

    // Registro fallido (ej: contraseñas no coinciden)
    @Test
    fun `authenticate registro fallido actualiza errorMessage`() = runTest {
        viewModel.switchMode()
        viewModel.onEmailChange("test@a.com")
        viewModel.onPasswordChange("123")
        viewModel.onConfirmPasswordChange("456")

        coEvery { registerUserUseCase("test@a.com","123","456") } throws IllegalArgumentException("Las contraseñas no coinciden")

        viewModel.authenticate()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertFalse(state.authSuccess)
        assertEquals("Las contraseñas no coinciden", state.errorMessage)
    }
}