package com.example.appmovile.di

import android.content.Context
import androidx.room.Room
import com.example.appmovile.data.local.AuthDao
import com.example.appmovile.data.local.TaskDatabase
import com.example.appmovile.data.local.TaskDao
import com.example.appmovile.data.repositories.AuthRepositoryImpl
import com.example.appmovile.data.repositories.TaskRepositoryImpl
import com.example.appmovile.domain.repositories.AuthRepository
import com.example.appmovile.domain.repositories.TaskRepository
import com.example.appmovile.domain.use_cases.DeleteTaskUseCase
import com.example.appmovile.domain.use_cases.GetCompletedTasksUseCase
import com.example.appmovile.domain.use_cases.GetTaskDetailsUseCase
import com.example.appmovile.domain.use_cases.GetTaskUseCase
import com.example.appmovile.domain.use_cases.LoginUserUseCase
import com.example.appmovile.domain.use_cases.RegisterUserUseCase
import com.example.appmovile.domain.use_cases.SaveTaskUseCase
import com.example.appmovile.domain.use_cases.UpdateTaskStatusUseCase

interface AppContainer {
    val taskRepository: TaskRepository
    val authRepository: AuthRepository


    val getTaskUseCase: GetTaskUseCase
    val saveTaskUseCase: SaveTaskUseCase
    val applicationContext: Context
    val deleteTaskUseCase: DeleteTaskUseCase
    val updateTaskStatusUseCase: UpdateTaskStatusUseCase
    val getCompletedTasksUseCase: GetCompletedTasksUseCase
    val getTaskDetailsUseCase: GetTaskDetailsUseCase
    val registerUserUseCase: RegisterUserUseCase // ⬅️ NUEVO: Use Case de Registro
    val loginUserUseCase: LoginUserUseCase
}

class AppDataContainer(private val context: Context) : AppContainer {
    // Inicialización de la Base de Datos
    private val database: TaskDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            TaskDatabase::class.java,
            "task_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    // Inicialización de DAOs
    private val taskDao: TaskDao by lazy {database.taskDao()}
    private val authDao: AuthDao by lazy { database.authDao() }


    override val applicationContext: Context get() = context

    //Repositorios (usa el DAO)
    override val taskRepository: TaskRepository by lazy { TaskRepositoryImpl(taskDao) }
    override val authRepository: AuthRepository by lazy { AuthRepositoryImpl(authDao) }


    // Casos de Uso Task (usan el Repositorio)
    override val getTaskUseCase: GetTaskUseCase by lazy { GetTaskUseCase(taskRepository) }
    override val saveTaskUseCase: SaveTaskUseCase by lazy { SaveTaskUseCase(taskRepository) }
    override val deleteTaskUseCase: DeleteTaskUseCase by lazy { DeleteTaskUseCase(taskRepository) }
    override val updateTaskStatusUseCase: UpdateTaskStatusUseCase by lazy { UpdateTaskStatusUseCase(taskRepository) }
    override val getCompletedTasksUseCase: GetCompletedTasksUseCase by lazy { GetCompletedTasksUseCase(taskRepository) }
    override val getTaskDetailsUseCase: GetTaskDetailsUseCase by lazy { GetTaskDetailsUseCase(taskRepository) }

    //Casos de uso Auth
    override val registerUserUseCase: RegisterUserUseCase by lazy { RegisterUserUseCase(authRepository) }
    override val loginUserUseCase: LoginUserUseCase by lazy { LoginUserUseCase(authRepository) }
}