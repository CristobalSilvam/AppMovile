// utils/PermissionHelper.kt
package com.example.appmovile.utils
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        val context = LocalContext.current

        // El launcher se encarga de mostrar la ventana de permiso
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
            }
        )

        LaunchedEffect(key1 = Unit) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            launcher.launch(permission)
        }
    }
}