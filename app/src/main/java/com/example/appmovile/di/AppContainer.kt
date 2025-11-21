package com.example.appmovile.di

import android.content.Context
// 1. Quita las importaciones de Room (o déjalas si aún las usas para algo)
// import androidx.room.Room
// import com.example.appmovile.data.local.AuthDao
// import com.example.appmovile.data.local.TaskDatabase
// import com.example.appmovile.data.local.TaskDao

// 2. Importa los Servicios de API
import com.example.appmovile.data.remote.AuthApiService
import com.example.appmovile.data.remote.TaskApiService
import com.example.appmovile.data.remote.WeatherApiService

// 3. Importa las IMPLEMENTACIONES (Impl)
import com.example.appmovile.data.repositories.AuthRepositoryImpl
import com.example.appmovile.data.repositories.TaskRepositoryImpl
// 4. Importa las INTERFACES
import com.example.appmovile.domain.repositories.AuthRepository
import com.example.appmovile.domain.repositories.TaskRepository
import com.example.appmovile.domain.repositories.WeatherRepository
import com.example.appmovile.domain.repositories.WeatherRepositoryImpl
// 5. Importa TODOS los Use Cases
import com.example.appmovile.domain.use_cases.*

// 6. Importa Retrofit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

// 7. --- INTERFAZ (Asegúrate que estén TODOS los val) ---
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
    val registerUserUseCase: RegisterUserUseCase
    val loginUserUseCase: LoginUserUseCase

    val weatherRepository: WeatherRepository
    val getWeatherUseCase: GetWeatherUseCase
}

// 8. --- IMPLEMENTACIÓN (AppDataContainer) ---
class AppDataContainer(private val context: Context) : AppContainer {

    private val BASE_URL = "http://10.0.2.2:8080/"


    //api externa
    private val WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/"

    private val client: OkHttpClient by lazy { // Resuelve el error 'Unresolved reference: client'
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val externalRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_API_URL) // Usamos la URL del clima
            .client(client) // Reutilizamos el cliente OkHttpClient con logging
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- Servicios de API ---
    // ... (authApiService, taskApiService)
    private val weatherApiService: WeatherApiService by lazy {
        externalRetrofit.create(WeatherApiService::class.java) // Servicio de clima
    }

    // --- Repositorios ---
    // ... (taskRepository, authRepository)
    override val weatherRepository: WeatherRepository by lazy {
        WeatherRepositoryImpl(weatherApiService) // Repositorio de Clima
    }

    // --- Casos de Uso ---
    // ... (otros UseCases)
    override val getWeatherUseCase: GetWeatherUseCase by lazy {
        GetWeatherUseCase(weatherRepository) // Use Case de Clima
    }

    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- Servicios de API ---
    private val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
    private val taskApiService: TaskApiService by lazy {
        retrofit.create(TaskApiService::class.java)
    }

    override val applicationContext: Context get() = context

    // --- Repositorios (Inyecta API Service) ---
    override val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(taskApiService)
    }
    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApiService)
    }

    // 9. --- Casos de Uso (TODOS deben tener 'override val') ---
    override val getTaskUseCase: GetTaskUseCase by lazy {
        GetTaskUseCase(taskRepository)
    }
    override val saveTaskUseCase: SaveTaskUseCase by lazy {
        SaveTaskUseCase(taskRepository)
    }
    override val deleteTaskUseCase: DeleteTaskUseCase by lazy {
        DeleteTaskUseCase(taskRepository)
    }
    override val updateTaskStatusUseCase: UpdateTaskStatusUseCase by lazy {
        UpdateTaskStatusUseCase(taskRepository)
    }
    override val getCompletedTasksUseCase: GetCompletedTasksUseCase by lazy {
        GetCompletedTasksUseCase(taskRepository)
    }
    override val getTaskDetailsUseCase: GetTaskDetailsUseCase by lazy {
        GetTaskDetailsUseCase(taskRepository)
    }
    override val registerUserUseCase: RegisterUserUseCase by lazy {
        RegisterUserUseCase(authRepository)
    }
    override val loginUserUseCase: LoginUserUseCase by lazy {
        LoginUserUseCase(authRepository)
    }
}