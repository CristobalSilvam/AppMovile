package com.example.appmovile.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appmovile.domain.models.Task
import com.example.appmovile.ui.components.DrawerContent
import com.example.appmovile.ui.theme.PriorityAccentColor
import com.example.appmovile.ui.theme.PriorityHigh
import com.example.appmovile.ui.theme.PriorityLow
import com.example.appmovile.ui.theme.PriorityMedium
import com.example.appmovile.ui.viewmodels.AuthViewModel
import com.example.appmovile.ui.viewmodels.AuthViewModelFactory
import com.example.appmovile.ui.viewmodels.TaskListViewModel
import com.example.appmovile.Destinations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavController,
    viewModel: TaskListViewModel,
    authViewModelFactory: AuthViewModelFactory,
    onNavigateToForm: () -> Unit,
    onNavigateToCompleted: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onViewDetails: (Int) -> Unit,
) {
    val state = viewModel.state.collectAsState().value
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    val authState by authViewModel.state.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    val priorities = listOf("TODAS", "ALTA", "MEDIA", "BAJA")

    var scaleTrigger by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (scaleTrigger) 1.5f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onOptionClicked = { option ->
                    scope.launch { drawerState.close() }
                    when (option) {
                        "Login" -> onNavigateToAuth()
                        "Lista de Tareas" -> {}
                        "Tareas Completadas" -> onNavigateToCompleted()
                        "Agregar Tarea" -> onNavigateToForm()
                        "Panel Admin" -> navController.navigate(Destinations.ADMIN_MENU) // CAMBIADO A ADMIN_MENU
                        "Mi Equipo" -> navController.navigate(Destinations.LEADER_PANEL)
                        "Cerrar Sesión" -> {
                            authViewModel.logout()
                            navController.navigate(Destinations.AUTH) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                },
                weatherState = state.weatherState,
                userRole = authState.user?.role ?: "USER"
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis Tareas Pendientes") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir Menú Lateral")
                        }
                    },
                    actions = {
                        IconButton(onClick = viewModel::toggleSortByPriority) {
                            val icon = if (state.isSortedByPriority) Icons.Filled.Sort else Icons.Filled.Reorder
                            Icon(imageVector = icon, contentDescription = null, tint = if (state.isSortedByPriority) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(imageVector = Icons.Filled.FilterList, contentDescription = "Filtrar", tint = if (state.filterPriority != "TODAS") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                priorities.forEach { priority ->
                                    DropdownMenuItem(
                                        text = { Text(priority) },
                                        onClick = { viewModel.setFilterPriority(priority); expanded = false },
                                        trailingIcon = if (state.filterPriority == priority) { { Icon(Icons.Filled.Check, contentDescription = null) } } else null
                                    )
                                }
                            }
                        }
                        IconButton(onClick = onNavigateToCompleted, modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }) {
                            Icon(imageVector = Icons.Filled.Checklist, contentDescription = "Ver Completadas")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFADD8E6), titleContentColor = Color.Black)
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNavigateToForm) { Icon(Icons.Filled.Add, contentDescription = "Añadir Tarea") }
            }
        ) { padding ->
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(state.tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggleCompletion = { t ->
                                viewModel.toggleTaskCompletion(t)
                                if (!t.isCompleted) { scaleTrigger = true; scope.launch { delay(300); scaleTrigger = false } }
                            },
                            onDelete = viewModel::deleteTask,
                            onViewDetails = onViewDetails
                        )
                    }
                }
                if (state.tasks.isEmpty() && !state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text("¡No tienes tareas pendientes!", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggleCompletion: (Task) -> Unit, onDelete: (Int) -> Unit, onViewDetails: (Int) -> Unit) {
    val cardColor = when (task.priority) {
        "ALTA" -> PriorityHigh.copy(alpha = 0.2f)
        "MEDIA" -> PriorityMedium.copy(alpha = 0.2f)
        else -> PriorityLow.copy(alpha = 0.2f)
    }
    var isShaking by remember { mutableStateOf(false) }
    val shakeAngle by animateFloatAsState(targetValue = if (isShaking) 10f else 0f, animationSpec = repeatable(iterations = 10, animation = tween(50), repeatMode = RepeatMode.Reverse))
    LaunchedEffect(isShaking) { if (isShaking) { delay(500); isShaking = false; onDelete(task.id) } }

    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = cardColor), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggleCompletion(task) })
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(text = task.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Prioridad: ${task.priority}", style = MaterialTheme.typography.bodySmall, color = PriorityAccentColor)
            }
            IconButton(onClick = { onViewDetails(task.id) }) { Icon(Icons.Filled.Details, contentDescription = "Detalles") }
            IconButton(onClick = { isShaking = true }, modifier = Modifier.graphicsLayer { rotationZ = shakeAngle }) { Icon(Icons.Filled.Delete, contentDescription = "Eliminar") }
        }
    }
}
