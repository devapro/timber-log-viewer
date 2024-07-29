package com.github.devapro.logcat.timber.model

internal data class LogItemModel(
    val tag: String,
    val message: String,
    val type: LogType,
    val time: Long
)