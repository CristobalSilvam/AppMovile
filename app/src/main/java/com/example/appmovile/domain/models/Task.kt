package com.example.appmovile.domain.models

// Esta clase es la que usará el ViewModel
data class Task(
    val id: Int = 0,
    val title: String,
    val priority: String,
    val isCompleted: Boolean
)