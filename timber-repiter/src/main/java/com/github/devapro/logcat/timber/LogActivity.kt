package com.github.devapro.logcat.timber

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.github.devapro.logcat.timber.data.LogRepository
import com.github.devapro.logcat.timber.data.TestData
import com.github.devapro.logcat.timber.model.LogType
import com.github.devapro.logcat.timber.ui.LogAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class LogActivity : Activity() {

    private var logCollectJob: Job? = null
    private  val logAdapter = LogAdapter()
    private lateinit var logList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        logList = findViewById(R.id.log_list)
        logList.adapter = logAdapter

        val spinner = findViewById<Spinner>(R.id.log_type_spinner)
        val items = LogType.entries.map { it.name }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("ALL") + items
        )
        spinner.setAdapter(adapter)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?,
                arg1: View?,
                arg2: Int,
                arg3: Long
            ) {
                // Do what you want
                val items = spinner.selectedItem.toString()
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        val minimizeBtn = findViewById<View>(R.id.minimize_btn)
        minimizeBtn.setOnClickListener {
            mBoundService?.showWindow()
            finish()
        }
    }


    override fun onStart() {
        super.onStart()
        doBindService()
        logAdapter.setItems(LogRepository.logsList)
        logList.scrollToPosition(logAdapter.itemCount - 1)
        logCollectJob = GlobalScope.launch {
            LogRepository.logs.collect{
                launch(Dispatchers.Main) {
                    logAdapter.setItems(LogRepository.logsList)
                    logList.scrollToPosition(logAdapter.itemCount - 1)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        logCollectJob?.cancel()
        doUnbindService()
    }


    private var mBoundService: LogNotificationsService? = null
    private var mIsBound = false

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // This is called when the connection with the service has
            // been established, giving us the service object we can use
            // to interact with the service.  Because we have bound to a
            // explicit service that we know is running in our own
            // process, we can cast its IBinder to a concrete class and
            // directly access it.
            mBoundService = (service as LogNotificationsService.LocalBinder).getService()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // This is called when the connection with the service has
            // been unexpectedly disconnected -- that is, its process
            // crashed. Because it is running in our same process, we
            // should never see this happen.
            mBoundService = null
        }
    }

    private fun doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation
        // that we know will be running in our own process (and thus
        // won't be supporting component replacement by other
        // applications).
        bindService(
            Intent(this, LogNotificationsService::class.java),
            mConnection,
            BIND_AUTO_CREATE
        )
        mIsBound = true
    }

    private fun doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection)
            mIsBound = false
        }
    }

}