package com.github.devapro.logcat.mobile

import android.app.Application
import com.github.devapro.logcat.timber.TimberViewerTree
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(TimberViewerTree())
    }
}