package com.example.appmovile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist // Ícono de ticket/historial
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmovile.ui.viewmodels.TaskListViewModel
import com.example.appmovile.Destinations // Asegúrate de importar Destinations
import com.example.appmovile.ui.theme.PriorityAccentColor
import com.example.appmovile.ui.theme.PriorityHigh
import com.example.appmovile.ui.theme.PriorityLow
import com.example.appmovile.ui.theme.PriorityMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    onNavigateToForm: () -> Unit,
    onNavigateToCompleted: () -> Unit
) {
    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas Pendientes") },
                actions = {
                    // BOTÓN HISTORIAL (TICKET)
                    IconButton(onClick = onNavigateToCompleted) {
                        Icon(
                            imageVector = Icons.Filled.Checklist, // Usamos Checklist como "ticket"
                            contentDescription = "Ver Tareas Completadas"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToForm) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Tarea")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(state.tasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggleCompletion = viewModel::toggleTaskCompletion,
                        onDelete = viewModel::deleteTask
                    )
                }
            }
            if (state.tasks.isEmpty() && !state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("¡No tienes tareas pendientes!", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: com.example.appmovile.domain.models.Task,
    onToggleCompletion: (com.example.appmovile.domain.models.Task) -> Unit,
    onDelete: (Int) -> Unit
) {

    val cardColor = when (task.priority) {
        "ALTA" -> PriorityHigh.copy(alpha = 0.2f) // 20% de opacidad para el fondo
        "MEDIA" -> PriorityMedium.copy(alpha = 0.2f) // 30% de opacidad
        "BAJA" -> PriorityLow.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface // Color por defecto si no hay prioridad
    }

    // 2. Determinar el color de acento/texto (más oscuro)
    val priorityAccentColor = when (task.priority) {
        "ALTA" -> PriorityHigh
        "MEDIA" -> PriorityMedium
        else -> PriorityLow
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Checkbox para completar/descompletar
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleCompletion(task) }
            )

            // Título de la tarea
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                // Mostrar Prioridad
                Text(
                    text = "Prioridad: ${task.priority}" ,
                    style = MaterialTheme.typography.bodySmall,
                    color = PriorityAccentColor
                )
            }

            // Botón de eliminar
            IconButton(onClick = { onDelete(task.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
        }
    }
}