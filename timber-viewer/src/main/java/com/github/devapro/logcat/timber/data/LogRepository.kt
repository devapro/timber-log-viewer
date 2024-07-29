package com.github.devapro.logcat.timber.data

import com.github.devapro.logcat.timber.CoroutineContextProvider
import com.github.devapro.logcat.timber.model.LogItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal object LogRepository {

    private val _updates = MutableSharedFlow<Long>()
    val updates
        get() = _updates.asSharedFlow()

    private val scope = CoroutineContextProvider().createScope(
        Dispatchers.IO
    )

    private val _logsList = mutableListOf<LogItemModel>()
    val logsList: List<LogItemModel>
        get() {
            val selectedLogType = SettingsRepository.getLogType()
            val searchQuery = SettingsRepository.getTagFilter()
            return _logsList
                .filter {
                    selectedLogType == null || it.type == selectedLogType
                }
                .filter {
                    searchQuery.isEmpty() || it.tag.contains(searchQuery)
                }
        }

    fun addLog(log: LogItemModel) {
        scope.launch {
            _logsList.add(log)
            _updates.emit(System.currentTimeMillis())
        }
    }

    fun refreshLogs() {
        scope.launch {
            _updates.emit(System.currentTimeMillis())
        }
    }
}