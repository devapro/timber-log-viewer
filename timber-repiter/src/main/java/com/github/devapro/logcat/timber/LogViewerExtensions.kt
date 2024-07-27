package com.github.devapro.logcat.timber

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat


private const val INTENT_COMMAND = "command"

fun Context.startFloatingService(command: String = "") {

    val intent = Intent(this, LogNotificationsService::class.java)
    if (command.isNotBlank()) {
        intent.putExtra(INTENT_COMMAND, command)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.startForegroundService(intent)
    } else {
        this.startService(intent)
    }

}

fun Context.drawOverOtherAppsEnabled(): Boolean {
    return Settings.canDrawOverlays(this)
}

/**
 * Check for notification permission before starting the service so that the notification is visible
 */
fun Activity.checkAndRequestNotificationPermission(
    onPermissionsGranted: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        when (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
            android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                onPermissionsGranted()
            }
            else -> {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }
    }
}

/**
 * Check if the service is running
 */
fun Context.isMyServiceRunning(): Boolean {
    // The ACTIVITY_SERVICE is needed to retrieve a
    // ActivityManager for interacting with the global system
    // It has a constant String value "activity".
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?


    // A loop is needed to get Service information that are currently running in the System.
    // So ActivityManager.RunningServiceInfo is used. It helps to retrieve a
    // particular service information, here its this service.
    // getRunningServices() method returns a list of the services that are currently running
    // and MAX_VALUE is 2147483647. So at most this many services can be returned by this method.
    for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
        // If this service is found as a running, it will return true or else false.

        if (LogNotificationsService::class.java.canonicalName == service.service.className) {
            return true
        }
    }
    return false
}


fun Activity.requestOverlayDisplayPermission() {
    // An AlertDialog is created
    val builder = AlertDialog.Builder(this)


    // This dialog can be closed, just by
    // taping outside the dialog-box
    builder.setCancelable(true)


    // The title of the Dialog-box is set
    builder.setTitle("Screen Overlay Permission Needed")


    // The message of the Dialog-box is set
    builder.setMessage("Enable 'Display over other apps' from System Settings.")


    // The event of the Positive-Button is set
    builder.setPositiveButton(
        "Open Settings"
    ) { dialog, which -> // The app will redirect to the 'Display over other apps' in Settings.
        // This is an Implicit Intent. This is needed when any Action is needed
        // to perform, here it is
        // redirecting to an other app(Settings).
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName())
        )


        // This method will start the intent. It takes two parameter,
        // one is the Intent and the other is
        // an requestCode Integer. Here it is -1.
        startActivityForResult(intent, RESULT_OK)
    }
    builder.create().show()
}