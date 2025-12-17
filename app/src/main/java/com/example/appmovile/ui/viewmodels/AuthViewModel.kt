package com.example.appmovile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appmovile.domain.models.User
import com.example.appmovile.domain.repositories.AuthRepository
import com.example.appmovile.domain.use_cases.LoginUserUseCase
import com.example.appmovile.domain.use_cases.LogoutUseCase
import com.example.appmovile.domain.use_cases.RegisterUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

data class AuthState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isRegistering: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val authSuccess: Boolean = false,
    val user: User? = null
)

class AuthViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository // Añadido
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                _state.update { it.copy(user = user) }
            }
        }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, errorMessage = null) }
    }
    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, errorMessage = null) }
    }
    fun onConfirmPasswordChange(confirm: String) {
        if (_state.value.isRegistering) {
            _state.update { it.copy(confirmPassword = confirm, errorMessage = null) }
        }
    }
    fun switchMode() {
        _state.update {
            it.copy(
                isRegistering = !it.isRegistering,
                errorMessage = null,
                email = "",
                password = "",
                confirmPassword = ""
            )
        }
    }

    fun authenticate() {
        val currentState = _state.value
        _state.update { it.copy(isLoading = true, errorMessage = null, authSuccess = false) }

        viewModelScope.launch {
            try {
                if (currentState.isRegistering) {
                    registerUserUseCase(currentState.email, currentState.password, currentState.confirmPassword)
                    _state.update { it.copy(isLoading = false, authSuccess = true) }
                } else {
                    val user = loginUserUseCase(currentState.email, currentState.password)
                    _state.update { it.copy(isLoading = false, authSuccess = true, user = user) }
                }
            } catch (e: IllegalArgumentException) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Ocurrió un error inesperado.") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.update { it.copy(user = null, authSuccess = false) }
        }
    }
}

class AuthViewModelFactory(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository // Añadido
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(registerUserUseCase, loginUserUseCase, logoutUseCase, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
