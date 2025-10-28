// ui/screens/TaskDetailScreen.kt
package com.example.appmovile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmovile.ui.theme.PriorityHigh
import com.example.appmovile.ui.theme.PriorityLow
import com.example.appmovile.ui.theme.PriorityMedium
import com.example.appmovile.ui.viewmodels.TaskDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: TaskDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val state = viewModel.state.collectAsState().value
    val task = state.task // La tarea cargada

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(task?.title ?: "Detalle de Tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (task != null) {
                // Muestra los detalles de la tarea
                Text("Descripción:", style = MaterialTheme.typography.titleMedium)
                Text(task.description ?: "Sin descripción", modifier = Modifier.padding(bottom = 16.dp))

                Text("Ubicación:", style = MaterialTheme.typography.titleMedium)
                Text(task.location ?: "Sin ubicación especificada", modifier = Modifier.padding(bottom = 16.dp))

                // Selector de Prioridad (igual al de TaskFormScreen)
                Text("Prioridad:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val currentPriority = task.priority.uppercase()

                    Button(
                        onClick = { viewModel.updatePriority("ALTA") },
                        colors = ButtonDefaults.buttonColors(containerColor = if (currentPriority == "ALTA") PriorityHigh else MaterialTheme.colorScheme.secondaryContainer)
                    ) { Text("Alta") }

                    Button(
                        onClick = { viewModel.updatePriority("MEDIA") },
                        colors = ButtonDefaults.buttonColors(containerColor = if (currentPriority == "MEDIA") PriorityMedium else MaterialTheme.colorScheme.secondaryContainer)
                    ) { Text("Media") }

                    Button(
                        onClick = { viewModel.updatePriority("BAJA") },
                        colors = ButtonDefaults.buttonColors(containerColor = if (currentPriority == "BAJA") PriorityLow else MaterialTheme.colorScheme.secondaryContainer)
                    ) { Text("Baja") }
                }

                // Puedes añadir más detalles como fecha, etc.

            } else {
                Text("No se pudo cargar la tarea.")
            }
        }
    }
}