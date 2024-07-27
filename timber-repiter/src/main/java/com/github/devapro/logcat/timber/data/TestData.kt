package com.github.devapro.logcat.timber.data

import com.github.devapro.logcat.timber.model.LogItemModel
import com.github.devapro.logcat.timber.model.LogType

object TestData {

    val logs = listOf(
        LogItemModel("TAG", "Message", LogType.VERBOSE, System.currentTimeMillis()),
        LogItemModel("TAG", "Message", LogType.DEBUG, System.currentTimeMillis()),
        LogItemModel("TAG", "Message", LogType.INFO, System.currentTimeMillis()),
        LogItemModel("TAG", "Message", LogType.WARN, System.currentTimeMillis()),
        LogItemModel("TAG", "Message", LogType.ERROR, System.currentTimeMillis())
    )
}