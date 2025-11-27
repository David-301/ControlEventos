package com.ch220048.eventcenter.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ch220048.eventcenter.data.model.CCLicense
import com.ch220048.eventcenter.data.model.Comment
import com.ch220048.eventcenter.data.model.Event
import com.ch220048.eventcenter.ui.auth.AuthViewModel
import com.ch220048.eventcenter.utils.ShareHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    authViewModel: AuthViewModel,
    eventViewModel: EventViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEditEvent: (String) -> Unit,
    onNavigateToOrganizerDashboard: (String) -> Unit
) {
    val context = LocalContext.current
    val authUiState by authViewModel.uiState.collectAsState()
    val eventUiState by eventViewModel.uiState.collectAsState()

    var showCommentDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLicenseInfo by remember { mutableStateOf(false) }
    var showShareMenu by remember { mutableStateOf(false) }
    var showCopiedSnackbar by remember { mutableStateOf(false) }

    // Cargar evento al iniciar
    LaunchedEffect(eventId) {
        eventViewModel.loadEventById(eventId)
    }

    val event = eventUiState.eventoActual

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Evento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de compartir (para TODOS los usuarios)
                    if (event != null) {
                        IconButton(onClick = { showShareMenu = true }) {
                            Icon(Icons.Default.Share, contentDescription = "Compartir")
                        }

                        DropdownMenu(
                            expanded = showShareMenu,
                            onDismissRequest = { showShareMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("WhatsApp") },
                                onClick = {
                                    ShareHelper.shareOnWhatsApp(context, event)
                                    showShareMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Facebook") },
                                onClick = {
                                    ShareHelper.shareOnFacebook(context, event)
                                    showShareMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Twitter (X)") },
                                onClick = {
                                    ShareHelper.shareOnTwitter(context, event)
                                    showShareMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Tag, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Email") },
                                onClick = {
                                    ShareHelper.shareByEmail(context, event)
                                    showShareMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null)
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Más opciones") },
                                onClick = {
                                    ShareHelper.shareGeneric(context, event)
                                    showShareMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.MoreHoriz, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Copiar texto") },
                                onClick = {
                                    ShareHelper.copyToClipboard(context, event)
                                    showShareMenu = false
                                    showCopiedSnackbar = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                                }
                            )
                        }
                    }

                    // Acciones del organizador
                    if (event != null && eventViewModel.isOrganizador(event)) {
                        IconButton(onClick = { onNavigateToOrganizerDashboard(eventId) }) {
                            Icon(Icons.Default.BarChart, contentDescription = "Estadísticas")
                        }
                        IconButton(onClick = { onNavigateToEditEvent(eventId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (eventUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (event == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Evento no encontrado",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Imagen del evento
                    if (!event.imagenUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = event.imagenUrl,
                            contentDescription = "Imagen del evento",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Título
                        Text(
                            text = event.titulo,
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Categoría
                        AssistChip(
                            onClick = { },
                            label = { Text(event.categoria) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Category,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Organizador
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Organizador",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = event.organizadorNombre,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        // Fecha y hora
                        InfoRow(
                            icon = Icons.Default.CalendarToday,
                            label = "Fecha",
                            value = formatDate(event.fecha)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow(
                            icon = Icons.Default.AccessTime,
                            label = "Hora",
                            value = event.hora
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Ubicación
                        InfoRow(
                            icon = Icons.Default.LocationOn,
                            label = "Ubicación",
                            value = event.ubicacion
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Capacidad
                        if (event.capacidadMaxima != null) {
                            InfoRow(
                                icon = Icons.Default.People,
                                label = "Capacidad",
                                value = "${event.asistentes.size} / ${event.capacidadMaxima}"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        } else {
                            InfoRow(
                                icon = Icons.Default.People,
                                label = "Asistentes confirmados",
                                value = "${event.asistentes.size}"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Calificación
                        if (event.totalCalificaciones > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = String.format("%.1f", event.calificacionPromedio),
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = " (${event.totalCalificaciones} calificaciones)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        // Descripción
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = event.descripcion,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Justify
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        // Licencia Creative Commons
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { showLicenseInfo = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Copyright,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Licencia Creative Commons",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = CCLicense.fromCodigo(event.licenciaCC).nombreCompleto,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = event.licenciaCC,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón de asistencia (solo si no es organizador)
                        // Botón de asistencia (solo si no es organizador Y el evento NO ha pasado)
                        if (!eventViewModel.isOrganizador(event) && !eventViewModel.hasEventPassed(event)) {
                            if (eventViewModel.hasConfirmedAttendance(event)) {
                                OutlinedButton(
                                    onClick = { eventViewModel.cancelarAsistencia(eventId) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Asistencia Confirmada")
                                }
                            } else {
                                Button(
                                    onClick = { eventViewModel.confirmarAsistencia(eventId) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Icon(Icons.Default.EventAvailable, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Confirmar Asistencia")
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

// Mensaje si el evento ya pasó
                        if (!eventViewModel.isOrganizador(event) && eventViewModel.hasEventPassed(event)) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EventBusy,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Este evento ya finalizó",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Botón para comentar (solo si ya asistió o es organizador)
                        if (eventViewModel.hasConfirmedAttendance(event) || eventViewModel.isOrganizador(event)) {
                            OutlinedButton(
                                onClick = { showCommentDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Icon(Icons.Default.RateReview, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Dejar Comentario")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sección de comentarios
                        Text(
                            text = "Comentarios (${eventUiState.comentarios.size})",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (eventUiState.comentarios.isEmpty()) {
                            Text(
                                text = "Aún no hay comentarios para este evento",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        } else {
                            eventUiState.comentarios.forEach { comment ->
                                CommentCard(comment = comment)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            // Snackbar para texto copiado
            if (showCopiedSnackbar) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("✅ Texto copiado al portapapeles")
                }

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showCopiedSnackbar = false
                }
            }

            // Snackbar para mensajes
            if (!showCopiedSnackbar && (eventUiState.errorMessage != null || eventUiState.successMessage != null)) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(eventUiState.errorMessage ?: eventUiState.successMessage ?: "")
                }

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000)
                    eventViewModel.clearMessages()
                }
            }
        }
    }

    // Diálogo de comentario
    if (showCommentDialog && event != null && authUiState.currentUser != null) {
        CommentDialog(
            onDismiss = { showCommentDialog = false },
            onSubmit = { texto, calificacion ->
                val comment = Comment(
                    eventoId = event.id,
                    userId = authUiState.currentUser!!.id,
                    userName = authUiState.currentUser!!.nombre,
                    userPhotoUrl = authUiState.currentUser!!.photoUrl,
                    texto = texto,
                    calificacion = calificacion
                )
                eventViewModel.addComment(comment)
                showCommentDialog = false
            }
        )
    }

    // Diálogo de información de licencia
    if (showLicenseInfo && event != null) {
        val license = CCLicense.fromCodigo(event.licenciaCC)
        AlertDialog(
            onDismissRequest = { showLicenseInfo = false },
            icon = { Icon(Icons.Default.Copyright, contentDescription = null) },
            title = { Text(license.nombreCompleto) },
            text = {
                Column {
                    Text(
                        text = license.codigo,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(license.descripcion)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Este evento está bajo esta licencia Creative Commons, lo que significa que el contenido puede ser utilizado según los términos especificados.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLicenseInfo = false }) {
                    Text("Entendido")
                }
            }
        )
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && event != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Eliminar Evento") },
            text = { Text("¿Estás seguro de que deseas eliminar este evento? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        eventViewModel.deleteEvent(eventId) {
                            onNavigateBack()
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun CommentCard(comment: Comment) {
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
                // Estrellas
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatDate(comment.fecha),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CommentDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int) -> Unit
) {
    var texto by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dejar Comentario") },
        text = {
            Column {
                Text("Calificación:")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { calificacion = star }) {
                            Icon(
                                imageVector = if (star <= calificacion) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$star estrellas",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Tu comentario") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (texto.isNotBlank()) {
                        onSubmit(texto.trim(), calificacion)
                    }
                },
                enabled = texto.isNotBlank()
            ) {
                Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
    return sdf.format(Date(timestamp))
}