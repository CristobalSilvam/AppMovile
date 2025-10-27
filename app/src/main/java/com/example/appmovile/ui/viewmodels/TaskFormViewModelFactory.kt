package com.example.appmovile.ui.viewmodels

// ui/viewmodels/TaskFormViewModel.kt y TaskListViewModel.kt
// (Añade esta clase DENTRO del archivo del ViewModel correspondiente)

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appmovile.domain.use_cases.SaveTaskUseCase


// ⬇️ Fábrica para TaskFormViewModel (Ejemplo)
class TaskFormViewModelFactory(
    private val saveTaskUseCase: SaveTaskUseCase,
    private val applicationContext: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskFormViewModel(saveTaskUseCase, applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
