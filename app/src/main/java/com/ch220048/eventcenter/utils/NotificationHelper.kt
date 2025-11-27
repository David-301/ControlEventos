package com.ch220048.eventcenter.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ch220048.eventcenter.MainActivity
import com.ch220048.eventcenter.R

object NotificationHelper {

    private const val CHANNEL_ID = "event_notifications"
    private const val CHANNEL_NAME = "Notificaciones de Eventos"
    private const val CHANNEL_DESCRIPTION = "Notificaciones sobre tus eventos"

    /**
     * Crea el canal de notificaciones (necesario para Android 8.0+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Solicitar permiso de notificaciones (Android 13+)
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Enviar notificación de recordatorio de evento
     */
    fun sendEventReminderNotification(
        context: Context,
        eventId: String,
        eventTitle: String,
        eventDate: String,
        eventTime: String
    ) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("eventId", eventId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⏰ Recordatorio de Evento")
            .setContentText("$eventTitle - $eventDate a las $eventTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(eventId.hashCode(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Enviar notificación de nuevo asistente
     */
    fun sendNewAttendeeNotification(
        context: Context,
        eventId: String,
        eventTitle: String,
        attendeeName: String
    ) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("eventId", eventId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🎉 Nuevo asistente")
            .setContentText("$attendeeName confirmó asistencia a \"$eventTitle\"")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                (eventId + attendeeName).hashCode(),
                notification
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Enviar notificación de nuevo comentario
     */
    fun sendNewCommentNotification(
        context: Context,
        eventId: String,
        eventTitle: String,
        commenterName: String,
        rating: Int
    ) {
        if (!hasNotificationPermission(context)) return

        val stars = "⭐".repeat(rating)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("eventId", eventId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("💬 Nuevo comentario")
            .setContentText("$commenterName comentó en \"$eventTitle\" $stars")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                (eventId + commenterName + System.currentTimeMillis()).hashCode(),
                notification
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Programar recordatorio de evento (1 día antes)
     */
    fun scheduleEventReminder(
        context: Context,
        eventId: String,
        eventTitle: String,
        eventDate: String,
        eventTime: String,
        eventTimestamp: Long
    ) {
        // Calcular 1 día antes del evento
        val oneDayBefore = eventTimestamp - (24 * 60 * 60 * 1000)
        val now = System.currentTimeMillis()

        // Solo programar si el evento es en el futuro
        if (oneDayBefore > now) {
            // Aquí usarías WorkManager o AlarmManager para programar
            // Por simplicidad, simulamos el recordatorio inmediatamente si está cercano
            if ((oneDayBefore - now) < (60 * 60 * 1000)) { // Menos de 1 hora
                sendEventReminderNotification(context, eventId, eventTitle, eventDate, eventTime)
            }
        }
    }
}