package com.example.appmovile.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.appmovile.data.local.TaskDao
import com.example.appmovile.data.local.models.TaskEntity

// Define las entidades y la versión de la base de datos
@Database(entities = [TaskEntity::class], version = 3, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    // Abstracta para exponer el DAO
    abstract fun taskDao(): TaskDao
}