// data/local/models/TaskEntity.kt
package com.example.appmovile.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    // Clave primaria autogenerada (necesaria para el almacenamiento local)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String? = null, // Opcional
    val priority: String, // ALTA, MEDIA, BAJA
    val isCompleted: Boolean = false
)