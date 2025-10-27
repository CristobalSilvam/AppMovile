package com.example.appmovile.data.local

import com.example.appmovile.data.local.models.TaskEntity
import com.example.appmovile.domain.models.Task

// Convierte de Entity (DB) a Domain (Negocio/UI)
fun TaskEntity.toDomain(): Task {
    return Task(
        id = this.id,
        title = this.title,
        priority = this.priority,
        isCompleted = this.isCompleted
    )
}

// Convierte de Domain (Negocio/UI) a Entity (DB)
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = this.id,
        title = this.title,
        priority = this.priority,
        isCompleted = this.isCompleted
    )
}