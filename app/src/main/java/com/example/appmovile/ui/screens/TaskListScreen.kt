package com.example.appmovile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.appmovile.domain.models.Task
import com.example.appmovile.ui.components.DrawerContent
import com.example.appmovile.ui.theme.PriorityAccentColor
import com.example.appmovile.ui.theme.PriorityHigh
import com.example.appmovile.ui.theme.PriorityLow
import com.example.appmovile.ui.theme.PriorityMedium
import com.example.appmovile.ui.viewmodels.TaskListViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Sort // Ícono de Ordenar
import androidx.compose.material.icons.filled.FilterList // Ícono de Filtrar
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    onNavigateToForm: () -> Unit,
    onNavigateToCompleted: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onViewDetails: (Int) -> Unit,
) {
    val state = viewModel.state.collectAsState().value

    var expanded by remember { mutableStateOf(false) }
    val priorities = listOf("TODAS", "ALTA", "MEDIA", "BAJA")

    // ESTADOS LOCALES para controlar el menú
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // CONEXIÓN DE LA LÓGICA DEL MENÚ
            DrawerContent(
                onOptionClicked = { option ->
                    scope.launch { drawerState.close() } // Cierra el menú al seleccionar una opción
                    // Lógica de Navegación Local
                    when (option) {
                        "Login" -> onNavigateToAuth()
                        "Lista de Tareas" -> {}
                        "Tareas Completadas" -> onNavigateToCompleted()
                        "Agregar Tarea" -> onNavigateToForm()
                        "Cerrar Sesión" -> onNavigateToAuth()
                    }
                },
                weatherState = state.weatherState
            )
        }
    ) {
        //CONTENIDO DE LA PANTALLA PRINCIPAL
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
                        // 1. BOTÓN DE ORDENAR POR PRIORIDAD
                        IconButton(onClick = viewModel::toggleSortByPriority) {
                            Icon(
                                imageVector = Icons.Filled.Sort,
                                contentDescription = "Ordenar por Prioridad",
                                // Resalta si el ordenamiento está activo (IL 2.2)
                                tint = if (state.isSortedByPriority) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // 2. BOTÓN DE FILTRAR (Menú Desplegable)
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.FilterList,
                                    contentDescription = "Filtrar por Prioridad",
                                    // Resalta si hay un filtro activo (no es 'TODAS')
                                    tint = if (state.filterPriority != "TODAS") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                priorities.forEach { priority ->
                                    DropdownMenuItem(
                                        text = { Text(priority) },
                                        onClick = {
                                            viewModel.setFilterPriority(priority) // ⬅️ Llama a la función de filtrado
                                            expanded = false // Cierra el menú
                                        },
                                        // Muestra un check en el elemento seleccionado
                                        trailingIcon = if (state.filterPriority == priority) {
                                            { Icon(Icons.Filled.Check, contentDescription = "Seleccionado") }
                                        } else null
                                    )
                                }
                            }
                        } // Fin del Box para el Dropdown
                        // BOTÓN HISTORIAL
                        IconButton(onClick = onNavigateToCompleted) {
                            Icon(
                                imageVector = Icons.Filled.Checklist,
                                contentDescription = "Ver Tareas Completadas"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFADD8E6), // Celeste claro

                        titleContentColor = Color.Black
                    )
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
                            onDelete = viewModel::deleteTask,
                            onViewDetails = onViewDetails
                        )
                    }
                }
                if (state.tasks.isEmpty() && !state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "¡No tienes tareas pendientes!",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleCompletion: (Task) -> Unit,
    onDelete: (Int) -> Unit,
    onViewDetails: (Int) -> Unit
) {

    val cardColor = when (task.priority) {
        "ALTA" -> PriorityHigh.copy(alpha = 0.2f) // 20% de opacidad para el fondo
        "MEDIA" -> PriorityMedium.copy(alpha = 0.2f) // 30% de opacidad
        else -> PriorityLow.copy(alpha = 0.2f)
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
            // Checkbox para completar
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
                    text = "Prioridad: ${task.priority}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PriorityAccentColor
                )
            }
            //Boton Detalles
            IconButton(onClick = { onViewDetails(task.id) }) { Icon(Icons.Filled.Details, contentDescription = "Eliminar" ) }
            // Botón de eliminar
            IconButton(onClick = { onDelete(task.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
        }
    }
}