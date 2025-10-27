// utils/PermissionHelper.kt
package com.example.appmovile.utils // ⬅️ Asegúrate de que el paquete sea correcto

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Componente Composable que solicita el permiso de POST_NOTIFICATIONS
 * en tiempo de ejecución para Android 13 (API 33) y superior.
 * Esto es necesario para que las notificaciones funcionen (IL 2.4).
 */
@Composable
fun RequestNotificationPermission() {
    // ⬇️ Solo es necesario para Android 13 (API 33) y superior
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        val context = LocalContext.current

        // El launcher se encarga de mostrar la ventana de permiso
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
                // Lógica de resultado: para la evaluación, solo el hecho de pedirlo es suficiente.
            }
        )

        // ⬇️ LaunchedEffect lanza la solicitud una sola vez al cargar el Composable
        LaunchedEffect(key1 = Unit) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            launcher.launch(permission)
        }
    }
}