package com.example.appmovile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmovile.ui.viewmodels.WeatherState

@Composable
fun DrawerContent(onOptionClicked: (String) -> Unit, weatherState: WeatherState) {

    val menuItems = listOf(
        Pair("Login", Icons.Default.AccountCircle),
        Pair("Lista de Tareas", Icons.Default.Notifications),
        Pair("Tareas Completadas", Icons.Default.Checklist),
        Pair("Agregar Tarea", Icons.Default.Add),
        Pair("Cerrar Sesión", Icons.Default.Close)
    )
    Surface (
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxHeight().fillMaxWidth(0.7f)
    ) {
        Column(modifier = Modifier.fillMaxHeight().padding(16.dp)) {
            when (weatherState) {
                is WeatherState.Loading -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Cargando Clima...", style = MaterialTheme.typography.titleMedium)
                    }
                }
                is WeatherState.Success -> {
                    // Muestra la temperatura y descripción
                    Text(
                        text = weatherState.temperature.toInt().toString() + "°C",
                        style = MaterialTheme.typography.headlineLarge, // Fuente grande
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = weatherState.description.capitalize(), // Muestra la descripción
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(16.dp))
                }
                is WeatherState.Error -> {
                    Text("Clima no disponible.", color = MaterialTheme.colorScheme.error)
                }
            }
            Text(
                "Bienvenido(a)",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            menuItems.forEach { (title, icon) ->
                NavigationDrawerItem(
                    label = { Text(title) },
                    icon = { Icon(icon, contentDescription = title) },
                    selected = title == "Lista de Tareas",
                    onClick = {
                        onOptionClicked(title)
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Empuja el contenido inferior hacia abajo

        }
    }
}