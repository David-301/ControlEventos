package com.ch220048.eventcenter.navigation

/**
 * Define todas las rutas de navegación de la app
 */
sealed class Screen(val route: String) {
    // Autenticación
    object Login : Screen("login")
    object Register : Screen("register")

    // Principal
    object Home : Screen("home")
    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
    object OrganizerDashboard : Screen("organizer_dashboard/{eventId}") {
        fun createRoute(eventId: String) = "organizer_dashboard/$eventId"
    }
    // Eventos
    object CreateEvent : Screen("create_event")
    object EditEvent : Screen("edit_event/{eventId}") {
        fun createRoute(eventId: String) = "edit_event/$eventId"
    }
    object MyEvents : Screen("my_events")

    // Perfil
    object Profile : Screen("profile")
    object History : Screen("history")

    // Otros
    object About : Screen("about")
}