package com.ch220048.eventcenter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "events")
data class Event(
    @PrimaryKey
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val hora: String = "",
    val ubicacion: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val organizadorId: String = "",
    val organizadorNombre: String = "",
    val imagenUrl: String? = null,
    val licenciaCC: String = "CC BY", // Licencia Creative Commons
    val categoria: String = "General",
    val capacidadMaxima: Int? = null,
    val asistentes: List<String> = emptyList(),
    val calificacionPromedio: Float = 0f,
    val totalCalificaciones: Int = 0,
    val estado: EstadoEvento = EstadoEvento.PROXIMO,
    val fechaCreacion: Long = System.currentTimeMillis()
) : Parcelable

enum class EstadoEvento {
    PROXIMO,    // Evento que aún no ha ocurrido
    EN_CURSO,   // Evento que está sucediendo ahora
    FINALIZADO, // Evento que ya pasó
    CANCELADO   // Evento cancelado
}