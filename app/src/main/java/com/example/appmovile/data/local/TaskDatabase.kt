package com.example.appmovile.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.appmovile.data.local.TaskDao
import com.example.appmovile.data.local.models.TaskEntity
import com.example.appmovile.data.local.models.UserEntity

// Define las entidades y la versión de la base de datos
@Database(entities = [TaskEntity::class, UserEntity::class], version = 6, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    // Abstracta para exponer el DAO
    abstract fun taskDao(): TaskDao
    abstract fun authDao(): AuthDao
}