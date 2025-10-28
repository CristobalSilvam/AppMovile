// data/local/models/TaskEntity.kt
package com.example.appmovile.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    // Clave primaria autogenerada (necesaria para el almacenamiento local)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    @ColumnInfo(name = "Descriptión") val description: String? = null, // Opcional
    @ColumnInfo(name = "Location") val location: String? = null,
    val priority: String, // ALTA, MEDIA, BAJA
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean,
    @ColumnInfo(name = "reminder_time") val reminderTime: Long?
)