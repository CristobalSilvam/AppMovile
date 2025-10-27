package com.example.appmovile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.appmovile.ui.theme.PrimaryBlue
import com.example.appmovile.ui.theme.ErrorRed
import com.example.appmovile.ui.theme.PriorityHigh   // Revisa nombres de color
import com.example.appmovile.ui.theme.PriorityLow
import com.example.appmovile.ui.theme.PriorityMedium
import com.example.appmovile.ui.viewmodels.TaskFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    taskFormViewModel: TaskFormViewModel = viewModel(),
    onSaveSuccess: () -> Unit
) {
    val state = taskFormViewModel.state.collectAsState().value

    // Efecto lateral: Ejecuta la navegación si el ViewModel indica éxito
    LaunchedEffect(key1 = state.saveSuccessful) {
        if (state.saveSuccessful) {
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nueva Tarea") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Campo de Título CONECTADO y con Retroalimentación Visual (IL 2.1)
            OutlinedTextField(
                value = state.title, // ⬅️ Usar el estado del ViewModel
                onValueChange = taskFormViewModel::onTitleChange, // ⬅️ Llamar al evento del ViewModel
                label = { Text("Título de la Tarea") },
                isError = state.titleError != null, // ⬅️ Error visual si hay mensaje
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = "Título") },
                modifier = Modifier.fillMaxWidth()
            )

            // Mensaje de Error
            state.titleError?.let { message ->
                Text(
                    text = message,
                    color = ErrorRed,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            // Sección de Prioridad
            Text(
                text = "Prioridad:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Determinar qué botón debe estar resaltado
                val isAlta = state.priority == "ALTA"
                val isMedia = state.priority == "MEDIA"
                val isBaja = state.priority == "BAJA"

                // 1. Botón ALTA
                Button(
                    onClick = { taskFormViewModel.onPriorityChange("ALTA") }, // ⬅️ Lógica de control
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAlta) PriorityHigh else Color.LightGray // Resalta si es seleccionado
                    )
                ) {
                    Text("Alta", color = if (isAlta) Color.White else Color.Black)
                }

                Spacer(Modifier.width(8.dp))

                // 2. Botón MEDIA
                Button(
                    onClick = { taskFormViewModel.onPriorityChange("MEDIA") }, // ⬅️ Lógica de control
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMedia) PriorityMedium else Color.LightGray // Naranja
                    )
                ) {
                    Text("Media", color = if (isMedia) Color.White else Color.Black)
                }

                Spacer(Modifier.width(8.dp))

                // 3. Botón BAJA
                Button(
                    onClick = { taskFormViewModel.onPriorityChange("BAJA") }, // ⬅️ Lógica de control
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBaja) PriorityLow else Color.LightGray
                    )
                ) {
                    Text("Baja", color = if (isBaja) Color.White else Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // (La selección de prioridad y otros campos irían aquí, llamando a onPriorityChange)

            Spacer(modifier = Modifier.weight(1f))

            // Botón Guardar CONECTADO
            Button(
                onClick = taskFormViewModel::saveTask, // Llama a la función de guardado
                enabled = !state.isSaving, // Deshabilitar si está guardando (Animación de Carga - IL 2.2)
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Guardar Tarea", color = Color.White)
                }
            }
        }
    }
}