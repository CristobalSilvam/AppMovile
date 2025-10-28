package com.example.appmovile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appmovile.ui.screens.TaskFormScreen
import com.example.appmovile.ui.screens.TaskListScreen
import com.example.appmovile.ui.theme.AppMovileTheme
import com.example.appmovile.ui.viewmodels.TaskListViewModel
import com.example.appmovile.ui.viewmodels.TaskListViewModelFactory
import com.example.appmovile.ui.viewmodels.TaskFormViewModel
import com.example.appmovile.ui.viewmodels.TaskFormViewModelFactory
import com.example.appmovile.di.AppContainer // Interfaz del contenedor DI
import com.example.appmovile.ui.screens.CompletedTasksScreen
import com.example.appmovile.ui.screens.TaskDetailScreen
import com.example.appmovile.ui.viewmodels.CompletedTasksViewModel
import com.example.appmovile.ui.viewmodels.CompletedTasksViewModelFactory
import com.example.appmovile.ui.viewmodels.TaskDetailViewModel
import com.example.appmovile.ui.viewmodels.TaskDetailViewModelFactory
import com.example.appmovile.utils.createNotificationChannel // Helper de Notificación
import com.example.appmovile.utils.RequestNotificationPermission // Helper de Permiso

import com.example.appmovile.ui.screens.AuthScreen
import com.example.appmovile.ui.viewmodels.AuthViewModel
import com.example.appmovile.ui.viewmodels.AuthViewModelFactory

// Definición de Rutas de Navegación
object Destinations {
    const val AUTH = "auth"
    const val TASK_LIST = "task_list"
    const val TASK_FORM = "task_form"

    const val COMPLETED_TASKS = "completed_tasks"

    const val TASK_DETAIL = "task_detail/{taskId}"
    fun taskDetailRoute(taskId: Int) = "task_detail/$taskId"
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    // Accede a la instancia única del contenedor (Patrón Service Locator)
    private val appContainer: AppContainer
        get() = (application as AppMovileApp).container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el canal de notificación (Requisito IL 2.4)
        createNotificationChannel(this)

        setContent {
            AppMovileTheme {
                MyAppNavigation(appContainer = appContainer)
            }
        }
    }
}

// Composable que gestiona la Navegación y la Inyección de Dependencias
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppNavigation(appContainer: AppContainer) {
    val navController = rememberNavController()

    // Recurso Nativo: Solicita el permiso POST_NOTIFICATIONS
    RequestNotificationPermission()

    // Inicialización de Fábricas

    // Fábrica de Listado (Inyecta Obtener, Borrar, y Actualizar Estado)
    val taskListViewModelFactory = remember {
        TaskListViewModelFactory(
            getTaskUseCase = appContainer.getTaskUseCase,
            deleteTaskUseCase = appContainer.deleteTaskUseCase,
            updateTaskStatusUseCase = appContainer.updateTaskStatusUseCase
        )
    }

    // Fábrica de Formulario (Inyecta Guardar y el Contexto para Recursos Nativos)
    val taskFormViewModelFactory = remember {
        TaskFormViewModelFactory(
            saveTaskUseCase = appContainer.saveTaskUseCase,
            applicationContext = appContainer.applicationContext
        )
    }
    val completedTasksViewModelFactory = remember {
        CompletedTasksViewModelFactory(
            getCompletedTasksUseCase = appContainer.getCompletedTasksUseCase
        )
    }
    val authViewModelFactory = remember {
        AuthViewModelFactory(
            registerUserUseCase = appContainer.registerUserUseCase,
            loginUserUseCase = appContainer.loginUserUseCase
        )
    }

    NavHost(
        navController = navController,
        startDestination = Destinations.AUTH // Punto de inicio (IL 2.1)
    ) {
        // Pantalla de Listado (Tasks List)
        composable(Destinations.TASK_LIST) {
            // Inyecta el ViewModel usando la fábrica
            val viewModel: TaskListViewModel = viewModel(factory = taskListViewModelFactory)
            TaskListScreen(
                viewModel = viewModel,
                onNavigateToForm = {navController.navigate(Destinations.TASK_FORM)},
                onNavigateToCompleted = { navController.navigate(Destinations.COMPLETED_TASKS) },
                onNavigateToAuth = { navController.navigate(Destinations.AUTH) },
                onViewDetails = { taskId -> navController.navigate(Destinations.taskDetailRoute(taskId))
                }
            )
        }

        // Pantalla de Formulario (Add/Edit Task)
        composable(Destinations.TASK_FORM) {
            // Inyecta el ViewModel usando la fábrica
            val viewModel: TaskFormViewModel = viewModel(factory = taskFormViewModelFactory)

            // Llama a la pantalla con el ViewModel y el callback de éxito
            TaskFormScreen(
                taskFormViewModel = viewModel,
                onSaveSuccess = {
                    // Navega de vuelta a la lista cuando el ViewModel indica guardado exitoso
                    navController.popBackStack()
                }
            )
        }
        //historial
        composable(Destinations.COMPLETED_TASKS) {
            val viewModel: CompletedTasksViewModel = viewModel(factory = completedTasksViewModelFactory)
            CompletedTasksScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        //detalles
        composable(
            route = Destinations.TASK_DETAIL,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            // 1. Extrae el ID de la tarea de la URL
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable

            // 2. Crea la fábrica inyectando el ID y los Use Cases
            val taskDetailViewModelFactory = remember {
                TaskDetailViewModelFactory(
                    taskId = taskId,
                    getTaskDetailsUseCase = appContainer.getTaskDetailsUseCase,
                    updateTaskStatusUseCase = appContainer.updateTaskStatusUseCase
                )
            }
            val viewModel: TaskDetailViewModel = viewModel(factory = taskDetailViewModelFactory)

            TaskDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        //login
        composable(Destinations.AUTH) {
            val viewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    // Navega a la lista principal y limpia la pila de atrás
                    navController.navigate(Destinations.TASK_LIST) {
                        popUpTo(Destinations.AUTH) { inclusive = true } // Evita volver al login con el botón 'atrás'
                    }
                }
            )
        }
    }
}