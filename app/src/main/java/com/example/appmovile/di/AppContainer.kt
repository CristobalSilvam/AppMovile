package com.example.appmovile.di

import android.content.Context
import com.example.appmovile.data.remote.AuthApiService
import com.example.appmovile.data.remote.TaskApiService
import com.example.appmovile.data.remote.WeatherApiService
import com.example.appmovile.data.remote.GroupApiService
import com.example.appmovile.data.repositories.AuthRepositoryImpl
import com.example.appmovile.data.repositories.TaskRepositoryImpl
import com.example.appmovile.domain.repositories.AuthRepository
import com.example.appmovile.domain.repositories.TaskRepository
import com.example.appmovile.domain.repositories.WeatherRepository
import com.example.appmovile.domain.repositories.WeatherRepositoryImpl
import com.example.appmovile.domain.use_cases.*
import com.example.appmovile.data.local.UserPreferencesRepository
import com.example.appmovile.data.remote.UserApiService
import com.example.appmovile.data.repositories.UserRepositoryImpl
import com.example.appmovile.domain.repositories.UserRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

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
    val logoutUseCase: LogoutUseCase
    val weatherRepository: WeatherRepository
    val getWeatherUseCase: GetWeatherUseCase
    val userRepository: UserRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val BASE_URL = "http://192.168.1.91:8081/"
    private val WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/"

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val externalRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val userPreferencesRepository = UserPreferencesRepository(context)

    private val authApiService: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    private val taskApiService: TaskApiService by lazy { retrofit.create(TaskApiService::class.java) }
    private val weatherApiService: WeatherApiService by lazy { externalRetrofit.create(WeatherApiService::class.java) }
    private val userApiService: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
    private val groupApiService: GroupApiService by lazy { retrofit.create(GroupApiService::class.java) }

    override val applicationContext: Context get() = context

    override val taskRepository: TaskRepository by lazy { TaskRepositoryImpl(taskApiService, userPreferencesRepository) }
    override val authRepository: AuthRepository by lazy { AuthRepositoryImpl(authApiService, userPreferencesRepository) }
    override val weatherRepository: WeatherRepository by lazy { WeatherRepositoryImpl(weatherApiService) }
    override val userRepository: UserRepository by lazy { UserRepositoryImpl(userApiService, groupApiService, userPreferencesRepository) }

    override val getTaskUseCase: GetTaskUseCase by lazy { GetTaskUseCase(taskRepository) }
    override val saveTaskUseCase: SaveTaskUseCase by lazy { SaveTaskUseCase(taskRepository) }
    override val deleteTaskUseCase: DeleteTaskUseCase by lazy { DeleteTaskUseCase(taskRepository) }
    override val updateTaskStatusUseCase: UpdateTaskStatusUseCase by lazy { UpdateTaskStatusUseCase(taskRepository) }
    override val getCompletedTasksUseCase: GetCompletedTasksUseCase by lazy { GetCompletedTasksUseCase(taskRepository) }
    override val getTaskDetailsUseCase: GetTaskDetailsUseCase by lazy { GetTaskDetailsUseCase(taskRepository) }
    override val registerUserUseCase: RegisterUserUseCase by lazy { RegisterUserUseCase(authRepository) }
    override val loginUserUseCase: LoginUserUseCase by lazy { LoginUserUseCase(authRepository) }
    override val logoutUseCase: LogoutUseCase by lazy { LogoutUseCase(authRepository) }
    override val getWeatherUseCase: GetWeatherUseCase by lazy { GetWeatherUseCase(weatherRepository) }
}
