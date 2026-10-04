package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import com.example.ui.CivicViewModel
import com.example.ui.UiEvent
import androidx.compose.material.icons.filled.Psychology
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AiComputerVisionScreen
import com.example.ui.screens.CmHelplineAiScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GrievanceDetailScreen
import com.example.ui.screens.GrievanceListScreen
import com.example.ui.screens.LoginRegisterScreen
import com.example.ui.screens.MapHotspotScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RTIScreen
import com.example.ui.screens.SubmitGrievanceScreen
import androidx.compose.runtime.CompositionLocalProvider
import com.example.ui.components.LanguageSwitcherBar
import com.example.ui.components.PushNotificationBanner
import com.example.ui.theme.CivicConnectTheme
import com.example.ui.theme.PrimaryBlue
import com.example.util.LocalAppLanguage
import com.example.util.Strings
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: CivicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CivicConnectTheme {
                CivicConnectApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CivicConnectApp(viewModel: CivicViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentUser by viewModel.currentUser.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val wards by viewModel.allWards.collectAsState()
    val grievances by viewModel.allGrievances.collectAsState()
    val projects by viewModel.allProjects.collectAsState()
    val rtiRequests by viewModel.allRTIRequests.collectAsState()
    val selectedGrievance by viewModel.selectedGrievance.collectAsState()
    val escalations by viewModel.escalations.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    CompositionLocalProvider(LocalAppLanguage provides selectedLanguage) {
        // Handle UI Event Flow
        LaunchedEffect(key1 = true) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is UiEvent.ShowSnackbar -> {
                        launch {
                            snackbarHostState.showSnackbar(event.message)
                        }
                    }
                    is UiEvent.NavigateToHome -> {
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                    is UiEvent.NavigateToPetitions -> {
                        navController.navigate("grievances") {
                            popUpTo("submit") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                    else -> {}
                }
            }
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Show Bottom Bar & FAB only when logged in and on main routes
        val mainRoutes = listOf("dashboard", "grievances", "cmcell_ai", "map", "ai_vision", "rti", "profile", "admin", "submit")
        val showBottomBar = currentUser != null && currentRoute in mainRoutes

        val currentPushBanner by viewModel.currentPushBanner.collectAsState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                LanguageSwitcherBar(
                    onNotificationClick = { navController.navigate("notifications") }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        onClick = { navController.navigate("dashboard") },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text(Strings.get("dashboard", selectedLanguage), fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "grievances",
                        onClick = { navController.navigate("grievances") },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Grievances") },
                        label = { Text(Strings.get("grievances", selectedLanguage), fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "cmcell_ai",
                        onClick = { navController.navigate("cmcell_ai") },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "CM AI") },
                        label = { Text(Strings.get("cm_ai", selectedLanguage), fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "map",
                        onClick = { navController.navigate("map") },
                        icon = { Icon(Icons.Default.Map, contentDescription = "GIS Map") },
                        label = { Text(Strings.get("map_hotspots", selectedLanguage), fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "ai_vision",
                        onClick = { navController.navigate("ai_vision") },
                        icon = { Icon(Icons.Default.Psychology, contentDescription = "AI Vision") },
                        label = { Text("AI Vision", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = { navController.navigate("profile") },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text(Strings.get("profile", selectedLanguage), fontSize = 10.sp) }
                    )
                    if (currentUser?.role in listOf("ADMIN", "OFFICER")) {
                        NavigationBarItem(
                            selected = currentRoute == "admin",
                            onClick = { navController.navigate("admin") },
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                            label = { Text(Strings.get("admin", selectedLanguage), fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar && currentRoute != "submit") {
                FloatingActionButton(
                    onClick = { navController.navigate("submit") },
                    containerColor = PrimaryBlue
                ) {
                    Icon(Icons.Default.Add, contentDescription = "File Grievance", tint = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = if (currentUser != null) "dashboard" else "auth",
                modifier = Modifier.fillMaxSize()
            ) {
            composable("auth") {
                LoginRegisterScreen(
                    viewModel = viewModel,
                    wards = wards,
                    onLoginSuccess = {
                        navController.navigate("dashboard") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }

            composable("dashboard") {
                DashboardScreen(
                    viewModel = viewModel,
                    grievances = grievances,
                    onNavigateToSubmit = { navController.navigate("submit") },
                    onNavigateToMap = { navController.navigate("map") },
                    onNavigateToRti = { navController.navigate("rti") },
                    onNavigateToAdmin = { navController.navigate("admin") },
                    onNavigateToVision = { navController.navigate("ai_vision") },
                    onNavigateToNotifications = { navController.navigate("notifications") },
                    onGrievanceClick = { g ->
                        viewModel.selectGrievance(g)
                        navController.navigate("detail")
                    }
                )
            }

            composable("grievances") {
                GrievanceListScreen(
                    viewModel = viewModel,
                    grievances = grievances,
                    onGrievanceClick = { g ->
                        viewModel.selectGrievance(g)
                        navController.navigate("detail")
                    }
                )
            }

            composable("cmcell_ai") {
                CmHelplineAiScreen(
                    viewModel = viewModel,
                    onNavigateToSubmit = { navController.navigate("submit") }
                )
            }

            composable("ai_vision") {
                AiComputerVisionScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToSubmitWithData = { title, desc, cat, photoPath ->
                        viewModel.setPrefilledGrievance(title, desc, cat, photoPath)
                        navController.navigate("submit")
                    }
                )
            }

            composable("notifications") {
                NotificationsScreen(
                    viewModel = viewModel,
                    notifications = notifications,
                    onBack = { navController.popBackStack() },
                    onNavigateToRoute = { route -> navController.navigate(route) }
                )
            }

            composable("submit") {
                SubmitGrievanceScreen(
                    viewModel = viewModel,
                    wards = wards,
                    onSubmitted = {
                        navController.navigate("grievances") {
                            popUpTo("submit") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable("detail") {
                val grievance = selectedGrievance
                if (grievance != null) {
                    GrievanceDetailScreen(
                        viewModel = viewModel,
                        grievance = grievance,
                        escalations = escalations,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

            composable("map") {
                MapHotspotScreen(
                    viewModel = viewModel,
                    grievances = grievances,
                    onGrievanceClick = { g ->
                        viewModel.selectGrievance(g)
                        navController.navigate("detail")
                    }
                )
            }

            composable("rti") {
                RTIScreen(
                    viewModel = viewModel,
                    rtiRequests = rtiRequests
                )
            }

            composable("profile") {
                ProfileScreen(
                    viewModel = viewModel,
                    user = currentUser,
                    onLogout = {
                        navController.navigate("auth") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("admin") {
                AdminPanelScreen(
                    viewModel = viewModel,
                    grievances = grievances,
                    projects = projects
                )
            }
        }

            // Push Notification Floating Banner Overlay
            PushNotificationBanner(
                notification = currentPushBanner,
                onDismiss = { com.example.service.NotificationService.dismissPushBanner() },
                onNotificationClick = { notif ->
                    com.example.service.NotificationService.dismissPushBanner()
                    when (notif.type) {
                        com.example.data.model.NotificationType.GEOFENCE_ALERT -> navController.navigate("map")
                        com.example.data.model.NotificationType.FLEET_DISPATCH -> navController.navigate("map")
                        com.example.data.model.NotificationType.AI_VISION_REPORT -> navController.navigate("ai_vision")
                        else -> navController.navigate("notifications")
                    }
                }
            )
        }
    }
}
}
