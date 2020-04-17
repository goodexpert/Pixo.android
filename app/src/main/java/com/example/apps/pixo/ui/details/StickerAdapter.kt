package com.example.apps.pixo.ui.details

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.apps.pixo.R
import com.example.apps.pixo.ui.base.BaseAdapter
import com.example.apps.pixo.ui.base.BaseViewHolder

class StickerAdapter(private val activity: Activity) : BaseAdapter<Int>(activity) {
    private val thumbnailSize: Int by lazy {
        activity.resources.getDimensionPixelSize(R.dimen.thumbnail_size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Int> {
        val view = layoutInflater().inflate(R.layout.view_sticker_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder : BaseViewHolder<Int> {
        private val image: ImageView

        constructor(itemView: View) : super(itemView) {
            this.image = itemView.findViewById(R.id.image)
        }

        override fun onBindViewHolder(position: Int, data: Int, listener: OnItemClickedListener?) {
            this.itemView.setOnClickListener {
                listener?.onItemClicked(position, data)
            }
            image.setImageResource(data)
        }
    }
}