package com.ch220048.eventcenter.utils

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import kotlinx.coroutines.tasks.await

class GoogleSignInHelper(private val context: Context) {

    private val oneTapClient: SignInClient = Identity.getSignInClient(context)

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return result?.pendingIntent?.intentSender
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(getWebClientId())
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()
    }

    fun getSignedInAccountFromIntent(intent: Intent): String? {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            credential.googleIdToken
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getWebClientId(): String {
        val resourceId = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        )
        return context.getString(resourceId)
    }
}