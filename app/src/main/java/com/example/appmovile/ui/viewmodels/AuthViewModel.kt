package com.example.appmovile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appmovile.domain.use_cases.LoginUserUseCase
import com.example.appmovile.domain.use_cases.RegisterUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update // Importa update
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

// Estado de la pantalla de Autenticación
data class AuthState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "", // Solo para registro
    val isRegistering: Boolean = false, // Modo Registro vs Login
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val authSuccess: Boolean = false // Indica si el login/registro fue exitoso
)

class AuthViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    // --- Cambios de Estado de la UI ---
    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, errorMessage = null) }
    }
    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, errorMessage = null) }
    }
    fun onConfirmPasswordChange(confirm: String) {
        // Solo actualizar si estamos en modo registro
        if (_state.value.isRegistering) {
            _state.update { it.copy(confirmPassword = confirm, errorMessage = null) }
        }
    }
    fun switchMode() { // Cambiar entre Login y Registro
        _state.update {
            it.copy(
                isRegistering = !it.isRegistering,
                errorMessage = null,
                // Limpiar campos al cambiar de modo para evitar confusiones
                email = "",
                password = "",
                confirmPassword = ""
            )
        }
    }

    // --- Lógica de Autenticación ---
    fun authenticate() {
        val currentState = _state.value
        // Muestra carga y limpia errores/éxito previo
        _state.update { it.copy(isLoading = true, errorMessage = null, authSuccess = false) }

        viewModelScope.launch {
            try {
                if (currentState.isRegistering) {
                    // LLAMA AL CASO DE USO DE REGISTRO (con validación)
                    registerUserUseCase(currentState.email, currentState.password, currentState.confirmPassword)
                    // Si no hay excepción, el registro fue exitoso
                    _state.update { it.copy(isLoading = false, authSuccess = true) }
                } else {
                    // LLAMA AL CASO DE USO DE LOGIN
                    val success = loginUserUseCase(currentState.email, currentState.password)
                    if (success) {
                        _state.update { it.copy(isLoading = false, authSuccess = true) }
                    } else {
                        // Si loginUserUseCase devuelve false (usuario no encontrado o contraseña incorrecta)
                        throw IllegalArgumentException("Email o contraseña incorrectos.")
                    }
                }
            } catch (e: IllegalArgumentException) { // Captura errores de validación o login
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            } catch (e: Exception) { // Otros errores (DB, etc.)
                _state.update { it.copy(isLoading = false, errorMessage = "Ocurrió un error inesperado.") }
            }
        }
    }
}

// --- Fábrica ---
class AuthViewModelFactory(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(registerUserUseCase, loginUserUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}