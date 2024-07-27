package com.github.devapro.logcat.timber.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.devapro.logcat.timber.R
import com.github.devapro.logcat.timber.model.LogItemModel

class LogAdapter: RecyclerView.Adapter<LogItemViewHolder>() {

    private val items = mutableListOf<LogItemModel>()

    fun setItems(newItems: List<LogItemModel>) {
        val diffCallback = LogDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_log, parent, false)
        return LogItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: LogItemViewHolder, position: Int) {
        val itemModel = items[position]
        holder.bind(itemModel)
    }
}