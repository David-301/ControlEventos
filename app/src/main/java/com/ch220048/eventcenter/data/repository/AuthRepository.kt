package com.ch220048.eventcenter.data.repository

import com.ch220048.eventcenter.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository para manejo de autenticación
 * Conecta Firebase Auth con nuestra app
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isUserAuthenticated: Boolean
        get() = auth.currentUser != null

    /**
     * Registro con email y contraseña
     */
    suspend fun registerWithEmail(
        nombre: String,
        email: String,
        password: String
    ): Result<User> = try {
        // Crear usuario en Firebase Auth
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("Error al crear usuario")

        // Actualizar perfil con nombre
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(nombre)
            .build()
        firebaseUser.updateProfile(profileUpdates).await()

        // Crear objeto User
        val user = User(
            id = firebaseUser.uid,
            nombre = nombre,
            email = email,
            photoUrl = firebaseUser.photoUrl?.toString()
        )

        // Guardar en Firestore
        firestore.collection("users")
            .document(firebaseUser.uid)
            .set(user)
            .await()

        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Login con email y contraseña
     */
    suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<User> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("Usuario no encontrado")

        // Obtener datos del usuario de Firestore
        val userDoc = firestore.collection("users")
            .document(firebaseUser.uid)
            .get()
            .await()

        val user = userDoc.toObject(User::class.java) ?: User(
            id = firebaseUser.uid,
            nombre = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            photoUrl = firebaseUser.photoUrl?.toString()
        )

        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Login con Google
     */
    suspend fun loginWithGoogle(idToken: String): Result<User> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val firebaseUser = result.user ?: throw Exception("Error al autenticar con Google")

        // Verificar si el usuario ya existe en Firestore
        val userDoc = firestore.collection("users")
            .document(firebaseUser.uid)
            .get()
            .await()

        val user = if (userDoc.exists()) {
            userDoc.toObject(User::class.java)!!
        } else {
            // Crear nuevo usuario
            val newUser = User(
                id = firebaseUser.uid,
                nombre = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                photoUrl = firebaseUser.photoUrl?.toString()
            )

            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(newUser)
                .await()

            newUser
        }

        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Obtener usuario actual
     */
    suspend fun getCurrentUser(): User? = try {
        val firebaseUser = auth.currentUser ?: return null

        val userDoc = firestore.collection("users")
            .document(firebaseUser.uid)
            .get()
            .await()

        userDoc.toObject(User::class.java)
    } catch (e: Exception) {
        null
    }

    /**
     * Cerrar sesión
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Enviar email de recuperación de contraseña
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}