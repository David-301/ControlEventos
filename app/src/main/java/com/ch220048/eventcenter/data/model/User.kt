package com.ch220048.eventcenter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val eventosCreados: List<String> = emptyList(),
    val eventosAsistidos: List<String> = emptyList(),
    val fechaRegistro: Long = System.currentTimeMillis()
) : Parcelable