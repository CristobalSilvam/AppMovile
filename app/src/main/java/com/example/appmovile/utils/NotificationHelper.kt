// utils/NotificationHelper.kt
package com.example.appmovile.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.appmovile.MainActivity
import com.example.appmovile.R

const val CHANNEL_ID = "task_reminder_channel"
const val CHANNEL_NAME = "Recordatorios de Tareas"


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
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("¡Tarea Pendiente!")
        .setContentText(title)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent) // Lanza la intención al hacer clic
        .setAutoCancel(true)

    // Lanza la notificación usando el taskId para que sea única
    notificationManager.notify(taskId, builder.build())
}