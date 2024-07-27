package com.github.devapro.logcat.timber

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.github.devapro.logcat.timber.data.TestData
import com.github.devapro.logcat.timber.model.LogType
import com.github.devapro.logcat.timber.ui.LogAdapter

class LogActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val logAdapter = LogAdapter()
        val logList = findViewById<RecyclerView>(R.id.log_list)
        logList.adapter = logAdapter
        logAdapter.setItems(TestData.logs)

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

            finish()
        }
    }

}