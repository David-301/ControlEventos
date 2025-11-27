package com.ch220048.eventcenter.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ch220048.eventcenter.ui.auth.AuthViewModel
import com.ch220048.eventcenter.ui.auth.LoginScreen
import com.ch220048.eventcenter.ui.auth.RegisterScreen
import com.ch220048.eventcenter.ui.events.CreateEventScreen
import com.ch220048.eventcenter.ui.events.EventDetailScreen
import com.ch220048.eventcenter.ui.events.EventViewModel
import com.ch220048.eventcenter.ui.events.HistoryScreen
import com.ch220048.eventcenter.ui.events.HomeScreen
import com.ch220048.eventcenter.ui.events.MyEventsScreen
import com.ch220048.eventcenter.ui.events.OrganizerDashboardScreen
import com.ch220048.eventcenter.ui.profile.AboutScreen
import com.ch220048.eventcenter.ui.profile.ProfileScreen
import com.ch220048.eventcenter.ui.events.EditEventScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    // ViewModels compartidos
    val authViewModel: AuthViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()

    // Observar estado de autenticación para navegar automáticamente
    val authUiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authUiState.isAuthenticated) {
        if (!authUiState.isAuthenticated && navController.currentDestination?.route != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Registro
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla Principal (Home)
        composable(Screen.Home.route) {
            HomeScreen(
                authViewModel = authViewModel,
                eventViewModel = eventViewModel,
                onNavigateToEventDetail = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                },
                onNavigateToCreateEvent = {
                    navController.navigate(Screen.CreateEvent.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToMyEvents = {
                    navController.navigate(Screen.MyEvents.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        // Crear Evento
        composable(Screen.CreateEvent.route) {
            CreateEventScreen(
                authViewModel = authViewModel,
                eventViewModel = eventViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Detalle del evento
        composable(Screen.EventDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                authViewModel = authViewModel,
                eventViewModel = eventViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditEvent = { eventId ->
                    navController.navigate("edit_event/$eventId")
                },
                onNavigateToOrganizerDashboard = { eventId ->
                    navController.navigate(Screen.OrganizerDashboard.createRoute(eventId))
                }
            )
        }

        // Editar Evento
        composable("edit_event/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EditEventScreen(
                eventId = eventId,
                authViewModel = authViewModel,
                eventViewModel = eventViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Panel de Organizador
        composable(Screen.OrganizerDashboard.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            OrganizerDashboardScreen(
                eventId = eventId,
                eventViewModel = eventViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Perfil
        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Mis Eventos
        composable(Screen.MyEvents.route) {
            MyEventsScreen(
                eventViewModel = eventViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEventDetail = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                },
                onNavigateToCreateEvent = {
                    navController.navigate(Screen.CreateEvent.route)
                }
            )
        }

        // Historial
        composable(Screen.History.route) {
            HistoryScreen(
                eventViewModel = eventViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEventDetail = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                }
            )
        }

        // Acerca de
        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

// Placeholder temporal para pantallas que implementaremos después
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Pantalla de $title\n(En desarrollo)",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}