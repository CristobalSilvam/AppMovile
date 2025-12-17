package com.example.appmovile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmovile.domain.models.Task
import com.example.appmovile.ui.theme.PriorityHigh
import com.example.appmovile.ui.theme.PriorityLow
import com.example.appmovile.ui.theme.PriorityMedium

@Composable
fun EditTaskScreen(
    task: Task,
    onSave: (Task) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description ?: "") }
    var location by remember { mutableStateOf(task.location ?: "") }
    var priority by remember { mutableStateOf(task.priority) }
    var isCompleted by remember { mutableStateOf(task.isCompleted) }
    var reminderTime by remember { mutableStateOf(task.reminderTime?.toString() ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Selector de Prioridad
        Text("Prioridad:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { priority = "ALTA" },
                colors = ButtonDefaults.buttonColors(containerColor = if (priority == "ALTA") PriorityHigh else MaterialTheme.colorScheme.secondaryContainer)
            ) { Text("Alta") }

            Button(
                onClick = { priority = "MEDIA" },
                colors = ButtonDefaults.buttonColors(containerColor = if (priority == "MEDIA") PriorityMedium else MaterialTheme.colorScheme.secondaryContainer)
            ) { Text("Media") }

            Button(
                onClick = { priority = "BAJA" },
                colors = ButtonDefaults.buttonColors(containerColor = if (priority == "BAJA") PriorityLow else MaterialTheme.colorScheme.secondaryContainer)
            ) { Text("Baja") }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { isCompleted = it }
            )
            Text("Completed")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val updatedTask = task.copy(
                    title = title,
                    description = description,
                    location = location,
                    priority = priority,
                    isCompleted = isCompleted,
                    reminderTime = reminderTime.toLongOrNull()
                )
                onSave(updatedTask)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}