package com.example.appmovile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.appmovile.domain.models.Task
import com.example.appmovile.ui.viewmodels.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (state.selectedUserId == null) "Panel Admin: Usuarios" else "Tareas del Usuario") 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.selectedUserId == null) onNavigateBack() 
                        else viewModel.selectUser(null)
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (state.selectedUserId != null) {
                        IconButton(onClick = { showAddTaskDialog = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Añadir Tarea")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                if (state.selectedUserId == null) {
                    // LISTA DE USUARIOS
                    LazyColumn {
                        items(state.users) { user ->
                            UserItem(
                                email = user.email,
                                role = user.role,
                                onClick = { viewModel.selectUser(user.id) },
                                onDelete = { viewModel.deleteUser(user.id) }
                            )
                        }
                    }
                } else {
                    // LISTA DE TAREAS DEL USUARIO SELECCIONADO
                    if (state.selectedUserTasks.isEmpty()) {
                        Text("Este usuario no tiene tareas.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn {
                            items(state.selectedUserTasks) { task ->
                                AdminTaskItem(
                                    task = task,
                                    onToggle = { viewModel.toggleTaskCompletionForUser(task) },
                                    onDelete = { viewModel.deleteTaskFromUser(task.id) }
                                )
                            }
                        }
                    }
                }
            }

            if (state.error != null) {
                Text(state.error!!, color = Color.Red, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))
            }
        }

        if (showAddTaskDialog) {
            AddTaskAdminDialog(
                onDismiss = { showAddTaskDialog = false },
                onConfirm = { title, desc, priority ->
                    viewModel.addTaskToUser(title, desc, priority)
                    showAddTaskDialog = false
                }
            )
        }
    }
}

@Composable
fun UserItem(email: String, role: String, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() }
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Person, null)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(email, style = MaterialTheme.typography.titleMedium)
                Text("Rol: $role", style = MaterialTheme.typography.bodySmall)
            }
            if (role != "ADMIN") {
                IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, null, tint = Color.Red) }
            }
        }
    }
}

@Composable
fun AdminTaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                Text(task.priority, style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, null, tint = Color.Red) }
        }
    }
}

@Composable
fun AddTaskAdminDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MEDIA") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Tarea para Usuario") },
        text = {
            Column {
                TextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                TextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") })
                // Simplificado: En una app real usarías un Dropdown
                Text("Prioridad: ALTA, MEDIA, BAJA")
                TextField(value = priority, onValueChange = { priority = it.uppercase() })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, desc, priority) }) { Text("Crear") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
