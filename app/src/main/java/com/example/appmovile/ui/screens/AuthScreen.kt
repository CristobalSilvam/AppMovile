package com.example.appmovile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appmovile.ui.theme.ErrorRed
import com.example.appmovile.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel, // Recibe el ViewModel inyectado
    onAuthSuccess: () -> Unit // Callback para navegar a la lista de tareas
) {
    val state = viewModel.state.collectAsState().value

    // Efecto para navegar cuando la autenticación es exitosa
    LaunchedEffect(state.authSuccess) {
        if (state.authSuccess) {
            onAuthSuccess() // Ejecuta la navegación a TaskListScreen
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isRegistering) "Crear Cuenta" else "Iniciar Sesión") }
                // Podrías añadir un botón de regreso si vienes de otra pantalla
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp), // Padding horizontal
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centra el contenido verticalmente
        ) {
            // Campo Email
            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true, // Evita saltos de línea
                isError = state.errorMessage?.contains("email", ignoreCase = true) == true
            )
            Spacer(Modifier.height(8.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(), // Oculta la contraseña
                singleLine = true,
                isError = state.errorMessage?.contains("contraseña", ignoreCase = true) == true ||
                        state.errorMessage?.contains("incorrectos", ignoreCase = true) == true // Marca error si login falla
            )
            Spacer(Modifier.height(8.dp))

            // Campo Confirmar Contraseña (Solo en modo Registro)
            if (state.isRegistering) {
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = state.errorMessage?.contains("coinciden", ignoreCase = true) == true
                )
                Spacer(Modifier.height(16.dp))
            }

            // Mensaje de Error (si existe)
            state.errorMessage?.let {
                Text(
                    text = it,
                    color = ErrorRed, // Usa tu color de error
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.bodySmall // Texto pequeño para el error
                )
            }

            // Botón de Acción (Login o Registro)
            Button(
                onClick = viewModel::authenticate, // Llama a la función de autenticación
                enabled = !state.isLoading, // Deshabilita mientras carga
                modifier = Modifier.fillMaxWidth().height(48.dp) // Tamaño estándar de botón
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary // Color del spinner
                    )
                } else {
                    Text(if (state.isRegistering) "Registrarse" else "Ingresar")
                }
            }
            Spacer(Modifier.height(16.dp))

            // Botón para cambiar de modo (Login <-> Registro)
            TextButton(onClick = viewModel::switchMode) {
                Text(if (state.isRegistering) "¿Ya tienes cuenta? Inicia Sesión" else "¿No tienes cuenta? Regístrate")
            }
        }
    }
}