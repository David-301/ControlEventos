package com.ch220048.eventcenter.data.local

import androidx.room.*
import com.ch220048.eventcenter.data.model.Comment
import kotlinx.coroutines.flow.Flow

/**
 * DAO para comentarios y calificaciones
 */
@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<Comment>)

    @Update
    suspend fun updateComment(comment: Comment)

    @Delete
    suspend fun deleteComment(comment: Comment)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteCommentById(commentId: String)

    @Query("SELECT * FROM comments WHERE eventoId = :eventoId ORDER BY fecha DESC")
    fun getCommentsByEvento(eventoId: String): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE userId = :userId ORDER BY fecha DESC")
    fun getCommentsByUser(userId: String): Flow<List<Comment>>

    @Query("SELECT AVG(calificacion) FROM comments WHERE eventoId = :eventoId")
    suspend fun getCalificacionPromedio(eventoId: String): Float?

    @Query("SELECT COUNT(*) FROM comments WHERE eventoId = :eventoId")
    suspend fun getTotalComentarios(eventoId: String): Int

    @Query("DELETE FROM comments WHERE eventoId = :eventoId")
    suspend fun deleteCommentsByEvento(eventoId: String)

    @Query("DELETE FROM comments")
    suspend fun deleteAllComments()
}