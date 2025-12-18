package com.example.appmovile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuScreen(
    onNavigateToUsers: () -> Unit,
    onNavigateToGroups: () -> Unit,
    onNavigateToMyTasks: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Control Admin") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AdminOptionCard("Administrar Usuarios", Icons.Filled.People, onNavigateToUsers)
            Spacer(Modifier.height(16.dp))
            AdminOptionCard("Administrar Grupos", Icons.Filled.Groups, onNavigateToGroups)
            Spacer(Modifier.height(16.dp))
            AdminOptionCard("Volver a Mis Tareas", Icons.Filled.TaskAlt, onNavigateToMyTasks)
        }
    }
}

@Composable
fun AdminOptionCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(0.8f).height(80.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
    }
}
