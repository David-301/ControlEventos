package com.ch220048.eventcenter.ui.events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ch220048.eventcenter.data.local.AppDatabase
import com.ch220048.eventcenter.data.model.Comment
import com.ch220048.eventcenter.data.model.Event
import com.ch220048.eventcenter.data.repository.EventRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.ch220048.eventcenter.utils.NotificationHelper

data class EventUiState(
    val isLoading: Boolean = false,
    val eventos: List<Event> = emptyList(),
    val proximosEventos: List<Event> = emptyList(),
    val eventosPasados: List<Event> = emptyList(),
    val misEventos: List<Event> = emptyList(),
    val eventoActual: Event? = null,
    val comentarios: List<Comment> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val database = AppDatabase.getDatabase(application)
    private val repository = EventRepository(
        eventDao = database.eventDao(),
        commentDao = database.commentDao()
    )

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(EventUiState())
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    init {
        loadAllEvents()
        loadProximosEventos()
    }

    /**
     * Cargar todos los eventos
     */
    fun loadAllEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.getAllEvents()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar eventos: ${exception.message}"
                    )
                }
                .collect { eventos ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        eventos = eventos
                    )
                }
        }
    }

    /**
     * Cargar próximos eventos
     */
    fun loadProximosEventos() {
        viewModelScope.launch {
            repository.getProximosEventos()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error al cargar próximos eventos: ${exception.message}"
                    )
                }
                .collect { eventos ->
                    _uiState.value = _uiState.value.copy(
                        proximosEventos = eventos
                    )
                }
        }
    }

    /**
     * Cargar eventos pasados
     */
    fun loadEventosPasados() {
        viewModelScope.launch {
            repository.getEventosPasados()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error al cargar eventos pasados: ${exception.message}"
                    )
                }
                .collect { eventos ->
                    _uiState.value = _uiState.value.copy(
                        eventosPasados = eventos
                    )
                }
        }
    }

    /**
     * Cargar mis eventos (creados por mí)
     */
    fun loadMisEventos() {
        viewModelScope.launch {
            repository.getMisEventos()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error al cargar mis eventos: ${exception.message}"
                    )
                }
                .collect { eventos ->
                    _uiState.value = _uiState.value.copy(
                        misEventos = eventos
                    )
                }
        }
    }

    /**
     * Cargar un evento específico por ID
     */
    fun loadEventById(eventId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val event = repository.getEventById(eventId)

            if (event != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    eventoActual = event
                )

                // Cargar comentarios del evento
                loadComments(eventId)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Evento no encontrado"
                )
            }
        }
    }

    /**
     * Cargar comentarios de un evento
     */
    private fun loadComments(eventId: String) {
        viewModelScope.launch {
            repository.getCommentsByEvento(eventId)
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error al cargar comentarios: ${exception.message}"
                    )
                }
                .collect { comments ->
                    _uiState.value = _uiState.value.copy(
                        comentarios = comments
                    )
                }
        }
    }

    /**
     * Crear un nuevo evento
     */
    fun createEvent(event: Event, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.createEvent(event)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Evento creado exitosamente"
                    )
                    onSuccess()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al crear evento: ${exception.message}"
                    )
                }
            )
        }
    }

    /**
     * Actualizar un evento
     */
    fun updateEvent(event: Event, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.updateEvent(event)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Evento actualizado exitosamente"
                    )
                    onSuccess()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al actualizar evento: ${exception.message}"
                    )
                }
            )
        }
    }

    /**
     * Eliminar un evento
     */
    fun deleteEvent(eventId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.deleteEvent(eventId)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Evento eliminado exitosamente"
                    )
                    onSuccess()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al eliminar evento: ${exception.message}"
                    )
                }
            )
        }
    }

    /**
     * Confirmar asistencia a un evento
     */
    fun confirmarAsistencia(eventId: String) {
        viewModelScope.launch {
            val result = repository.confirmarAsistencia(eventId)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Asistencia confirmada"
                    )

                    // Enviar notificación al organizador
                    val event = _uiState.value.eventoActual
                    if (event != null) {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            NotificationHelper.sendNewAttendeeNotification(
                                context,
                                eventId,
                                event.titulo,
                                currentUser.displayName ?: "Alguien"
                            )
                        }
                    }

                    loadEventById(eventId)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error al confirmar asistencia: ${exception.message}"
                    )
                }
            )
        }
    }

    /**
     * Cancelar asistencia a un evento
     */
    fun cancelarAsistencia(eventId: String) {
        viewModelScope.launch {
            val result = repository.cancelarAsistencia(eventId)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Asistencia cancelada"
                    )
                    loadEventById(eventId)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error al cancelar asistencia: ${exception.message}"
                    )
                }
            )
        }
    }

    /**
     * Agregar comentario y calificación
     */
    fun addComment(comment: Comment) {
        viewModelScope.launch {
            val result = repository.addComment(comment)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Comentario agregado"
                    )

                    // Enviar notificación al organizador
                    val event = _uiState.value.eventoActual
                    if (event != null) {
                        NotificationHelper.sendNewCommentNotification(
                            context,
                            event.id,
                            event.titulo,
                            comment.userName,
                            comment.calificacion
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error al agregar comentario: ${exception.message}"
                    )
                }
            )
        }
    }

    /**
     * Verificar si el usuario actual es el organizador del evento
     */
    fun isOrganizador(event: Event): Boolean {
        return event.organizadorId == auth.currentUser?.uid
    }

    /**
     * Verificar si el usuario ya confirmó asistencia
     */
    fun hasConfirmedAttendance(event: Event): Boolean {
        return event.asistentes.contains(auth.currentUser?.uid)
    }

    /**
     * Limpiar mensajes
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    /**
     * Verificar si un evento ya pasó
     */
    fun hasEventPassed(event: Event): Boolean {
        return event.fecha < System.currentTimeMillis()
    }


}