package com.github.devapro.logcat.timber.data

import com.github.devapro.logcat.timber.model.LogType

internal object SettingsRepository {

    private var logType: LogType? = null

    private var tagFilter: String = ""

    fun setTagFilter(tag: String) {
        tagFilter = tag
    }

    fun getTagFilter(): String {
        return tagFilter
    }

    fun setLogType(type: LogType?) {
        logType = type
    }

    fun getLogType(): LogType? {
        return logType
    }

    fun setSearchQuery(query: String) {

    }
}