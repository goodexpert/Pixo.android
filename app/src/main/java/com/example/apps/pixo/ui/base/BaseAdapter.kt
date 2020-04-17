package com.example.apps.pixo.ui.base

import android.app.Activity
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.example.apps.pixo.util.AndroidDisposable

abstract class BaseAdapter<T : Any>(private val activity: Activity) : RecyclerView.Adapter<BaseViewHolder<T>>() {
    private val items: MutableList<T> by lazy { mutableListOf<T>() }
    private val layoutInflater: LayoutInflater by lazy { LayoutInflater.from(activity) }
    private val androidDisposable: AndroidDisposable by lazy { AndroidDisposable() }

    private var listener: OnItemClickedListener? = null

    protected fun layoutInflater(): LayoutInflater =
        layoutInflater

    fun getDisposable(): AndroidDisposable {
        return androidDisposable
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.onBindViewHolder(position, items[position], listener)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<T>) {
        super.onViewDetachedFromWindow(holder)
        getDisposable().dispose()
    }

    fun getItem(position: Int): T? {
        if (position < 0 || position >= this.items.count()) return null
        return this.items[position]
    }

    fun setItems(items: List<T>?) {
        this.items.clear()
        items?.let {
            this.items.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    fun setOnItemClickedListener(listener: OnItemClickedListener) {
        this.listener = listener
    }

    fun setOnItemClickedListener(listener: (position: Int, data: Any) -> Unit) {
        setOnItemClickedListener(object :OnItemClickedListener {
            override fun onItemClicked(position: Int, data: Any) {
                listener(position, data)
            }
        })
    }

    interface OnItemClickedListener {
        fun onItemClicked(position: Int, data: Any)
    }
}