package com.github.devapro.logcat.timber.model

internal enum class LogType(val symbol: String) {
    VERBOSE("V"),
    DEBUG("D"),
    INFO("I"),
    WARN("W"),
    ERROR("E")
}