package com.ch220048.eventcenter.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.ch220048.eventcenter.data.model.Event
import java.text.SimpleDateFormat
import java.util.*

object ShareHelper {

    /**
     * Genera el texto para compartir un evento
     */
    fun getShareText(event: Event): String {
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        val fecha = dateFormat.format(Date(event.fecha))

        return """
            🎉 ¡Te invito a este evento!
            
            📅 ${event.titulo}
            
            📍 ${event.ubicacion}
            🗓️ ${fecha} a las ${event.hora}
            
            ${event.descripcion}
            
            📱 Descarga EventCenter para más detalles
        """.trimIndent()
    }

    /**
     * Compartir en WhatsApp
     */
    fun shareOnWhatsApp(context: Context, event: Event) {
        val text = getShareText(event)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            `package` = "com.whatsapp"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Si WhatsApp no está instalado, usar compartir genérico
            shareGeneric(context, event)
        }
    }

    /**
     * Compartir en Facebook
     */
    fun shareOnFacebook(context: Context, event: Event) {
        val text = getShareText(event)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            `package` = "com.facebook.katana"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Si Facebook no está instalado, usar compartir genérico
            shareGeneric(context, event)
        }
    }

    /**
     * Compartir en Twitter (X)
     */
    fun shareOnTwitter(context: Context, event: Event) {
        val text = getShareText(event)
        val twitterUrl = "https://twitter.com/intent/tweet?text=${Uri.encode(text)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl))

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            shareGeneric(context, event)
        }
    }

    /**
     * Compartir por email
     */
    fun shareByEmail(context: Context, event: Event) {
        val text = getShareText(event)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_SUBJECT, "Invitación a evento: ${event.titulo}")
            putExtra(Intent.EXTRA_TEXT, text)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Enviar por email"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Compartir de forma genérica (abre el selector de Android)
     */
    fun shareGeneric(context: Context, event: Event) {
        val text = getShareText(event)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Invitación a evento: ${event.titulo}")
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Compartir evento"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Copiar al portapapeles
     */
    fun copyToClipboard(context: Context, event: Event) {
        val text = getShareText(event)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Evento", text)
        clipboard.setPrimaryClip(clip)
    }
}