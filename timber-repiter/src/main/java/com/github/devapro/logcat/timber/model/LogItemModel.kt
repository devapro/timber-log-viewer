package com.github.devapro.logcat.timber.model

data class LogItemModel(
    val tag: String,
    val message: String,
    val type: LogType,
    val time: Long
)