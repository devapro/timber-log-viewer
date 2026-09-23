package com.github.devapro.logcat.timber

import android.util.Log
import com.github.devapro.logcat.timber.data.LogRepository
import com.github.devapro.logcat.timber.model.LogItemModel
import com.github.devapro.logcat.timber.model.LogType
import timber.log.Timber

class TimberViewerTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        LogRepository.addLog(
            LogItemModel(
                type = mapPriority(priority),
                tag = tag.orEmpty(),
                message = message,
                time = System.currentTimeMillis()
            )
        )
    }

    private fun mapPriority(priority: Int): LogType {
        return when (priority) {
            Log.VERBOSE -> LogType.VERBOSE
            Log.DEBUG -> LogType.DEBUG
            Log.INFO -> LogType.INFO
            Log.WARN -> LogType.WARN
            Log.ERROR -> LogType.ERROR
            Log.ASSERT -> LogType.ERROR
            else -> LogType.INFO
        }
    }
}