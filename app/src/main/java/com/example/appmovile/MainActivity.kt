package com.example.appmovile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.appmovile.di.AppContainer
import com.example.appmovile.ui.screens.AdminScreen
import com.example.appmovile.ui.screens.CompletedTasksScreen
import com.example.appmovile.ui.screens.EditTaskScreen
import com.example.appmovile.ui.screens.TaskDetailScreen
import com.example.appmovile.ui.viewmodels.CompletedTasksViewModel
import com.example.appmovile.ui.viewmodels.CompletedTasksViewModelFactory
import com.example.appmovile.ui.viewmodels.EditTaskViewModel
import com.example.appmovile.ui.viewmodels.EditTaskViewModelFactory
import com.example.appmovile.ui.viewmodels.TaskDetailViewModel
import com.example.appmovile.ui.viewmodels.TaskDetailViewModelFactory
import com.example.appmovile.utils.createNotificationChannel
import com.example.appmovile.utils.RequestNotificationPermission
import com.example.appmovile.ui.screens.AuthScreen
import com.example.appmovile.ui.viewmodels.AdminViewModel
import com.example.appmovile.ui.viewmodels.AdminViewModelFactory
import com.example.appmovile.ui.viewmodels.AuthViewModel
import com.example.appmovile.ui.viewmodels.AuthViewModelFactory

object Destinations {
    const val AUTH = "auth"
    const val TASK_LIST = "task_list"
    const val TASK_FORM = "task_form"
    const val COMPLETED_TASKS = "completed_tasks"
    const val TASK_DETAIL = "task_detail/{taskId}"
    const val EDIT_TASK = "edit_task/{taskId}"
    const val ADMIN_PANEL = "admin_panel"
    
    fun taskDetailRoute(taskId: Int) = "task_detail/$taskId"
    fun editTaskRoute(taskId: Int) = "edit_task/$taskId"
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val appContainer: AppContainer
        get() = (application as AppMovileApp).container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)

        setContent {
            AppMovileTheme {
                MyAppNavigation(appContainer = appContainer)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppNavigation(appContainer: AppContainer) {
    val navController = rememberNavController()

    RequestNotificationPermission()

    val taskListViewModelFactory = remember {
        TaskListViewModelFactory(
            getTaskUseCase = appContainer.getTaskUseCase,
            deleteTaskUseCase = appContainer.deleteTaskUseCase,
            updateTaskStatusUseCase = appContainer.updateTaskStatusUseCase,
            getWeatherUseCase = appContainer.getWeatherUseCase
        )
    }

    val taskFormViewModelFactory = remember {
        TaskFormViewModelFactory(
            saveTaskUseCase = appContainer.saveTaskUseCase,
            applicationContext = appContainer.applicationContext
        )
    }
    
    val completedTasksViewModelFactory = remember {
        CompletedTasksViewModelFactory(
            getCompletedTasksUseCase = appContainer.getCompletedTasksUseCase,
            updateTaskStatusUseCase = appContainer.updateTaskStatusUseCase
        )
    }
    
    val authViewModelFactory = remember {
        AuthViewModelFactory(
            registerUserUseCase = appContainer.registerUserUseCase,
            loginUserUseCase = appContainer.loginUserUseCase,
            logoutUseCase = appContainer.logoutUseCase,
            authRepository = appContainer.authRepository
        )
    }

    val editTaskViewModelFactory = remember {
        EditTaskViewModelFactory(
            updateTaskStatusUseCase = appContainer.updateTaskStatusUseCase
        )
    }

    // CORRECCIÓN: Inyectar ambos repositorios necesarios
    val adminViewModelFactory = remember {
        AdminViewModelFactory(
            userRepository = appContainer.userRepository,
            taskRepository = appContainer.taskRepository
        )
    }

    NavHost(
        navController = navController,
        startDestination = Destinations.AUTH
    ) {
        composable(Destinations.AUTH) {
            val viewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    navController.navigate(Destinations.TASK_LIST) {
                        popUpTo(Destinations.AUTH) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    (navController.context as? ComponentActivity)?.finish()
                }
            )
        }

        composable(Destinations.TASK_LIST) {
            val viewModel: TaskListViewModel = viewModel(factory = taskListViewModelFactory)
            TaskListScreen(
                navController = navController,
                viewModel = viewModel,
                authViewModelFactory = authViewModelFactory,
                onNavigateToForm = { navController.navigate(Destinations.TASK_FORM) },
                onNavigateToCompleted = { navController.navigate(Destinations.COMPLETED_TASKS) },
                onNavigateToAuth = {
                    navController.navigate(Destinations.AUTH) {
                        popUpTo(Destinations.TASK_LIST) { inclusive = true }
                    }
                },
                onViewDetails = { taskId ->
                    navController.navigate(Destinations.taskDetailRoute(taskId))
                }
            )
        }

        composable(Destinations.TASK_FORM) {
            val viewModel: TaskFormViewModel = viewModel(factory = taskFormViewModelFactory)
            TaskFormScreen(
                taskFormViewModel = viewModel,
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(Destinations.COMPLETED_TASKS) {
            val viewModel: CompletedTasksViewModel = viewModel(factory = completedTasksViewModelFactory)
            CompletedTasksScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Destinations.ADMIN_PANEL) {
            val viewModel: AdminViewModel = viewModel(factory = adminViewModelFactory)
            AdminScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Destinations.TASK_DETAIL,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Destinations.editTaskRoute(it)) }
            )
        }

        composable(
            route = Destinations.EDIT_TASK,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
            val taskDetailViewModel: TaskDetailViewModel = viewModel(
                factory = TaskDetailViewModelFactory(
                    taskId = taskId,
                    getTaskDetailsUseCase = appContainer.getTaskDetailsUseCase,
                    updateTaskStatusUseCase = appContainer.updateTaskStatusUseCase
                )
            )
            val editTaskViewModel: EditTaskViewModel = viewModel(factory = editTaskViewModelFactory)
            val task = taskDetailViewModel.state.collectAsState().value.task
            if (task != null) {
                editTaskViewModel.setTask(task)
                EditTaskScreen(
                    task = task,
                    onSave = {
                        editTaskViewModel.saveTask(it)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
