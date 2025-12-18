package com.example.appmovile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.appmovile.domain.models.Task
import com.example.appmovile.ui.viewmodels.LeaderViewModel
import com.example.appmovile.ui.theme.PriorityHigh
import com.example.appmovile.ui.theme.PriorityLow
import com.example.appmovile.ui.theme.PriorityMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderScreen(
    viewModel: LeaderViewModel,
    leaderId: Long,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showAddMemberDialog by remember { mutableStateOf(false) }
    
    // Estados para el formulario de tareas
    var showTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMembers(leaderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.selectedMemberId == null) "Mi Equipo" 
                        else if (state.showCompletedTasks) "Tareas Completadas" 
                        else "Tareas del Miembro"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // LÓGICA DE REGRESO JERÁRQUICA
                        when {
                            state.showCompletedTasks -> {
                                // Si está en completadas, vuelve a pendientes del mismo usuario
                                viewModel.toggleShowCompleted(false)
                            }
                            state.selectedMemberId != null -> {
                                // Si está en pendientes, vuelve a la lista de miembros
                                viewModel.selectMember(null)
                            }
                            else -> {
                                // Si está en la lista, sale de la pantalla
                                onNavigateBack()
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (state.selectedMemberId == null) {
                        IconButton(onClick = { showAddMemberDialog = true }) {
                            Icon(Icons.Filled.PersonAdd, contentDescription = "Agregar Miembro")
                        }
                    } else {
                        // Botón para alternar entre tareas pendientes y completadas
                        IconButton(onClick = { viewModel.toggleShowCompleted(!state.showCompletedTasks) }) {
                            Icon(
                                imageVector = if (state.showCompletedTasks) Icons.Filled.List else Icons.Filled.Checklist,
                                contentDescription = "Ver Completadas",
                                tint = if (state.showCompletedTasks) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                        
                        // Solo permitir agregar tareas si estamos en la vista de pendientes
                        if (!state.showCompletedTasks) {
                            IconButton(onClick = { 
                                taskToEdit = null
                                showTaskDialog = true 
                            }) {
                                Icon(Icons.Filled.Add, contentDescription = "Asignar Tarea")
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                if (state.selectedMemberId == null) {
                    // LISTA DE MIEMBROS
                    if (state.members.isEmpty()) {
                        Text("Aún no tienes miembros en tu equipo.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn {
                            items(state.members) { member ->
                                MemberItem(
                                    email = member.email,
                                    onClick = { viewModel.selectMember(member.id) }
                                )
                            }
                        }
                    }
                } else {
                    // LISTA DE TAREAS DEL MIEMBRO (Filtradas por estado de completitud)
                    val filteredTasks = state.selectedMemberTasks.filter { it.isCompleted == state.showCompletedTasks }
                    
                    if (filteredTasks.isEmpty()) {
                        Text(
                            if (state.showCompletedTasks) "No hay tareas completadas." else "Este miembro no tiene tareas pendientes.", 
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn {
                            items(filteredTasks) { task ->
                                MemberTaskItem(
                                    task = task,
                                    onToggle = { viewModel.toggleTaskCompletionForMember(task) },
                                    onDelete = { viewModel.deleteTaskFromMember(task.id) },
                                    onEdit = {
                                        taskToEdit = task
                                        showTaskDialog = true
                                    }
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

        if (showAddMemberDialog) {
            AddMemberDialog(
                onDismiss = { showAddMemberDialog = false },
                onConfirm = { email ->
                    viewModel.addMember(leaderId, email)
                    showAddMemberDialog = false
                }
            )
        }

        if (showTaskDialog) {
            TaskFormDialog(
                task = taskToEdit,
                onDismiss = { showTaskDialog = false },
                onConfirm = { updatedTask ->
                    if (taskToEdit == null) {
                        viewModel.addTaskToMember(updatedTask)
                    } else {
                        viewModel.updateTaskForMember(updatedTask)
                    }
                    showTaskDialog = false
                }
            )
        }
    }
}

@Composable
fun MemberItem(email: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() }
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.AccountCircle, null, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(16.dp))
            Text(email, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Filled.ChevronRight, null)
        }
    }
}

@Composable
fun MemberTaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onEdit() }) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                Text("Prioridad: ${task.priority}", style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, null, tint = MaterialTheme.colorScheme.primary) }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, null, tint = Color.Red) }
        }
    }
}

@Composable
fun TaskFormDialog(task: Task?, onDismiss: () -> Unit, onConfirm: (Task) -> Unit) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var location by remember { mutableStateOf(task?.location ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: "MEDIA") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Asignar Tarea" else "Editar Tarea") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Ubicación") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                
                Text("Prioridad:", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    PriorityButton("BAJA", priority == "BAJA", PriorityLow) { priority = "BAJA" }
                    PriorityButton("MEDIA", priority == "MEDIA", PriorityMedium) { priority = "MEDIA" }
                    PriorityButton("ALTA", priority == "ALTA", PriorityHigh) { priority = "ALTA" }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val newTask = task?.copy(title = title, description = description, location = location, priority = priority)
                    ?: Task(title = title, description = description, location = location, priority = priority, isCompleted = false)
                onConfirm(newTask)
            }) { Text(if (task == null) "Asignar" else "Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun PriorityButton(label: String, isSelected: Boolean, color: Color, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.5f)
        )
    )
}

@Composable
fun AddMemberDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Miembro al Equipo") },
        text = {
            Column {
                Text("Ingresa el correo del usuario que deseas agregar.")
                Spacer(Modifier.height(8.dp))
                TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { onConfirm(email) }) { Text("Agregar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
