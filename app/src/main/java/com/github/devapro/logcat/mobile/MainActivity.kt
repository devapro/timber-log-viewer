package com.github.devapro.logcat.mobile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.github.devapro.logcat.timber.checkAndRequestNotificationPermission
import com.github.devapro.logcat.timber.drawOverOtherAppsEnabled
import com.github.devapro.logcat.timber.requestOverlayDisplayPermission
import com.github.devapro.logcat.timber.startFloatingService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var logsJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkAndRequestNotificationPermission {
            if (drawOverOtherAppsEnabled()) {
                startFloatingService()
            } else {
                requestOverlayDisplayPermission()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        logsJob = lifecycleScope.launch {
            while (true) {
                Timber.e("Logcat message")
                delay(1000)
                Timber.i("Logcat message")
                delay(1000)
                Timber.i(
                    "Logcat message with a very long text that should be truncated to fit the screen"
                )
                delay(1000)
                Timber.w("Logcat message")
                Timber.tag("CustomTag").d("Logcat message")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        logsJob?.cancel()
    }
}