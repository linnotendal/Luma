package com.example.luma

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.luma.data.OnboardingPreferences
import com.example.luma.data.model.Task
import com.example.luma.ui.checkin.CheckInScreen
import com.example.luma.ui.home.HomeScreen
import com.example.luma.ui.insights.InsightsScreen
import com.example.luma.ui.onboarding.OnboardingScreen
import com.example.luma.ui.tasks.AddTaskScreen
import com.example.luma.ui.tasks.TasksScreen
import androidx.compose.material.icons.automirrored.filled.List
import kotlinx.coroutines.launch
import com.example.luma.ui.theme.NavBar

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Default.Home)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object CheckIn : Screen("checkin", "Check-in", Icons.Default.CheckCircle)
    object Tasks : Screen("tasks", "Tasks", Icons.AutoMirrored.Filled.List)
    object AddTask : Screen("add_task", "Add Task", Icons.AutoMirrored.Filled.List)
    object Insights : Screen("insights", "Insights", Icons.Default.BarChart)
}

val bottomNavScreens = listOf(Screen.Home, Screen.CheckIn, Screen.Tasks, Screen.Insights)

@Composable
fun LumaNavigation() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    // Håller den valda tasken i minnet för redigering
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    // Avgör om onboarding redan har visats. null = vi vet inte än.
    val context = LocalContext.current
    val onboardingPreferences = remember { OnboardingPreferences(context) }
    val hasSeenOnboarding by onboardingPreferences.hasSeenOnboarding.collectAsState(initial = null)

    // Vänta tills vi vet, annars kan vi flasha fel startskärm
    if (hasSeenOnboarding == null) {
        return
    }
    val startDestination = if (hasSeenOnboarding == true) Screen.Home.route else Screen.Onboarding.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.AddTask.route && currentRoute != Screen.Onboarding.route) {
                NavigationBar(
                    containerColor = NavBar,
                    tonalElevation = 0.dp
                ) {
                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                val coroutineScope = rememberCoroutineScope()
                HomeScreen(
                    onStartCheckIn = { navController.navigate(Screen.CheckIn.route) },
                    onGoToTasks = { navController.navigate(Screen.Tasks.route) },
                    onShowOnboarding = {
                        coroutineScope.launch {
                            onboardingPreferences.resetOnboarding()
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(Screen.CheckIn.route) {
                CheckInScreen(
                    onCheckInSaved = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Tasks.route) {
                TasksScreen(
                    onAddTask = {
                        taskToEdit = null
                        navController.navigate(Screen.AddTask.route)
                    },
                    onEditTask = { task ->
                        taskToEdit = task
                        navController.navigate(Screen.AddTask.route)
                    }
                )
            }
            composable(Screen.AddTask.route) {
                AddTaskScreen(
                    onTaskSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                    existingTask = taskToEdit
                )
            }
            composable(Screen.Insights.route) {
                InsightsScreen()
            }
        }
    }
}