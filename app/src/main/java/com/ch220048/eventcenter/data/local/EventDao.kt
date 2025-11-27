package com.ch220048.eventcenter.data.local

import androidx.room.*
import com.ch220048.eventcenter.data.model.Event
import com.ch220048.eventcenter.data.model.EstadoEvento
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para eventos
 * Define todas las operaciones de base de datos para eventos
 */
@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<Event>)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: String)

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: String): Event?

    @Query("SELECT * FROM events ORDER BY fecha DESC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE estado = :estado ORDER BY fecha ASC")
    fun getEventsByEstado(estado: EstadoEvento): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE organizadorId = :organizadorId ORDER BY fecha DESC")
    fun getEventsByOrganizador(organizadorId: String): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE fecha >= :fechaActual AND estado = :estado ORDER BY fecha ASC")
    fun getProximosEventos(
        fechaActual: Long = System.currentTimeMillis(),
        estado: EstadoEvento = EstadoEvento.PROXIMO
    ): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE fecha < :fechaActual OR estado = :estado ORDER BY fecha DESC")
    fun getEventosPasados(
        fechaActual: Long = System.currentTimeMillis(),
        estado: EstadoEvento = EstadoEvento.FINALIZADO
    ): Flow<List<Event>>

    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()
}