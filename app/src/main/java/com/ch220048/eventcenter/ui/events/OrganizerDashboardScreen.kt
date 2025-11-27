package com.ch220048.eventcenter.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ch220048.eventcenter.data.model.Event
import com.ch220048.eventcenter.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerDashboardScreen(
    eventId: String,
    eventViewModel: EventViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by eventViewModel.uiState.collectAsState()
    val event = uiState.eventoActual

    var asistentes by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoadingAsistentes by remember { mutableStateOf(false) }

    // Cargar evento y asistentes
    LaunchedEffect(eventId) {
        eventViewModel.loadEventById(eventId)
    }

    LaunchedEffect(event) {
        if (event != null && event.asistentes.isNotEmpty()) {
            isLoadingAsistentes = true
            asistentes = loadAttendees(event.asistentes)
            isLoadingAsistentes = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Organizador") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (event == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header del evento
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = event.titulo,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatDate(event.fecha) + " - " + event.hora,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Estadísticas principales
                Text(
                    text = "Estadísticas del Evento",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.People,
                        title = "Asistentes",
                        value = event.asistentes.size.toString(),
                        subtitle = if (event.capacidadMaxima != null) {
                            "de ${event.capacidadMaxima}"
                        } else "confirmados",
                        color = MaterialTheme.colorScheme.primaryContainer
                    )

                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Comment,
                        title = "Comentarios",
                        value = event.totalCalificaciones.toString(),
                        subtitle = "recibidos",
                        color = MaterialTheme.colorScheme.secondaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Star,
                        title = "Calificación",
                        value = if (event.totalCalificaciones > 0) {
                            String.format("%.1f", event.calificacionPromedio)
                        } else "N/A",
                        subtitle = "promedio",
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )

                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Percent,
                        title = "Ocupación",
                        value = if (event.capacidadMaxima != null) {
                            "${(event.asistentes.size * 100 / event.capacidadMaxima)}%"
                        } else "N/A",
                        subtitle = "capacidad",
                        color = MaterialTheme.colorScheme.errorContainer
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))

                // Lista de asistentes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Asistentes Confirmados",
                        style = MaterialTheme.typography.titleLarge
                    )

                    if (event.asistentes.isNotEmpty()) {
                        AssistChip(
                            onClick = { },
                            label = { Text("${event.asistentes.size}") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoadingAsistentes) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (asistentes.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Aún no hay asistentes confirmados",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    asistentes.forEach { attendee ->
                        AttendeeCard(attendee = attendee)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Comentarios recientes
                if (uiState.comentarios.isNotEmpty()) {
                    Text(
                        text = "Comentarios Recientes",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.comentarios.take(3).forEach { comment ->
                        CommentCard(comment = comment)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (uiState.comentarios.size > 3) {
                        TextButton(
                            onClick = { /* TODO: Ver todos los comentarios */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver todos los comentarios (${uiState.comentarios.size})")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
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
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AttendeeCard(attendee: User) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = attendee.nombre.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attendee.nombre,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = attendee.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Confirmado",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CommentCard(comment: com.ch220048.eventcenter.data.model.Comment) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.titleSmall
                )
                Row {
                    repeat(comment.calificacion) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.texto,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private suspend fun loadAttendees(attendeeIds: List<String>): List<User> {
    val firestore = FirebaseFirestore.getInstance()
    val users = mutableListOf<User>()

    try {
        attendeeIds.forEach { userId ->
            val doc = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            doc.toObject(User::class.java)?.let { users.add(it) }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return users
}

private fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("es", "ES"))
    return sdf.format(java.util.Date(timestamp))
}