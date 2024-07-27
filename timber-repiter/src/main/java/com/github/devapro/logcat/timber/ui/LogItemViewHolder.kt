package com.github.devapro.logcat.timber.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.devapro.logcat.timber.R
import com.github.devapro.logcat.timber.model.LogItemModel
import com.github.devapro.logcat.timber.model.LogType

internal class LogItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val logType: TextView = itemView.findViewById(R.id.log_type)
    private val logTag: TextView = itemView.findViewById(R.id.log_tag)
    private val logMessage: TextView = itemView.findViewById(R.id.log_message)

    fun bind(logItemModel: LogItemModel) {
        logType.text = logItemModel.type.symbol
        when (logItemModel.type) {
            LogType.VERBOSE -> logType.setBackgroundResource(R.color.bg_log_verbose)
            LogType.DEBUG -> logType.setBackgroundResource(R.color.bg_log_debug)
            LogType.INFO -> logType.setBackgroundResource(R.color.bg_log_info)
            LogType.WARN -> logType.setBackgroundResource(R.color.bg_log_warn)
            LogType.ERROR -> logType.setBackgroundResource(R.color.bg_log_error)
        }
        logTag.text = logItemModel.tag
        logMessage.text = logItemModel.message
    }
}