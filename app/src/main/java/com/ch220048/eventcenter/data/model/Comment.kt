package com.ch220048.eventcenter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey
    val id: String = "",
    val eventoId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhotoUrl: String? = null,
    val texto: String = "",
    val calificacion: Int = 0, // 1-5 estrellas
    val fecha: Long = System.currentTimeMillis(),
    val editado: Boolean = false
) : Parcelable