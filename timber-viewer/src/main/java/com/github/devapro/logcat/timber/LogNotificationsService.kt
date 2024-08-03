package com.github.devapro.logcat.timber

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ServiceCompat
import com.github.devapro.logcat.timber.ui.LogFloatingWindow

private const val INTENT_COMMAND = "command"
private const val INTENT_COMMAND_EXIT = "exit"

internal class LogNotificationsService: Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService() = this@LogNotificationsService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private var floatingWindow: LogFloatingWindow? = null

    override fun onCreate() {
        super.onCreate()
        floatingWindow = LogFloatingWindow(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val command = intent?.getStringExtra(INTENT_COMMAND) ?: ""

        // Exit the service if we receive the EXIT command.
        // START_NOT_STICKY is important here, we don't want
        // the service to be relaunched.
        if (command == INTENT_COMMAND_EXIT) {
            stopService(intent)
            return START_NOT_STICKY
        }

        startAsForegroundService()
        showWindow()

        return super.onStartCommand(intent, flags, startId)
    }

    fun showWindow() {
        floatingWindow?.createWindow()
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingWindow?.removeWindow()
    }

    private fun startAsForegroundService() {
        // create the notification channel
        NotificationsHelper.createNotificationChannel(this)

        // promote service to foreground service
        ServiceCompat.startForeground(
            this,
            1,
            NotificationsHelper.showNotification(this),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                0
            }
        )
    }
}