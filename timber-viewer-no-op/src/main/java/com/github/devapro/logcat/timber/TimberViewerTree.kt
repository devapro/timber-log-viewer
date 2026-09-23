package com.github.devapro.logcat.timber

import timber.log.Timber

class TimberViewerTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) = Unit
}