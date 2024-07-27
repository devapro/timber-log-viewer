package com.github.devapro.logcat.mobile

import android.app.Application
import com.github.devapro.logcat.timber.TimberInterceptor
import com.github.devapro.logcat.timber.startFloatingService
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(TimberInterceptor())
    }
}