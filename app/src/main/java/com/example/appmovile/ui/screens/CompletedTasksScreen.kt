package com.example.appmovile.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmovile.domain.models.Task
import com.example.appmovile.ui.viewmodels.CompletedTasksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedTasksScreen(
    viewModel: CompletedTasksViewModel = viewModel(),
    // Puedes pasar un callback para volver atrás si lo necesitas
    onNavigateBack: () -> Unit
) {
    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = { TopAppBar(title = { Text("Historial de Tareas Completadas") },
            navigationIcon = {
                IconButton( onClick = onNavigateBack
                ){
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Volver a Tareas Pendientes"
                    )
                }
            }

        ) }
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
                items(state.completedTasks, key = { it.id }) { task ->
                    Text(
                        text = "✅ ${task.title}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            if (state.completedTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay tareas completadas todavía.")
                }
            }
        }
    }
}
@Composable
fun CompletedTaskItem(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Completada",
                tint = MaterialTheme.colorScheme.primary // o SuccessGreen
            )
            Spacer(Modifier.width(16.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                // Puedes tachar el texto si quieres:
                // textDecoration = TextDecoration.LineThrough
            )
            // Aquí puedes añadir la fecha de completado si la tuvieras
        }
    }
}