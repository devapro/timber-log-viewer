package com.github.devapro.logcat.timber

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.github.devapro.logcat.timber.data.LogRepository
import com.github.devapro.logcat.timber.data.SettingsRepository
import com.github.devapro.logcat.timber.model.LogType
import com.github.devapro.logcat.timber.ui.LogAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


internal class LogActivity : Activity() {

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

        var selectedIndex = LogType.entries.indexOf(SettingsRepository.getLogType())
        if (selectedIndex < 0) {
            selectedIndex = 0
        } else {
            selectedIndex++
        }
        spinner.setSelection(selectedIndex)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?,
                arg1: View?,
                arg2: Int,
                arg3: Long
            ) {
                SettingsRepository.setLogType(
                    LogType.entries.firstOrNull { it.name == spinner.selectedItem.toString() }
                )
                LogRepository.refreshLogs()
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        val minimizeBtn = findViewById<View>(R.id.minimize_btn)
        minimizeBtn.setOnClickListener {
            mBoundService?.showWindow()
            finish()
        }

        val tagSearch = findViewById<EditText>(R.id.tag_search)
        tagSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        tagSearch.setText(SettingsRepository.getTagFilter())
        tagSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId ==  EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE) {
                SettingsRepository.setTagFilter(v.text.toString())
                v.clearFocus()
                hideKeyBoard(v.windowToken)
                LogRepository.refreshLogs()
            }
            true
        }
    }

    private fun hideKeyBoard(windowToken: IBinder) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


    override fun onStart() {
        super.onStart()
        doBindService()
        logAdapter.setItems(LogRepository.logsList)
        logList.scrollToPosition(logAdapter.itemCount - 1)
        logCollectJob = GlobalScope.launch {
            LogRepository.updates.collect{
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