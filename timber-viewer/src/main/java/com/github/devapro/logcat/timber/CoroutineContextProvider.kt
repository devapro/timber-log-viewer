package com.github.devapro.logcat.timber

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

internal class CoroutineContextProvider {

    fun createScope(
        context: CoroutineContext
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob(null) + context)
    }
}