package com.github.devapro.logcat.timber

import android.app.Activity
import android.os.Bundle

class LogActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
    }

}