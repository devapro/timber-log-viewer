package com.github.devapro.logcat.timber.data

import com.github.devapro.logcat.timber.model.LogItemModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal object LogRepository {
    
    private val _logs = MutableSharedFlow<LogItemModel>()
    val logs
        get() = _logs.asSharedFlow()


    val logsList = mutableListOf<LogItemModel>()

    fun addLog(log: LogItemModel) {
        GlobalScope.launch {
            logsList.add(log)
            _logs.emit(log)
        }
    }
}