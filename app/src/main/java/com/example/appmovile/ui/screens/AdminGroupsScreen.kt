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
import com.example.appmovile.ui.viewmodels.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminGroupsScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showAddMemberDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.selectedLeaderId == null) "Grupos de Trabajo" else "Miembros del Grupo") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.selectedLeaderId == null) onNavigateBack()
                        else viewModel.selectLeader(null)
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (state.selectedLeaderId != null) {
                        IconButton(onClick = { showAddMemberDialog = true }) {
                            Icon(Icons.Filled.PersonAdd, contentDescription = "Añadir Miembro")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                if (state.selectedLeaderId == null) {
                    // LISTA DE LÍDERES (Representan los grupos)
                    if (state.leaders.isEmpty()) {
                        Text("No hay grupos creados. Cambia el rol de un usuario a LEADER para crear uno.", 
                            modifier = Modifier.align(Alignment.Center).padding(32.dp))
                    } else {
                        LazyColumn {
                            items(state.leaders) { leader ->
                                ListItem(
                                    headlineContent = { Text(leader.email) },
                                    supportingContent = { Text("Líder de Grupo") },
                                    leadingContent = { Icon(Icons.Filled.Group, null) },
                                    trailingContent = {
                                        IconButton(onClick = { viewModel.deleteGroup(leader.id) }) {
                                            Icon(Icons.Filled.DeleteForever, contentDescription = "Eliminar Grupo", tint = Color.Red)
                                        }
                                    },
                                    modifier = Modifier.clickable { viewModel.selectLeader(leader.id) }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                } else {
                    // LISTA DE MIEMBROS DEL LÍDER SELECCIONADO
                    if (state.selectedGroupMembers.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Este líder aún no tiene miembros en su grupo.")
                            Button(onClick = { showAddMemberDialog = true }, modifier = Modifier.padding(top = 16.dp)) {
                                Text("Añadir Primer Miembro")
                            }
                        }
                    } else {
                        LazyColumn {
                            items(state.selectedGroupMembers) { member ->
                                ListItem(
                                    headlineContent = { Text(member.email) },
                                    supportingContent = { Text("Miembro") },
                                    leadingContent = { Icon(Icons.Filled.Person, null) },
                                    trailingContent = {
                                        IconButton(onClick = { 
                                            viewModel.removeMemberFromGroup(state.selectedLeaderId!!, member.id) 
                                        }) {
                                            Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Remover del Grupo", tint = Color.Red)
                                        }
                                    }
                                )
                                HorizontalDivider()
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
            AddMemberToGroupDialog(
                onDismiss = { showAddMemberDialog = false },
                onConfirm = { email ->
                    viewModel.addMemberToGroup(state.selectedLeaderId!!, email)
                    showAddMemberDialog = false
                }
            )
        }
    }
}

@Composable
fun AddMemberToGroupDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Miembro al Grupo") },
        text = {
            Column {
                Text("Ingresa el correo del usuario que deseas añadir a este grupo.")
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = email, 
                    onValueChange = { email = it }, 
                    label = { Text("Email del usuario") }, 
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { 
            Button(onClick = { if (email.isNotBlank()) onConfirm(email) }) { 
                Text("Añadir") 
            } 
        },
        dismissButton = { 
            TextButton(onClick = onDismiss) { 
                Text("Cancelar") 
            } 
        }
    )
}
