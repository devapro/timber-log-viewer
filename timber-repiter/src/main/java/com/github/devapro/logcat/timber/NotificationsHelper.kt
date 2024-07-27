package com.github.devapro.logcat.timber

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_CHANNEL_ID = "general_notification_channel"

private const val INTENT_COMMAND = "command"
private const val INTENT_COMMAND_EXIT = "exit"
private const val INTENT_COMMAND_NOTE = "note"

private const val CODE_EXIT_INTENT = 2
private const val CODE_NOTE_INTENT = 3

internal object NotificationsHelper {

    fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create the notification channel
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_general),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Create and show the foreground notification.
     */
    fun showNotification(context: Context): Notification {
        val exitIntent = Intent(context, LogNotificationsService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_EXIT)
        }

        val noteIntent = Intent(context, LogNotificationsService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_NOTE)
        }

        val exitPendingIntent = PendingIntent.getService(
            context, CODE_EXIT_INTENT, exitIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notePendingIntent = PendingIntent.getService(
            context, CODE_NOTE_INTENT, noteIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(
            context,
            NOTIFICATION_CHANNEL_ID
        )
            .setTicker(null)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("Service is running")
            .setAutoCancel(false)
            .setOngoing(true)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(notePendingIntent)
            .addAction(
                NotificationCompat.Action(
                    0,
                    context.getString(R.string.notification_exit),
                    exitPendingIntent
                )
            )
            .build()
    }
}