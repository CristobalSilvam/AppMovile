package com.example.appmovile.domain.models

// Esta clase es la que usará el ViewModel
data class Task(
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val priority: String,
    val isCompleted: Boolean,
    //alarma
    val reminderTime: Long? = null
)