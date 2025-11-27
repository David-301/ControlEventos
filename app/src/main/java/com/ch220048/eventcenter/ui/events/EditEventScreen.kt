package com.ch220048.eventcenter.ui.events

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ch220048.eventcenter.data.model.CCLicense
import com.ch220048.eventcenter.data.model.Event
import com.ch220048.eventcenter.ui.auth.AuthViewModel
import com.ch220048.eventcenter.ui.components.CustomTextField
import com.ch220048.eventcenter.ui.components.LoadingDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    eventId: String,
    authViewModel: AuthViewModel,
    eventViewModel: EventViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val authUiState by authViewModel.uiState.collectAsState()
    val eventUiState by eventViewModel.uiState.collectAsState()

    val event = eventUiState.eventoActual

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("General") }
    var capacidadMaxima by remember { mutableStateOf("") }
    var selectedLicense by remember { mutableStateOf(CCLicense.CC_BY) }

    var showLicenseDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    // Cargar evento
    LaunchedEffect(eventId) {
        eventViewModel.loadEventById(eventId)
    }

    // Inicializar campos cuando se carga el evento
    LaunchedEffect(event) {
        if (event != null) {
            titulo = event.titulo
            descripcion = event.descripcion
            ubicacion = event.ubicacion
            selectedDate = event.fecha
            selectedTime = event.hora
            categoria = event.categoria
            capacidadMaxima = event.capacidadMaxima?.toString() ?: ""
            selectedLicense = CCLicense.fromCodigo(event.licenciaCC)
        }
    }

    // Errores de validación
    var tituloError by remember { mutableStateOf<String?>(null) }
    var descripcionError by remember { mutableStateOf<String?>(null) }
    var ubicacionError by remember { mutableStateOf<String?>(null) }

    val categorias = listOf(
        "General", "Deportes", "Música", "Tecnología",
        "Arte", "Educación", "Negocios", "Social", "Otro"
    )

    // Función de validación
    fun validateFields(): Boolean {
        var isValid = true

        if (titulo.isBlank()) {
            tituloError = "El título es requerido"
            isValid = false
        } else {
            tituloError = null
        }

        if (descripcion.isBlank()) {
            descripcionError = "La descripción es requerida"
            isValid = false
        } else {
            descripcionError = null
        }

        if (ubicacion.isBlank()) {
            ubicacionError = "La ubicación es requerida"
            isValid = false
        } else {
            ubicacionError = null
        }

        return isValid
    }

    // DatePicker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    // TimePicker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
        },
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Evento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (event == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Título
                    CustomTextField(
                        value = titulo,
                        onValueChange = {
                            titulo = it
                            tituloError = null
                        },
                        label = "Título del evento",
                        leadingIcon = {
                            Icon(Icons.Default.Event, contentDescription = null)
                        },
                        isError = tituloError != null,
                        errorMessage = tituloError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Descripción
                    CustomTextField(
                        value = descripcion,
                        onValueChange = {
                            descripcion = it
                            descripcionError = null
                        },
                        label = "Descripción",
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        },
                        singleLine = false,
                        maxLines = 5,
                        isError = descripcionError != null,
                        errorMessage = descripcionError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fecha
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Fecha del evento",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = selectedDate?.let {
                                        SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))
                                            .format(Date(it))
                                    } ?: "Seleccionar fecha",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hora
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { timePickerDialog.show() }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hora del evento",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = selectedTime.ifEmpty { "Seleccionar hora" },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ubicación
                    CustomTextField(
                        value = ubicacion,
                        onValueChange = {
                            ubicacion = it
                            ubicacionError = null
                        },
                        label = "Ubicación",
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                        },
                        isError = ubicacionError != null,
                        errorMessage = ubicacionError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Categoría
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryDialog = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Categoría",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = categoria,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Capacidad máxima
                    CustomTextField(
                        value = capacidadMaxima,
                        onValueChange = {
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                capacidadMaxima = it
                            }
                        },
                        label = "Capacidad máxima (opcional)",
                        leadingIcon = {
                            Icon(Icons.Default.People, contentDescription = null)
                        },
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Licencia Creative Commons
                    Text(
                        text = "Licencia Creative Commons",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLicenseDialog = true }
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
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedLicense.nombreCompleto,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = selectedLicense.codigo,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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

                    // Botón guardar cambios
                    Button(
                        onClick = {
                            if (validateFields()) {
                                val updatedEvent = event.copy(
                                    titulo = titulo.trim(),
                                    descripcion = descripcion.trim(),
                                    fecha = selectedDate!!,
                                    hora = selectedTime,
                                    ubicacion = ubicacion.trim(),
                                    categoria = categoria,
                                    capacidadMaxima = capacidadMaxima.toIntOrNull(),
                                    licenciaCC = selectedLicense.codigo
                                )

                                eventViewModel.updateEvent(updatedEvent) {
                                    onNavigateBack()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !eventUiState.isLoading
                    ) {
                        Text("Guardar Cambios", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Loading dialog
            if (eventUiState.isLoading) {
                LoadingDialog(message = "Actualizando evento...")
            }

            // Snackbar para mensajes
            if (eventUiState.errorMessage != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(eventUiState.errorMessage!!)
                }

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000)
                    eventViewModel.clearMessages()
                }
            }
        }
    }

    // Diálogo de selección de categoría
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Seleccionar Categoría") },
            text = {
                Column {
                    categorias.forEach { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    categoria = cat
                                    showCategoryDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = categoria == cat,
                                onClick = {
                                    categoria = cat
                                    showCategoryDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(cat)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Diálogo de selección de licencia
    // Diálogo de selección de licencia
    if (showLicenseDialog) {
        LicenseSelectionDialog(
            currentLicense = selectedLicense,
            onLicenseSelected = { license ->
                selectedLicense = license
                showLicenseDialog = false
            },
            onDismiss = { showLicenseDialog = false }
        )
    }

}

