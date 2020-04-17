package com.example.apps.pixo.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun onBindViewHolder(position: Int, data: T, listener: BaseAdapter.OnItemClickedListener? = null) {}
}