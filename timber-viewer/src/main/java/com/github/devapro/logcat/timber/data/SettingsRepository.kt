package com.github.devapro.logcat.timber.data

import com.github.devapro.logcat.timber.model.LogType

internal object SettingsRepository {

    private var logType: LogType? = null

    private var tagFilter: String = ""

    private var tagColumnVisibility: Boolean = true

    private var typeColumnVisibility: Boolean = true

    private var timeColumnVisibility: Boolean = true

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

    fun setTagColumnVisibility(isVisible: Boolean) {
        tagColumnVisibility = isVisible
    }

    fun getTagColumnVisibility(): Boolean {
        return tagColumnVisibility
    }

    fun setTypeColumnVisibility(isVisible: Boolean) {
        typeColumnVisibility = isVisible
    }

    fun getTypeColumnVisibility(): Boolean {
        return typeColumnVisibility
    }

    fun setTimeColumnVisibility(isVisible: Boolean) {
        timeColumnVisibility = isVisible
    }

    fun getTimeColumnVisibility(): Boolean {
        return timeColumnVisibility
    }
}