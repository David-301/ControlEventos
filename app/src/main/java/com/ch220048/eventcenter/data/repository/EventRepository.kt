package com.ch220048.eventcenter.data.repository

import com.ch220048.eventcenter.data.local.AppDatabase
import com.ch220048.eventcenter.data.local.CommentDao
import com.ch220048.eventcenter.data.local.EventDao
import com.ch220048.eventcenter.data.model.Comment
import com.ch220048.eventcenter.data.model.Event
import com.ch220048.eventcenter.data.model.EstadoEvento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository para manejo de eventos
 * Sincroniza datos entre Firebase Firestore y Room Database local
 */
class EventRepository(
    private val eventDao: EventDao,
    private val commentDao: CommentDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /**
     * Crear un nuevo evento
     */
    /**
     * Crear un nuevo evento
     */
    suspend fun createEvent(event: Event): Result<Event> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

        val eventWithId = event.copy(
            id = firestore.collection("events").document().id,
            organizadorId = userId,
            fechaCreacion = System.currentTimeMillis()
        )

        // Guardar en Firestore
        firestore.collection("events")
            .document(eventWithId.id)
            .set(eventWithId)
            .await()

        // Guardar en Room local
        eventDao.insertEvent(eventWithId)

        // Actualizar el perfil del usuario agregando el evento a eventosCreados
        val userDoc = firestore.collection("users").document(userId).get().await()
        val eventosCreados = userDoc.get("eventosCreados") as? List<String> ?: emptyList()
        val eventosCreadosActualizados = eventosCreados.toMutableList().apply { add(eventWithId.id) }

        firestore.collection("users")
            .document(userId)
            .update("eventosCreados", eventosCreadosActualizados)
            .await()

        Result.success(eventWithId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Actualizar un evento existente
     */
    suspend fun updateEvent(event: Event): Result<Event> = try {
        // Actualizar en Firestore
        firestore.collection("events")
            .document(event.id)
            .set(event)
            .await()

        // Actualizar en Room
        eventDao.updateEvent(event)

        Result.success(event)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Eliminar un evento
     */
    /**
     * Eliminar un evento
     */
    suspend fun deleteEvent(eventId: String): Result<Unit> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

        // Eliminar de Firestore
        firestore.collection("events")
            .document(eventId)
            .delete()
            .await()

        // Eliminar comentarios asociados
        firestore.collection("comments")
            .whereEqualTo("eventoId", eventId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete() }

        // Eliminar de Room
        eventDao.deleteEventById(eventId)
        commentDao.deleteCommentsByEvento(eventId)

        // Actualizar el perfil del usuario removiendo el evento de eventosCreados
        val userDoc = firestore.collection("users").document(userId).get().await()
        val eventosCreados = userDoc.get("eventosCreados") as? List<String> ?: emptyList()
        val eventosCreadosActualizados = eventosCreados.toMutableList().apply { remove(eventId) }

        firestore.collection("users")
            .document(userId)
            .update("eventosCreados", eventosCreadosActualizados)
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Obtener todos los eventos (sincronizado con Firestore)
     */
    fun getAllEvents(): Flow<List<Event>> = callbackFlow {
        val listener = firestore.collection("events")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull {
                    it.toObject(Event::class.java)
                } ?: emptyList()

                trySend(events)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Obtener eventos próximos
     */
    fun getProximosEventos(): Flow<List<Event>> = callbackFlow {
        val fechaActual = System.currentTimeMillis()

        val listener = firestore.collection("events")
            .whereGreaterThanOrEqualTo("fecha", fechaActual)
            .orderBy("fecha", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull {
                    it.toObject(Event::class.java)
                } ?: emptyList()

                trySend(events)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Obtener eventos pasados
     */
    fun getEventosPasados(): Flow<List<Event>> = callbackFlow {
        val fechaActual = System.currentTimeMillis()

        val listener = firestore.collection("events")
            .whereLessThan("fecha", fechaActual)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull {
                    it.toObject(Event::class.java)
                } ?: emptyList()

                trySend(events)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Obtener eventos creados por el usuario actual
     */
    fun getMisEventos(): Flow<List<Event>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: ""

        val listener = firestore.collection("events")
            .whereEqualTo("organizadorId", userId)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull {
                    it.toObject(Event::class.java)
                } ?: emptyList()

                trySend(events)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Obtener un evento por ID
     */
    suspend fun getEventById(eventId: String): Event? = try {
        val doc = firestore.collection("events")
            .document(eventId)
            .get()
            .await()

        doc.toObject(Event::class.java)
    } catch (e: Exception) {
        null
    }

    /**
     * Confirmar asistencia a un evento
     */
    /**
     * Confirmar asistencia a un evento
     */
    suspend fun confirmarAsistencia(eventId: String): Result<Unit> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
        val event = getEventById(eventId) ?: throw Exception("Evento no encontrado")

        val asistentesActualizados = event.asistentes.toMutableList()

        if (!asistentesActualizados.contains(userId)) {
            asistentesActualizados.add(userId)

            val eventoActualizado = event.copy(asistentes = asistentesActualizados)
            updateEvent(eventoActualizado)

            // Actualizar el perfil del usuario agregando el evento a eventosAsistidos
            val userDoc = firestore.collection("users").document(userId).get().await()
            val eventosAsistidos = userDoc.get("eventosAsistidos") as? List<String> ?: emptyList()
            val eventosAsistidosActualizados = eventosAsistidos.toMutableList().apply { add(eventId) }

            firestore.collection("users")
                .document(userId)
                .update("eventosAsistidos", eventosAsistidosActualizados)
                .await()
        }
        // Enviar notificación al organizador
        try {
            val eventDoc = firestore.collection("events").document(eventId).get().await()
            val event = eventDoc.toObject(Event::class.java)
            if (event != null) {
                val userDoc = firestore.collection("users").document(userId).get().await()
                val userName = userDoc.getString("nombre") ?: "Alguien"

                // Aquí se enviaría la notificación al organizador
                // (necesitarías el contexto, lo implementaremos en el ViewModel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Cancelar asistencia a un evento
     */
    /**
     * Cancelar asistencia a un evento
     */
    suspend fun cancelarAsistencia(eventId: String): Result<Unit> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
        val event = getEventById(eventId) ?: throw Exception("Evento no encontrado")

        val asistentesActualizados = event.asistentes.toMutableList()
        asistentesActualizados.remove(userId)

        val eventoActualizado = event.copy(asistentes = asistentesActualizados)
        updateEvent(eventoActualizado)

        // Actualizar el perfil del usuario removiendo el evento de eventosAsistidos
        val userDoc = firestore.collection("users").document(userId).get().await()
        val eventosAsistidos = userDoc.get("eventosAsistidos") as? List<String> ?: emptyList()
        val eventosAsistidosActualizados = eventosAsistidos.toMutableList().apply { remove(eventId) }

        firestore.collection("users")
            .document(userId)
            .update("eventosAsistidos", eventosAsistidosActualizados)
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Agregar comentario y calificación a un evento
     */
    suspend fun addComment(comment: Comment): Result<Comment> = try {
        val commentWithId = comment.copy(
            id = firestore.collection("comments").document().id,
            fecha = System.currentTimeMillis()
        )

        // Guardar en Firestore
        firestore.collection("comments")
            .document(commentWithId.id)
            .set(commentWithId)
            .await()

        // Guardar en Room
        commentDao.insertComment(commentWithId)

        // Actualizar calificación promedio del evento
        actualizarCalificacionEvento(comment.eventoId)

        Result.success(commentWithId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Obtener comentarios de un evento
     */
    fun getCommentsByEvento(eventoId: String): Flow<List<Comment>> = callbackFlow {
        val listener = firestore.collection("comments")
            .whereEqualTo("eventoId", eventoId)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val comments = snapshot?.documents?.mapNotNull {
                    it.toObject(Comment::class.java)
                } ?: emptyList()

                trySend(comments)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Actualizar calificación promedio de un evento
     */
    private suspend fun actualizarCalificacionEvento(eventoId: String) {
        val comments = firestore.collection("comments")
            .whereEqualTo("eventoId", eventoId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(Comment::class.java) }

        if (comments.isNotEmpty()) {
            val promedio = comments.map { it.calificacion }.average().toFloat()
            val total = comments.size

            firestore.collection("events")
                .document(eventoId)
                .update(
                    mapOf(
                        "calificacionPromedio" to promedio,
                        "totalCalificaciones" to total
                    )
                )
                .await()
        }
    }
}