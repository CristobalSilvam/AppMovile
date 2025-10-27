// utils/NotificationHelper.kt
package com.example.appmovile.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.appmovile.R // Requiere un recurso en res/drawable, como un ícono
import com.example.appmovile.MainActivity // Clase principal para abrir al tocar

const val CHANNEL_ID = "task_reminder_channel"
const val CHANNEL_NAME = "Recordatorios de Tareas"

/**
 * Crea el canal de notificación. Obligatorio para Android 8.0 (API 26) y superior.
 * Esto debe llamarse al inicio de la aplicación (ej., en MainActivity.onCreate).
 */
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = "Notificaciones para recordarte tareas pendientes"
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

/**
 * Muestra una notificación simple para recordar una tarea.
 * @param taskId El ID de la tarea, usado como ID de la notificación para permitir la actualización.
 * @param title El título de la tarea a mostrar en el cuerpo de la notificación.
 */
fun showSimpleNotification(context: Context, taskId: Int, title: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Intención que se lanza al hacer clic en la notificación (vuelve a abrir la app)
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0, // Código de solicitud
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Flags necesarios
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        // ⬇️ Necesitas tener un archivo R.drawable.ic_notification.xml en res/drawable
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("¡Tarea Pendiente!")
        .setContentText(title)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent) // Lanza la intención al hacer clic
        .setAutoCancel(true)

    // Lanza la notificación usando el taskId para que sea única
    notificationManager.notify(taskId, builder.build())
}