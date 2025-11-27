package com.ch220048.eventcenter.ui.events

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ch220048.eventcenter.data.model.Event
import com.ch220048.eventcenter.ui.auth.AuthViewModel
import com.ch220048.eventcenter.ui.components.EventCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    eventViewModel: EventViewModel = viewModel(),
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMyEvents: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val uiState by eventViewModel.uiState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Todos") }

    // Recargar datos cada vez que se muestra la pantalla
    DisposableEffect(Unit) {
        eventViewModel.loadAllEvents()
        eventViewModel.loadProximosEventos()
        eventViewModel.loadMisEventos()

        onDispose { }
    }

    // También recargar cuando cambia el usuario autenticado
    LaunchedEffect(authUiState.currentUser?.id) {
        if (authUiState.currentUser != null) {
            eventViewModel.loadMisEventos()
        }
    }

    // Filtrar eventos según la categoría seleccionada
    val eventosFiltrados = remember(uiState.proximosEventos, selectedFilter) {
        if (selectedFilter == "Todos") {
            uiState.proximosEventos
        } else {
            uiState.proximosEventos.filter { it.categoria == selectedFilter }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateEvent,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nuevo Evento") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header con gradiente y bienvenida
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Comunidad",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (authUiState.currentUser != null)
                                        "¡Hola, ${authUiState.currentUser!!.nombre}! 👋"
                                    else "EventCenter",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Box para anclar el menú al botón
                            Box {
                                IconButton(
                                    onClick = { showMenu = true },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface)
                                ) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "Menú",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Mi Perfil") },
                                        onClick = {
                                            showMenu = false
                                            onNavigateToProfile()
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Person, contentDescription = null)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Mis Eventos") },
                                        onClick = {
                                            showMenu = false
                                            onNavigateToMyEvents()
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.EventNote, contentDescription = null)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Historial") },
                                        onClick = {
                                            showMenu = false
                                            onNavigateToHistory()
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.History, contentDescription = null)
                                        }
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Cerrar Sesión") },
                                        onClick = {
                                            showMenu = false
                                            authViewModel.signOut()
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Logout, contentDescription = null)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tarjetas de acceso rápido
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickAccessCard(
                                icon = Icons.Default.EventNote,
                                title = "Mis Eventos",
                                subtitle = "${uiState.misEventos.size}",
                                onClick = onNavigateToMyEvents,
                                modifier = Modifier.weight(1f),
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer
                            )

                            QuickAccessCard(
                                icon = Icons.Default.History,
                                title = "Historial",
                                subtitle = "Ver",
                                onClick = onNavigateToHistory,
                                modifier = Modifier.weight(1f),
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                            )

                            QuickAccessCard(
                                icon = Icons.Default.Person,
                                title = "Perfil",
                                subtitle = "Editar",
                                onClick = onNavigateToProfile,
                                modifier = Modifier.weight(1f),
                                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        }
                    }
                }
            }

            // Filtros por categoría
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "Categorías",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categorias = listOf(
                            "Todos",
                            "General",
                            "Deportes",
                            "Música",
                            "Tecnología",
                            "Arte",
                            "Educación",
                            "Negocios",
                            "Social"
                        )

                        items(categorias) { categoria ->
                            FilterChip(
                                selected = selectedFilter == categoria,
                                onClick = { selectedFilter = categoria },
                                label = { Text(categoria) },
                                leadingIcon = if (selectedFilter == categoria) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Título de próximos eventos
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Próximos Eventos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${eventosFiltrados.size} eventos disponibles",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Lista de eventos
            when {
                uiState.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                eventosFiltrados.isEmpty() -> {
                    item {
                        ModernEmptyState(
                            message = if (selectedFilter == "Todos")
                                "No hay eventos próximos"
                            else
                                "No hay eventos de $selectedFilter",
                            icon = Icons.Default.EventBusy
                        )
                    }
                }

                else -> {
                    items(eventosFiltrados) { event ->
                        ModernEventCard(
                            event = event,
                            onClick = { onNavigateToEventDetail(event.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Espaciado final para el FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Snackbar para mensajes
        AnimatedVisibility(
            visible = uiState.errorMessage != null || uiState.successMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Snackbar {
                    Text(uiState.errorMessage ?: uiState.successMessage ?: "")
                }
            }

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                eventViewModel.clearMessages()
            }
        }
    }
}

@Composable
private fun QuickAccessCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernEventCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Indicador de fecha en el lado izquierdo
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = event.fecha
                    }
                    Text(
                        text = calendar.get(Calendar.DAY_OF_MONTH).toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = SimpleDateFormat("MMM", Locale("es", "ES")).format(Date(event.fecha)).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Contenido del evento
            Column(modifier = Modifier.weight(1f)) {
                // Categoría
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = event.categoria,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.height(24.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = getCategoryColor(event.categoria)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Título
                Text(
                    text = event.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Hora y ubicación
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.hora,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.ubicacion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Footer: Asistentes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${event.asistentes.size} asistentes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (event.totalCalificaciones > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = String.format("%.1f", event.calificacionPromedio),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Icono de flecha
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun ModernEmptyState(
    message: String,
    icon: ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Crea un evento para empezar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun getCategoryColor(categoria: String): Color {
    return when (categoria) {
        "Deportes" -> Color(0xFFE3F2FD)
        "Música" -> Color(0xFFFCE4EC)
        "Tecnología" -> Color(0xFFE8F5E9)
        "Arte" -> Color(0xFFFFF3E0)
        "Educación" -> Color(0xFFF3E5F5)
        "Negocios" -> Color(0xFFE0F2F1)
        "Social" -> Color(0xFFFFF9C4)
        else -> Color(0xFFEEEEEE)
    }
}