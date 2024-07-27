package com.github.devapro.logcat.timber.ui

import androidx.recyclerview.widget.DiffUtil
import com.github.devapro.logcat.timber.model.LogItemModel

class LogDiffCallback(
    private val oldList: List<LogItemModel>,
    private val newList: List<LogItemModel>
) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].time == newList[newItemPosition].time
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
}